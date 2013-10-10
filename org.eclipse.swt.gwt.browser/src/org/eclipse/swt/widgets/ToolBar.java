/*******************************************************************************
 * Copyright (c) 2000, 2010, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
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
import org.eclipse.swt.graphics.Point;

/**
 * Instances of this class support the layout of selectable tool bar items.
 * <p>
 * The item children that may be added to instances of this class must be of
 * type <code>ToolItem</code>.
 * </p>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it
 * does not make sense to add <code>Control</code> children to it, or set a
 * layout on it.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>FLAT, WRAP, RIGHT, HORIZONTAL, VERTICAL, SHADOW_OUT</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/snippets/#toolbar">ToolBar, ToolItem
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ToolBar extends Composite {
	ToolItem lastFocus;
	ToolItem[] tabItemList;
	ImageList imageList;

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
	 * @see SWT#FLAT
	 * @see SWT#WRAP
	 * @see SWT#RIGHT
	 * @see SWT#HORIZONTAL
	 * @see SWT#SHADOW_OUT
	 * @see SWT#VERTICAL
	 * @see Widget#checkSubclass()
	 * @see Widget#getStyle()
	 */
	public ToolBar(Composite parent, int style) {
	}

	protected void checkSubclass() {
		if (!isValidSubclass())
			error(SWT.ERROR_INVALID_SUBCLASS);
	}

	void createHandle(int index) {
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return null;
	}

	Widget computeTabGroup() {
		ToolItem[] items = _getItems();
		if (tabItemList == null) {
			int i = 0;
			while (i < items.length && items[i].control == null)
				i++;
			if (i == items.length)
				return super.computeTabGroup();
		}
		int index = 0;
		while (index < items.length) {
			if (items[index].hasFocus())
				break;
			index++;
		}
		while (index >= 0) {
			ToolItem item = items[index];
			if (item.isTabGroup())
				return item;
			index--;
		}
		return super.computeTabGroup();
	}

	Widget[] computeTabList() {
		ToolItem[] items = _getItems();
		if (tabItemList == null) {
			int i = 0;
			while (i < items.length && items[i].control == null)
				i++;
			if (i == items.length)
				return super.computeTabList();
		}
		Widget result[] = {};
		if (!isTabGroup() || !isEnabled() || !isVisible())
			return result;
		ToolItem[] list = tabList != null ? _getTabItemList() : items;
		for (int i = 0; i < list.length; i++) {
			ToolItem child = list[i];
			Widget[] childList = child.computeTabList();
			if (childList.length != 0) {
				Widget[] newResult = new Widget[result.length
						+ childList.length];
				System.arraycopy(result, 0, newResult, 0, result.length);
				System.arraycopy(childList, 0, newResult, result.length,
						childList.length);
				result = newResult;
			}
		}
		if (result.length == 0)
			result = new Widget[] { this };
		return result;
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
	public ToolItem getItem(int index) {
		checkWidget();
		if (!(0 <= index && index < getItemCount()))
			error(SWT.ERROR_INVALID_RANGE);
		return getItems()[index];
	}

	/**
	 * Returns the item at the given point in the receiver or null if no such
	 * item exists. The point is in the coordinate system of the receiver.
	 * 
	 * @param point
	 *            the point used to locate the item
	 * @return the item at the given point
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
	public ToolItem getItem(Point point) {
		checkWidget();
		if (point == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getBounds().contains(point))
				return items[i];
		}
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
		return 0;
	}

	/**
	 * Returns an array of <code>ToolItem</code>s which are the items in the
	 * receiver.
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
	public ToolItem[] getItems() {
		checkWidget();
		return _getItems();
	}

	ToolItem[] _getItems() {
		return null;
	}

	/**
	 * Returns the number of rows in the receiver. When the receiver has the
	 * <code>WRAP</code> style, the number of rows can be greater than one.
	 * Otherwise, the number of rows is always one.
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
	public int getRowCount() {
		checkWidget();
		/* On GTK, toolbars cannot wrap */
		return 1;
	}

	ToolItem[] _getTabItemList() {
		if (tabItemList == null)
			return tabItemList;
		int count = 0;
		for (int i = 0; i < tabItemList.length; i++) {
			if (!tabItemList[i].isDisposed())
				count++;
		}
		if (count == tabItemList.length)
			return tabItemList;
		ToolItem[] newList = new ToolItem[count];
		int index = 0;
		for (int i = 0; i < tabItemList.length; i++) {
			if (!tabItemList[i].isDisposed()) {
				newList[index++] = tabItemList[i];
			}
		}
		tabItemList = newList;
		return tabItemList;
	}

	boolean hasFocus() {
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			ToolItem item = items[i];
			if (item.hasFocus())
				return true;
		}
		return super.hasFocus();
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
	 *                <li>ERROR_NULL_ARGUMENT - if the tool item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the tool item has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int indexOf(ToolItem item) {
		checkWidget();
		if (item == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			if (item == items[i])
				return i;
		}
		return -1;
	}

	boolean mnemonicHit(char key) {
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			long /* int */labelHandle = items[i].labelHandle;
			if (labelHandle != 0 && mnemonicHit(labelHandle, key))
				return true;
		}
		return false;
	}

	boolean mnemonicMatch(char key) {
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			long /* int */labelHandle = items[i].labelHandle;
			if (labelHandle != 0 && mnemonicMatch(labelHandle, key))
				return true;
		}
		return false;
	}

	void relayout() {
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			ToolItem item = items[i];
			if (item != null)
				item.resizeControl();
		}
	}

	void releaseChildren(boolean destroy) {
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			ToolItem item = items[i];
			if (item != null && !item.isDisposed()) {
				item.release(false);
			}
		}
		super.releaseChildren(destroy);
	}

	void releaseWidget() {
		super.releaseWidget();
		if (imageList != null)
			imageList.dispose();
		imageList = null;
	}

	void removeControl(Control control) {
		super.removeControl(control);
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			ToolItem item = items[i];
			if (item.control == control)
				item.setControl(null);
		}
	}

	void reskinChildren(int flags) {
		ToolItem[] items = _getItems();
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				ToolItem item = items[i];
				if (item != null)
					item.reskin(flags);
			}
		}
		super.reskinChildren(flags);
	}

	int setBounds(int x, int y, int width, int height, boolean move,
			boolean resize) {
		int result = super.setBounds(x, y, width, height, move, resize);
		if ((result & RESIZED) != 0)
			relayout();
		return result;
	}

	void setFontDescription(long /* int */font) {
		super.setFontDescription(font);
		ToolItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setFontDescription(font);
		}
		relayout();
	}

	void setOrientation(boolean create) {
		super.setOrientation(create);
		ToolItem[] items = _getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setOrientation(create);
		}
	}

	public void setToolTipText(String string) {
	}

}
