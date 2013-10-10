/*******************************************************************************
 * Copyright (c) 2000, 2009, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Instances of this class represent a selectable user interface object that
 * represents an item in a table.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/snippets/#table">Table, TableItem,
 *      TableColumn snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class TableItem extends Item {
	Table parent;
	Font font;
	Font[] cellFont;
	boolean cached, grayed;

	/**
	 * Constructs a new instance of this class given its parent (which must be a
	 * <code>Table</code>), a style value describing its behavior and
	 * appearance, and the index at which to place it in the items maintained by
	 * its parent.
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
	 * @param index
	 *            the zero-relative index to store the receiver in its parent
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0
	 *                and the number of elements in the parent (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 * 
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public TableItem(Table parent, int style, int index) {
		this(parent, style, index, true);
	}

	/**
	 * Constructs a new instance of this class given its parent (which must be a
	 * <code>Table</code>) and a style value describing its behavior and
	 * appearance. The item is added to the end of the items maintained by its
	 * parent.
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
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public TableItem(Table parent, int style) {
		this(parent, style, checkNull(parent).getItemCount(), true);
	}

	TableItem(Table parent, int style, int index, boolean create) {
		super(parent, style);
	}

	static Table checkNull(Table control) {
		if (control == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		return control;
	}

	Color _getBackground() {
		return null;
	}

	Color _getBackground(int index) {
		return null;
	}

	boolean _getChecked() {
		return false;
	}

	Color _getForeground() {
		return null;
	}

	Color _getForeground(int index) {
		return null;
	}

	Image _getImage(int index) {
		return null;
	}

	String _getText(int index) {
		return null;
	}

	protected void checkSubclass() {
		if (!isValidSubclass())
			error(SWT.ERROR_INVALID_SUBCLASS);
	}

	void clear() {
	}

	void destroyWidget() {
		parent.destroyItem(this);
		releaseHandle();
	}

	/**
	 * Returns the receiver's background color.
	 * 
	 * @return the background color
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
	public Color getBackground() {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return _getBackground();
	}

	/**
	 * Returns a rectangle describing the size and location of the receiver's
	 * text relative to its parent.
	 * 
	 * @return the bounding rectangle of the receiver's text
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
	public Rectangle getBounds() {
		return null;
	}

	/**
	 * Returns the background color at the given column index in the receiver.
	 * 
	 * @param index
	 *            the column index
	 * @return the background color
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
	public Color getBackground(int index) {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return _getBackground(index);
	}

	/**
	 * Returns a rectangle describing the receiver's size and location relative
	 * to its parent at a column in the table.
	 * 
	 * @param index
	 *            the index that specifies the column
	 * @return the receiver's bounding column rectangle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Rectangle getBounds(int index) {
		return null;
	}

	/**
	 * Returns <code>true</code> if the receiver is checked, and false
	 * otherwise. When the parent does not have the <code>CHECK</code> style,
	 * return false.
	 * 
	 * @return the checked state of the checkbox
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getChecked() {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		if ((parent.style & SWT.CHECK) == 0)
			return false;
		return _getChecked();
	}

	/**
	 * Returns the font that the receiver will use to paint textual information
	 * for this item.
	 * 
	 * @return the receiver's font
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
	public Font getFont() {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return font != null ? font : parent.getFont();
	}

	/**
	 * Returns the font that the receiver will use to paint textual information
	 * for the specified cell in this item.
	 * 
	 * @param index
	 *            the column index
	 * @return the receiver's font
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
	public Font getFont(int index) {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		int count = Math.max(1, parent.columnCount);
		if (0 > index || index > count - 1)
			return getFont();
		if (cellFont == null || cellFont[index] == null)
			return getFont();
		return cellFont[index];
	}

	/**
	 * Returns the foreground color that the receiver will use to draw.
	 * 
	 * @return the receiver's foreground color
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
	public Color getForeground() {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return _getForeground();
	}

	/**
	 * 
	 * Returns the foreground color at the given column index in the receiver.
	 * 
	 * @param index
	 *            the column index
	 * @return the foreground color
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
	public Color getForeground(int index) {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return _getForeground(index);
	}

	/**
	 * Returns <code>true</code> if the receiver is grayed, and false otherwise.
	 * When the parent does not have the <code>CHECK</code> style, return false.
	 * 
	 * @return the grayed state of the checkbox
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getGrayed() {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		if ((parent.style & SWT.CHECK) == 0)
			return false;
		return grayed;
	}

	public Image getImage() {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return getImage(0);
	}

	/**
	 * Returns the image stored at the given column index in the receiver, or
	 * null if the image has not been set or if the column does not exist.
	 * 
	 * @param index
	 *            the column index
	 * @return the image stored at the given column index in the receiver
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Image getImage(int index) {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return _getImage(index);
	}

	/**
	 * Returns a rectangle describing the size and location relative to its
	 * parent of an image at a column in the table. An empty rectangle is
	 * returned if index exceeds the index of the table's last column.
	 * 
	 * @param index
	 *            the index that specifies the column
	 * @return the receiver's bounding image rectangle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Rectangle getImageBounds(int index) {
		return null;
	}

	/**
	 * Gets the image indent.
	 * 
	 * @return the indent
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getImageIndent() {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		/* Image indent is not supported on GTK */
		return 0;
	}

	String getNameText() {
		if ((parent.style & SWT.VIRTUAL) != 0) {
			if (!cached)
				return "*virtual*"; //$NON-NLS-1$
		}
		return super.getNameText();
	}

	/**
	 * Returns the receiver's parent, which must be a <code>Table</code>.
	 * 
	 * @return the receiver's parent
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Table getParent() {
		checkWidget();
		return parent;
	}

	public String getText() {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return getText(0);
	}

	/**
	 * Returns the text stored at the given column index in the receiver, or
	 * empty string if the text has not been set.
	 * 
	 * @param index
	 *            the column index
	 * @return the text stored at the given column index in the receiver
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public String getText(int index) {
		checkWidget();
		if (!parent.checkData(this))
			error(SWT.ERROR_WIDGET_DISPOSED);
		return _getText(index);
	}

	/**
	 * Returns a rectangle describing the size and location relative to its
	 * parent of the text at a column in the table. An empty rectangle is
	 * returned if index exceeds the index of the table's last column.
	 * 
	 * @param index
	 *            the index that specifies the column
	 * @return the receiver's bounding text rectangle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.3
	 */
	public Rectangle getTextBounds(int index) {
		return null;
	}

	void releaseHandle() {
	}

	void releaseWidget() {
		super.releaseWidget();
		font = null;
		cellFont = null;
	}

	/**
	 * Sets the receiver's background color to the color specified by the
	 * argument, or to the default system color for the item if the argument is
	 * null.
	 * 
	 * @param color
	 *            the new color (or null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been
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
	 * @since 2.0
	 */
	public void setBackground(Color color) {
	}

	/**
	 * Sets the background color at the given column index in the receiver to
	 * the color specified by the argument, or to the default system color for
	 * the item if the argument is null.
	 * 
	 * @param index
	 *            the column index
	 * @param color
	 *            the new color (or null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been
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
	public void setBackground(int index, Color color) {
	}

	/**
	 * Sets the checked state of the checkbox for this item. This state change
	 * only applies if the Table was created with the SWT.CHECK style.
	 * 
	 * @param checked
	 *            the new checked state of the checkbox
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setChecked(boolean checked) {
	}

	/**
	 * Sets the font that the receiver will use to paint textual information for
	 * this item to the font specified by the argument, or to the default font
	 * for that kind of control if the argument is null.
	 * 
	 * @param font
	 *            the new font (or null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been
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
	public void setFont(Font font) {
	}

	/**
	 * Sets the font that the receiver will use to paint textual information for
	 * the specified cell in this item to the font specified by the argument, or
	 * to the default font for that kind of control if the argument is null.
	 * 
	 * @param index
	 *            the column index
	 * @param font
	 *            the new font (or null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been
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
	public void setFont(int index, Font font) {
	}

	/**
	 * Sets the receiver's foreground color to the color specified by the
	 * argument, or to the default system color for the item if the argument is
	 * null.
	 * 
	 * @param color
	 *            the new color (or null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been
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
	 * @since 2.0
	 */
	public void setForeground(Color color) {
	}

	/**
	 * Sets the foreground color at the given column index in the receiver to
	 * the color specified by the argument, or to the default system color for
	 * the item if the argument is null.
	 * 
	 * @param index
	 *            the column index
	 * @param color
	 *            the new color (or null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been
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
	public void setForeground(int index, Color color) {
	}

	/**
	 * Sets the grayed state of the checkbox for this item. This state change
	 * only applies if the Table was created with the SWT.CHECK style.
	 * 
	 * @param grayed
	 *            the new grayed state of the checkbox;
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setGrayed(boolean grayed) {
	}

	/**
	 * Sets the receiver's image at a column.
	 * 
	 * @param index
	 *            the column index
	 * @param image
	 *            the new image
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the image has been
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
	public void setImage(int index, Image image) {
	}

	public void setImage(Image image) {
		checkWidget();
		setImage(0, image);
	}

	/**
	 * Sets the image for multiple columns in the table.
	 * 
	 * @param images
	 *            the array of new images
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the array of images is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if one of the images has been
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
	public void setImage(Image[] images) {
		checkWidget();
		if (images == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		for (int i = 0; i < images.length; i++) {
			setImage(i, images[i]);
		}
	}

	/**
	 * Sets the indent of the first column's image, expressed in terms of the
	 * image's width.
	 * 
	 * @param indent
	 *            the new indent
	 * 
	 *            </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @deprecated this functionality is not supported on most platforms
	 */
	public void setImageIndent(int indent) {
		checkWidget();
		if (indent < 0)
			return;
		/* Image indent is not supported on GTK */
		cached = true;
	}

	/**
	 * Sets the receiver's text at a column
	 * 
	 * @param index
	 *            the column index
	 * @param string
	 *            the new text
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the text is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setText(int index, String string) {
	}

	public void setText(String string) {
		checkWidget();
		setText(0, string);
	}

	/**
	 * Sets the text for multiple columns in the table.
	 * 
	 * @param strings
	 *            the array of new strings
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the text is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setText(String[] strings) {
		checkWidget();
		if (strings == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			if (string != null)
				setText(i, string);
		}
	}
}
