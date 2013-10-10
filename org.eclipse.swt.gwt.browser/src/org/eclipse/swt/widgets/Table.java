/*******************************************************************************
 * Copyright (c) 2000, 2011, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gerhardt Informatics Kft. - GEFGWT port
 *******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.gwt.GdkColor;

/**
 * Instances of this class implement a selectable user interface object that
 * displays a list of images and strings and issues notification when selected.
 * <p>
 * The item children that may be added to instances of this class must be of
 * type <code>TableItem</code>.
 * </p>
 * <p>
 * Style <code>VIRTUAL</code> is used to create a <code>Table</code> whose
 * <code>TableItem</code>s are to be populated by the client on an on-demand
 * basis instead of up-front. This can provide significant performance
 * improvements for tables that are very large or for which
 * <code>TableItem</code> population is expensive (for example, retrieving
 * values from an external source).
 * </p>
 * <p>
 * Here is an example of using a <code>Table</code> with style
 * <code>VIRTUAL</code>: <code><pre>
 *  final Table table = new Table (parent, SWT.VIRTUAL | SWT.BORDER);
 *  table.setItemCount (1000000);
 *  table.addListener (SWT.SetData, new Listener () {
 *      public void handleEvent (Event event) {
 *          TableItem item = (TableItem) event.item;
 *          int index = table.indexOf (item);
 *          item.setText ("Item " + index);
 *          System.out.println (item.getText ());
 *      }
 *  }); 
 * </pre></code>
 * </p>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it
 * does not normally make sense to add <code>Control</code> children to it, or
 * set a layout on it, unless implementing something like a cell editor.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, CHECK, FULL_SELECTION, HIDE_SELECTION, VIRTUAL, NO_SCROLL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection, SetData, MeasureItem, EraseItem, PaintItem</dd>
 * </dl>
 * </p>
 * <p>
 * Note: Only one of the styles SINGLE, and MULTI may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/snippets/#table">Table, TableItem,
 *      TableColumn snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Table extends Composite {
	long /* int */modelHandle, checkRenderer;
	int itemCount, columnCount, lastIndexOf, sortDirection;
	long /* int */ignoreCell;
	TableItem[] items;
	TableColumn[] columns;
	TableItem currentItem;
	TableColumn sortColumn;
	ImageList imageList, headerImageList;
	boolean firstCustomDraw;
	int drawState, drawFlags;
	GdkColor drawForeground;
	boolean ownerDraw, ignoreSize, ignoreAccessibility;

	static final int CHECKED_COLUMN = 0;
	static final int GRAYED_COLUMN = 1;
	static final int FOREGROUND_COLUMN = 2;
	static final int BACKGROUND_COLUMN = 3;
	static final int FONT_COLUMN = 4;
	static final int FIRST_COLUMN = FONT_COLUMN + 1;
	static final int CELL_PIXBUF = 0;
	static final int CELL_TEXT = 1;
	static final int CELL_FOREGROUND = 2;
	static final int CELL_BACKGROUND = 3;
	static final int CELL_FONT = 4;
	static final int CELL_TYPES = CELL_FONT + 1;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must
	 * be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * 
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 * 
	 * @see SWT#SINGLE
	 * @see SWT#MULTI
	 * @see SWT#CHECK
	 * @see SWT#FULL_SELECTION
	 * @see SWT#HIDE_SELECTION
	 * @see SWT#VIRTUAL
	 * @see SWT#NO_SCROLL
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public Table(Composite parent, int style) {
		super(parent, checkStyle(style));
	}

	void _addListener(int eventType, Listener listener) {
		super._addListener(eventType, listener);
		if (!ownerDraw) {
			switch (eventType) {
			case SWT.MeasureItem:
			case SWT.EraseItem:
			case SWT.PaintItem:
				ownerDraw = true;
				recreateRenderers();
				break;
			}
		}
	}

	TableItem _getItem(int index) {
		if ((style & SWT.VIRTUAL) == 0)
			return items[index];
		if (items[index] != null)
			return items[index];
		return items[index] = new TableItem(this, SWT.NONE, index, false);
	}

	static int checkStyle(int style) {
		/*
		 * Feature in Windows. Even when WS_HSCROLL or WS_VSCROLL is not
		 * specified, Windows creates trees and tables with scroll bars. The fix
		 * is to set H_SCROLL and V_SCROLL.
		 * 
		 * NOTE: This code appears on all platforms so that applications have
		 * consistent scroll bar behavior.
		 */
		if ((style & SWT.NO_SCROLL) == 0) {
			style |= SWT.H_SCROLL | SWT.V_SCROLL;
		}
		/* GTK is always FULL_SELECTION */
		style |= SWT.FULL_SELECTION;
		return checkBits(style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0);
	}

	boolean checkData(TableItem item) {
		return true;
	}

	protected void checkSubclass() {
		if (!isValidSubclass())
			error(SWT.ERROR_INVALID_SUBCLASS);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the user changes the receiver's selection, by sending it one of the
	 * messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * When <code>widgetSelected</code> is called, the item field of the event
	 * object is valid. If the receiver has the <code>SWT.CHECK</code> style and
	 * the check selection changes, the event object detail field contains the
	 * value <code>SWT.CHECK</code>. <code>widgetDefaultSelected</code> is
	 * typically called when an item is double-clicked. The item field of the
	 * event object is valid for default selection, but the detail field is not
	 * used.
	 * </p>
	 * 
	 * @param listener
	 *            the listener which should be notified when the user changes
	 *            the receiver's selection
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	/**
	 * Clears the item at the given zero-relative index in the receiver. The
	 * text, icon and other attributes of the item are set to the default value.
	 * If the table was created with the <code>SWT.VIRTUAL</code> style, these
	 * attributes are requested again as needed.
	 * 
	 * @param index
	 *            the index of the item to clear
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0
	 *                and the number of elements in the list minus 1 (inclusive)
	 *                </li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 * 
	 * @since 3.0
	 */
	public void clear(int index) {
		checkWidget();
		if (!(0 <= index && index < itemCount)) {
			error(SWT.ERROR_INVALID_RANGE);
		}
		TableItem item = items[index];
		if (item != null)
			item.clear();
	}

	/**
	 * Removes the items from the receiver which are between the given
	 * zero-relative start and end indices (inclusive). The text, icon and other
	 * attributes of the items are set to their default values. If the table was
	 * created with the <code>SWT.VIRTUAL</code> style, these attributes are
	 * requested again as needed.
	 * 
	 * @param start
	 *            the start index of the item to clear
	 * @param end
	 *            the end index of the item to clear
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if either the start or end are
	 *                not between 0 and the number of elements in the list minus
	 *                1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 * 
	 * @since 3.0
	 */
	public void clear(int start, int end) {
		checkWidget();
		if (start > end)
			return;
		if (!(0 <= start && start <= end && end < itemCount)) {
			error(SWT.ERROR_INVALID_RANGE);
		}
		if (start == 0 && end == itemCount - 1) {
			clearAll();
		} else {
			for (int i = start; i <= end; i++) {
				TableItem item = items[i];
				if (item != null)
					item.clear();
			}
		}
	}

	/**
	 * Clears the items at the given zero-relative indices in the receiver. The
	 * text, icon and other attributes of the items are set to their default
	 * values. If the table was created with the <code>SWT.VIRTUAL</code> style,
	 * these attributes are requested again as needed.
	 * 
	 * @param indices
	 *            the array of indices of the items
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0
	 *                and the number of elements in the list minus 1 (inclusive)
	 *                </li>
	 *                <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 * 
	 * @since 3.0
	 */
	public void clear(int[] indices) {
		checkWidget();
		if (indices == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (indices.length == 0)
			return;
		for (int i = 0; i < indices.length; i++) {
			if (!(0 <= indices[i] && indices[i] < itemCount)) {
				error(SWT.ERROR_INVALID_RANGE);
			}
		}
		for (int i = 0; i < indices.length; i++) {
			TableItem item = items[indices[i]];
			if (item != null)
				item.clear();
		}
	}

	/**
	 * Clears all the items in the receiver. The text, icon and other attributes
	 * of the items are set to their default values. If the table was created
	 * with the <code>SWT.VIRTUAL</code> style, these attributes are requested
	 * again as needed.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 * 
	 * @since 3.0
	 */
	public void clearAll() {
		checkWidget();
		for (int i = 0; i < itemCount; i++) {
			TableItem item = items[i];
			if (item != null)
				item.clear();
		}
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return null;
	}

	void createHandle(int index) {
	}

	void createItem(TableColumn column, int index) {
	}

	void createWidget(int index) {
		super.createWidget(index);
		items = new TableItem[4];
		columns = new TableColumn[4];
		itemCount = columnCount = 0;
	}

	/**
	 * Deselects the item at the given zero-relative index in the receiver. If
	 * the item at the index was already deselected, it remains deselected.
	 * Indices that are out of range are ignored.
	 * 
	 * @param index
	 *            the index of the item to deselect
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselect(int index) {
	}

	/**
	 * Deselects the items at the given zero-relative indices in the receiver.
	 * If the item at the given zero-relative index in the receiver is selected,
	 * it is deselected. If the item at the index was not selected, it remains
	 * deselected. The range of the indices is inclusive. Indices that are out
	 * of range are ignored.
	 * 
	 * @param start
	 *            the start index of the items to deselect
	 * @param end
	 *            the end index of the items to deselect
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselect(int start, int end) {
	}

	/**
	 * Deselects the items at the given zero-relative indices in the receiver.
	 * If the item at the given zero-relative index in the receiver is selected,
	 * it is deselected. If the item at the index was not selected, it remains
	 * deselected. Indices that are out of range and duplicate indices are
	 * ignored.
	 * 
	 * @param indices
	 *            the array of indices for the items to deselect
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the set of indices is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselect(int[] indices) {
	}

	/**
	 * Deselects all selected items in the receiver.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselectAll() {
	}

	void destroyItem(TableColumn column) {
	}

	void destroyItem(TableItem item) {
	}

	boolean dragDetect(int x, int y, boolean filter, boolean dragOnTimeout,
			boolean[] consume) {
		return false;
	}

	GdkColor getBackgroundColor() {
		return getBaseColor();
	}

	public Rectangle getClientArea() {
		return null;
	}

	int getClientWidth() {
		return 0;
	}

	/**
	 * Returns the column at the given, zero-relative index in the receiver.
	 * Throws an exception if the index is out of range. Columns are returned in
	 * the order that they were created. If no <code>TableColumn</code>s were
	 * created by the programmer, this method will throw
	 * <code>ERROR_INVALID_RANGE</code> despite the fact that a single column of
	 * data may be visible in the table. This occurs when the programmer uses
	 * the table like a list, adding items but never creating a column.
	 * 
	 * @param index
	 *            the index of the column to return
	 * @return the column at the given index
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0
	 *                and the number of elements in the list minus 1 (inclusive)
	 *                </li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#getColumnOrder()
	 * @see Table#setColumnOrder(int[])
	 * @see TableColumn#getMoveable()
	 * @see TableColumn#setMoveable(boolean)
	 * @see SWT#Move
	 */
	public TableColumn getColumn(int index) {
		checkWidget();
		if (!(0 <= index && index < columnCount))
			error(SWT.ERROR_INVALID_RANGE);
		return columns[index];
	}

	/**
	 * Returns the number of columns contained in the receiver. If no
	 * <code>TableColumn</code>s were created by the programmer, this value is
	 * zero, despite the fact that visually, one column of items may be visible.
	 * This occurs when the programmer uses the table like a list, adding items
	 * but never creating a column.
	 * 
	 * @return the number of columns
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getColumnCount() {
		checkWidget();
		return columnCount;
	}

	/**
	 * Returns an array of zero-relative integers that map the creation order of
	 * the receiver's items to the order in which they are currently being
	 * displayed.
	 * <p>
	 * Specifically, the indices of the returned array represent the current
	 * visual order of the items, and the contents of the array represent the
	 * creation order of the items.
	 * </p>
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain
	 * its list of items, so modifying the array will not affect the receiver.
	 * </p>
	 * 
	 * @return the current visual order of the receiver's items
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#setColumnOrder(int[])
	 * @see TableColumn#getMoveable()
	 * @see TableColumn#setMoveable(boolean)
	 * @see SWT#Move
	 * 
	 * @since 3.1
	 */
	public int[] getColumnOrder() {
		return null;
	}

	/**
	 * Returns an array of <code>TableColumn</code>s which are the columns in
	 * the receiver. Columns are returned in the order that they were created.
	 * If no <code>TableColumn</code>s were created by the programmer, the array
	 * is empty, despite the fact that visually, one column of items may be
	 * visible. This occurs when the programmer uses the table like a list,
	 * adding items but never creating a column.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain
	 * its list of items, so modifying the array will not affect the receiver.
	 * </p>
	 * 
	 * @return the items in the receiver
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#getColumnOrder()
	 * @see Table#setColumnOrder(int[])
	 * @see TableColumn#getMoveable()
	 * @see TableColumn#setMoveable(boolean)
	 * @see SWT#Move
	 */
	public TableColumn[] getColumns() {
		checkWidget();
		TableColumn[] result = new TableColumn[columnCount];
		System.arraycopy(columns, 0, result, 0, columnCount);
		return result;
	}

	TableItem getFocusItem() {
		return null;
	}

	GdkColor getForegroundColor() {
		return getTextColor();
	}

	/**
	 * Returns the width in pixels of a grid line.
	 * 
	 * @return the width of a grid line in pixels
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getGridLineWidth() {
		checkWidget();
		return 0;
	}

	/**
	 * Returns the height of the receiver's header
	 * 
	 * @return the height of the header or zero if the header is not visible
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 2.0
	 */
	public int getHeaderHeight() {
		return 0;
	}

	/**
	 * Returns <code>true</code> if the receiver's header is visible, and
	 * <code>false</code> otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, this method may still indicate that it is
	 * considered visible even though it may not actually be showing.
	 * </p>
	 * 
	 * @return the receiver's header's visibility state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getHeaderVisible() {
		return false;
	}

	/**
	 * Returns the item at the given, zero-relative index in the receiver.
	 * Throws an exception if the index is out of range.
	 * 
	 * @param index
	 *            the index of the item to return
	 * @return the item at the given index
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0
	 *                and the number of elements in the list minus 1 (inclusive)
	 *                </li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public TableItem getItem(int index) {
		checkWidget();
		if (!(0 <= index && index < itemCount))
			error(SWT.ERROR_INVALID_RANGE);
		return _getItem(index);
	}

	/**
	 * Returns the item at the given point in the receiver or null if no such
	 * item exists. The point is in the coordinate system of the receiver.
	 * <p>
	 * The item that is returned represents an item that could be selected by
	 * the user. For example, if selection only occurs in items in the first
	 * column, then null is returned if the point is outside of the item. Note
	 * that the SWT.FULL_SELECTION style hint, which specifies the selection
	 * policy, determines the extent of the selection.
	 * </p>
	 * 
	 * @param point
	 *            the point used to locate the item
	 * @return the item at the given point, or null if the point is not in a
	 *         selectable item
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the point is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public TableItem getItem(Point point) {
		return null;
	}

	/**
	 * Returns the number of items contained in the receiver.
	 * 
	 * @return the number of items
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getItemCount() {
		checkWidget();
		return itemCount;
	}

	/**
	 * Returns the height of the area which would be used to display
	 * <em>one</em> of the items in the receiver.
	 * 
	 * @return the height of one item
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getItemHeight() {
		return 0;
	}

	/**
	 * Returns a (possibly empty) array of <code>TableItem</code>s which are the
	 * items in the receiver.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain
	 * its list of items, so modifying the array will not affect the receiver.
	 * </p>
	 * 
	 * @return the items in the receiver
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public TableItem[] getItems() {
		checkWidget();
		TableItem[] result = new TableItem[itemCount];
		if ((style & SWT.VIRTUAL) != 0) {
			for (int i = 0; i < itemCount; i++) {
				result[i] = _getItem(i);
			}
		} else {
			System.arraycopy(items, 0, result, 0, itemCount);
		}
		return result;
	}

	/**
	 * Returns <code>true</code> if the receiver's lines are visible, and
	 * <code>false</code> otherwise. Note that some platforms draw grid lines
	 * while others may draw alternating row colors.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, this method may still indicate that it is
	 * considered visible even though it may not actually be showing.
	 * </p>
	 * 
	 * @return the visibility state of the lines
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getLinesVisible() {
		return false;
	}

	/**
	 * Returns an array of <code>TableItem</code>s that are currently selected
	 * in the receiver. The order of the items is unspecified. An empty array
	 * indicates that no items are selected.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain
	 * its selection, so modifying the array will not affect the receiver.
	 * </p>
	 * 
	 * @return an array representing the selection
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public TableItem[] getSelection() {
		return null;
	}

	/**
	 * Returns the number of selected items contained in the receiver.
	 * 
	 * @return the number of selected items
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getSelectionCount() {
		return 0;
	}

	/**
	 * Returns the zero-relative index of the item which is currently selected
	 * in the receiver, or -1 if no item is selected.
	 * 
	 * @return the index of the selected item
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getSelectionIndex() {
		return -1;
	}

	/**
	 * Returns the zero-relative indices of the items which are currently
	 * selected in the receiver. The order of the indices is unspecified. The
	 * array is empty if no items are selected.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain
	 * its selection, so modifying the array will not affect the receiver.
	 * </p>
	 * 
	 * @return the array of indices of the selected items
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int[] getSelectionIndices() {
		return new int[0];
	}

	/**
	 * Returns the column which shows the sort indicator for the receiver. The
	 * value may be null if no column shows the sort indicator.
	 * 
	 * @return the sort indicator
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #setSortColumn(TableColumn)
	 * 
	 * @since 3.2
	 */
	public TableColumn getSortColumn() {
		checkWidget();
		return sortColumn;
	}

	/**
	 * Returns the direction of the sort indicator for the receiver. The value
	 * will be one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
	 * 
	 * @return the sort direction
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #setSortDirection(int)
	 * 
	 * @since 3.2
	 */
	public int getSortDirection() {
		checkWidget();
		return sortDirection;
	}

	/**
	 * Returns the zero-relative index of the item which is currently at the top
	 * of the receiver. This index can change when items are scrolled or new
	 * items are added or removed.
	 * 
	 * @return the index of the top item
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getTopIndex() {
		return 0;
	}

	void hideFirstColumn() {
	}

	void hookEvents() {
	}

	/**
	 * Searches the receiver's list starting at the first column (index 0) until
	 * a column is found that is equal to the argument, and returns the index of
	 * that column. If no column is found, returns -1.
	 * 
	 * @param column
	 *            the search column
	 * @return the index of the column
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the column is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int indexOf(TableColumn column) {
		checkWidget();
		if (column == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		for (int i = 0; i < columnCount; i++) {
			if (columns[i] == column)
				return i;
		}
		return -1;
	}

	/**
	 * Searches the receiver's list starting at the first item (index 0) until
	 * an item is found that is equal to the argument, and returns the index of
	 * that item. If no item is found, returns -1.
	 * 
	 * @param item
	 *            the search item
	 * @return the index of the item
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int indexOf(TableItem item) {
		checkWidget();
		if (item == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (1 <= lastIndexOf && lastIndexOf < itemCount - 1) {
			if (items[lastIndexOf] == item)
				return lastIndexOf;
			if (items[lastIndexOf + 1] == item)
				return ++lastIndexOf;
			if (items[lastIndexOf - 1] == item)
				return --lastIndexOf;
		}
		if (lastIndexOf < itemCount / 2) {
			for (int i = 0; i < itemCount; i++) {
				if (items[i] == item)
					return lastIndexOf = i;
			}
		} else {
			for (int i = itemCount - 1; i >= 0; --i) {
				if (items[i] == item)
					return lastIndexOf = i;
			}
		}
		return -1;
	}

	/**
	 * Returns <code>true</code> if the item is selected, and <code>false</code>
	 * otherwise. Indices out of range are ignored.
	 * 
	 * @param index
	 *            the index of the item
	 * @return the selection state of the item at the index
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean isSelected(int index) {
		return false;
	}

	boolean mnemonicHit(char key) {
		for (int i = 0; i < columnCount; i++) {
			long /* int */labelHandle = columns[i].labelHandle;
			if (labelHandle != 0 && mnemonicHit(labelHandle, key))
				return true;
		}
		return false;
	}

	boolean mnemonicMatch(char key) {
		for (int i = 0; i < columnCount; i++) {
			long /* int */labelHandle = columns[i].labelHandle;
			if (labelHandle != 0 && mnemonicMatch(labelHandle, key))
				return true;
		}
		return false;
	}

	void recreateRenderers() {
	}

	void redrawBackgroundImage() {
		Control control = findBackgroundControl();
		if (control != null && control.backgroundImage != null) {
			redrawWidget(0, 0, 0, 0, true, false, false);
		}
	}

	void register() {
	}

	void releaseChildren(boolean destroy) {
		if (items != null) {
			for (int i = 0; i < itemCount; i++) {
				TableItem item = items[i];
				if (item != null && !item.isDisposed()) {
					item.release(false);
				}
			}
			items = null;
		}
		if (columns != null) {
			for (int i = 0; i < columnCount; i++) {
				TableColumn column = columns[i];
				if (column != null && !column.isDisposed()) {
					column.release(false);
				}
			}
			columns = null;
		}
		super.releaseChildren(destroy);
	}

	void releaseWidget() {
	}

	/**
	 * Removes the item from the receiver at the given zero-relative index.
	 * 
	 * @param index
	 *            the index for the item
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0
	 *                and the number of elements in the list minus 1 (inclusive)
	 *                </li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(int index) {
	}

	/**
	 * Removes the items from the receiver which are between the given
	 * zero-relative start and end indices (inclusive).
	 * 
	 * @param start
	 *            the start of the range
	 * @param end
	 *            the end of the range
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if either the start or end are
	 *                not between 0 and the number of elements in the list minus
	 *                1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(int start, int end) {
	}

	/**
	 * Removes the items from the receiver's list at the given zero-relative
	 * indices.
	 * 
	 * @param indices
	 *            the array of indices of the items
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0
	 *                and the number of elements in the list minus 1 (inclusive)
	 *                </li>
	 *                <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(int[] indices) {
	}

	/**
	 * Removes all of the items from the receiver.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void removeAll() {
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the user changes the receiver's selection.
	 * 
	 * @param listener
	 *            the listener which should no longer be notified
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SelectionListener
	 * @see #addSelectionListener(SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.Selection, listener);
		eventTable.unhook(SWT.DefaultSelection, listener);
	}

	void reskinChildren(int flags) {
		if (items != null) {
			for (int i = 0; i < itemCount; i++) {
				TableItem item = items[i];
				if (item != null)
					item.reskin(flags);
			}
		}
		if (columns != null) {
			for (int i = 0; i < columnCount; i++) {
				TableColumn column = columns[i];
				if (!column.isDisposed())
					column.reskin(flags);
			}
		}
		super.reskinChildren(flags);
	}

	/**
	 * Selects the item at the given zero-relative index in the receiver. If the
	 * item at the index was already selected, it remains selected. Indices that
	 * are out of range are ignored.
	 * 
	 * @param index
	 *            the index of the item to select
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void select(int index) {
	}

	/**
	 * Selects the items in the range specified by the given zero-relative
	 * indices in the receiver. The range of indices is inclusive. The current
	 * selection is not cleared before the new items are selected.
	 * <p>
	 * If an item in the given range is not selected, it is selected. If an item
	 * in the given range was already selected, it remains selected. Indices
	 * that are out of range are ignored and no items will be selected if start
	 * is greater than end. If the receiver is single-select and there is more
	 * than one item in the given range, then all indices are ignored.
	 * </p>
	 * 
	 * @param start
	 *            the start of the range
	 * @param end
	 *            the end of the range
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#setSelection(int,int)
	 */
	public void select(int start, int end) {
	}

	/**
	 * Selects the items at the given zero-relative indices in the receiver. The
	 * current selection is not cleared before the new items are selected.
	 * <p>
	 * If the item at a given index is not selected, it is selected. If the item
	 * at a given index was already selected, it remains selected. Indices that
	 * are out of range and duplicate indices are ignored. If the receiver is
	 * single-select and multiple indices are specified, then all indices are
	 * ignored.
	 * </p>
	 * 
	 * @param indices
	 *            the array of indices for the items to select
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#setSelection(int[])
	 */
	public void select(int[] indices) {
	}

	/**
	 * Selects all of the items in the receiver.
	 * <p>
	 * If the receiver is single-select, do nothing.
	 * </p>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void selectAll() {
	}

	void selectFocusIndex(int index) {
	}

	int setBounds(int x, int y, int width, int height, boolean move,
			boolean resize) {
		return 0;
	}

	/**
	 * Sets the order that the items in the receiver should be displayed in to
	 * the given argument which is described in terms of the zero-relative
	 * ordering of when the items were added.
	 * 
	 * @param order
	 *            the new order to display the items
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item order is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item order is not the
	 *                same length as the number of items</li>
	 *                </ul>
	 * 
	 * @see Table#getColumnOrder()
	 * @see TableColumn#getMoveable()
	 * @see TableColumn#setMoveable(boolean)
	 * @see SWT#Move
	 * 
	 * @since 3.1
	 */
	public void setColumnOrder(int[] order) {
	}

	void setFontDescription(long /* int */font) {
		super.setFontDescription(font);
		TableColumn[] columns = getColumns();
		for (int i = 0; i < columns.length; i++) {
			if (columns[i] != null) {
				columns[i].setFontDescription(font);
			}
		}
	}

	/**
	 * Marks the receiver's header as visible if the argument is
	 * <code>true</code>, and marks it invisible otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, marking it visible may not actually cause
	 * it to be displayed.
	 * </p>
	 * 
	 * @param show
	 *            the new visibility state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setHeaderVisible(boolean show) {
	}

	/**
	 * Sets the number of items contained in the receiver.
	 * 
	 * @param count
	 *            the number of items
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.0
	 */
	public void setItemCount(int count) {
	}

	/**
	 * Marks the receiver's lines as visible if the argument is
	 * <code>true</code>, and marks it invisible otherwise. Note that some
	 * platforms draw grid lines while others may draw alternating row colors.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, marking it visible may not actually cause
	 * it to be displayed.
	 * </p>
	 * 
	 * @param show
	 *            the new visibility state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setLinesVisible(boolean show) {
	}

	void setOrientation(boolean create) {
		super.setOrientation(create);
		for (int i = 0; i < itemCount; i++) {
			if (items[i] != null)
				items[i].setOrientation(create);
		}
		for (int i = 0; i < columnCount; i++) {
			if (columns[i] != null)
				columns[i].setOrientation(create);
		}
	}

	public void setRedraw(boolean redraw) {
		checkWidget();
		super.setRedraw(redraw);
		if (redraw && drawCount == 0) {
			/* Resize the item array to match the item count */
			if (items.length > 4 && items.length - itemCount > 3) {
				int length = Math.max(4, (itemCount + 3) / 4 * 4);
				TableItem[] newItems = new TableItem[length];
				System.arraycopy(items, 0, newItems, 0, itemCount);
				items = newItems;
			}
		}
	}

	/**
	 * Sets the column used by the sort indicator for the receiver. A null value
	 * will clear the sort indicator. The current sort column is cleared before
	 * the new column is set.
	 * 
	 * @param column
	 *            the column used by the sort indicator or <code>null</code>
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the column is disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.2
	 */
	public void setSortColumn(TableColumn column) {
	}

	/**
	 * Sets the direction of the sort indicator for the receiver. The value can
	 * be one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
	 * 
	 * @param direction
	 *            the direction of the sort indicator
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.2
	 */
	public void setSortDirection(int direction) {
	}

	/**
	 * Selects the item at the given zero-relative index in the receiver. The
	 * current selection is first cleared, then the new item is selected.
	 * 
	 * @param index
	 *            the index of the item to select
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#deselectAll()
	 * @see Table#select(int)
	 */
	public void setSelection(int index) {
		checkWidget();
		boolean fixColumn = showFirstColumn();
		deselectAll();
		selectFocusIndex(index);
		showSelection();
		if (fixColumn)
			hideFirstColumn();
	}

	/**
	 * Selects the items in the range specified by the given zero-relative
	 * indices in the receiver. The range of indices is inclusive. The current
	 * selection is cleared before the new items are selected.
	 * <p>
	 * Indices that are out of range are ignored and no items will be selected
	 * if start is greater than end. If the receiver is single-select and there
	 * is more than one item in the given range, then all indices are ignored.
	 * </p>
	 * 
	 * @param start
	 *            the start index of the items to select
	 * @param end
	 *            the end index of the items to select
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#deselectAll()
	 * @see Table#select(int,int)
	 */
	public void setSelection(int start, int end) {
		checkWidget();
		deselectAll();
		if (end < 0 || start > end
				|| ((style & SWT.SINGLE) != 0 && start != end))
			return;
		if (itemCount == 0 || start >= itemCount)
			return;
		boolean fixColumn = showFirstColumn();
		start = Math.max(0, start);
		end = Math.min(end, itemCount - 1);
		selectFocusIndex(start);
		if ((style & SWT.MULTI) != 0) {
			select(start, end);
		}
		showSelection();
		if (fixColumn)
			hideFirstColumn();
	}

	/**
	 * Selects the items at the given zero-relative indices in the receiver. The
	 * current selection is cleared before the new items are selected.
	 * <p>
	 * Indices that are out of range and duplicate indices are ignored. If the
	 * receiver is single-select and multiple indices are specified, then all
	 * indices are ignored.
	 * </p>
	 * 
	 * @param indices
	 *            the indices of the items to select
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#deselectAll()
	 * @see Table#select(int[])
	 */
	public void setSelection(int[] indices) {
		checkWidget();
		if (indices == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		deselectAll();
		int length = indices.length;
		if (length == 0 || ((style & SWT.SINGLE) != 0 && length > 1))
			return;
		boolean fixColumn = showFirstColumn();
		selectFocusIndex(indices[0]);
		if ((style & SWT.MULTI) != 0) {
			select(indices);
		}
		showSelection();
		if (fixColumn)
			hideFirstColumn();
	}

	/**
	 * Sets the receiver's selection to the given item. The current selection is
	 * cleared before the new item is selected.
	 * <p>
	 * If the item is not in the receiver, then it is ignored.
	 * </p>
	 * 
	 * @param item
	 *            the item to select
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item has been disposed
	 *                </li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.2
	 */
	public void setSelection(TableItem item) {
		if (item == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		setSelection(new TableItem[] { item });
	}

	/**
	 * Sets the receiver's selection to be the given array of items. The current
	 * selection is cleared before the new items are selected.
	 * <p>
	 * Items that are not in the receiver are ignored. If the receiver is
	 * single-select and multiple items are specified, then all items are
	 * ignored.
	 * </p>
	 * 
	 * @param items
	 *            the array of items
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if one of the items has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#deselectAll()
	 * @see Table#select(int[])
	 * @see Table#setSelection(int[])
	 */
	public void setSelection(TableItem[] items) {
		checkWidget();
		if (items == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		boolean fixColumn = showFirstColumn();
		deselectAll();
		int length = items.length;
		if (!(length == 0 || ((style & SWT.SINGLE) != 0 && length > 1))) {
			boolean first = true;
			for (int i = 0; i < length; i++) {
				int index = indexOf(items[i]);
				if (index != -1) {
					if (first) {
						first = false;
						selectFocusIndex(index);
					} else {
						select(index);
					}
				}
			}
			showSelection();
		}
		if (fixColumn)
			hideFirstColumn();
	}

	/**
	 * Sets the zero-relative index of the item which is currently at the top of
	 * the receiver. This index can change when items are scrolled or new items
	 * are added and removed.
	 * 
	 * @param index
	 *            the index of the top item
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setTopIndex(int index) {
	}

	/**
	 * Shows the column. If the column is already showing in the receiver, this
	 * method simply returns. Otherwise, the columns are scrolled until the
	 * column is visible.
	 * 
	 * @param column
	 *            the column to be shown
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the column is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the column has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.0
	 */
	public void showColumn(TableColumn column) {
	}

	boolean showFirstColumn() {
		return true;
	}

	/**
	 * Shows the item. If the item is already showing in the receiver, this
	 * method simply returns. Otherwise, the items are scrolled until the item
	 * is visible.
	 * 
	 * @param item
	 *            the item to be shown
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item has been disposed
	 *                </li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#showSelection()
	 */
	public void showItem(TableItem item) {
	}

	/**
	 * Shows the selection. If the selection is already showing in the receiver,
	 * this method simply returns. Otherwise, the items are scrolled until the
	 * selection is visible.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Table#showItem(TableItem)
	 */
	public void showSelection() {
	}
}
