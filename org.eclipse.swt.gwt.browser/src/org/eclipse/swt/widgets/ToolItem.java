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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Instances of this class represent a selectable user interface object that
 * represents a button in a tool bar.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>PUSH, CHECK, RADIO, SEPARATOR, DROP_DOWN</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles CHECK, PUSH, RADIO, SEPARATOR and DROP_DOWN may
 * be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/snippets/#toolbar">ToolBar, ToolItem
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ToolItem extends Item {
	long /* int */boxHandle, arrowHandle, arrowBoxHandle, separatorHandle,
			labelHandle, imageHandle;
	ToolBar parent;
	Control control;
	Image hotImage, disabledImage;
	String toolTipText;
	boolean drawHotImage;

	/**
	 * Constructs a new instance of this class given its parent (which must be a
	 * <code>ToolBar</code>) and a style value describing its behavior and
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
	 * @see SWT#PUSH
	 * @see SWT#CHECK
	 * @see SWT#RADIO
	 * @see SWT#SEPARATOR
	 * @see SWT#DROP_DOWN
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ToolItem(ToolBar parent, int style) {
		super(parent, checkStyle(style));
		this.parent = parent;
		createWidget(parent.getItemCount());
	}

	/**
	 * Constructs a new instance of this class given its parent (which must be a
	 * <code>ToolBar</code>), a style value describing its behavior and
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
	 * @see SWT#PUSH
	 * @see SWT#CHECK
	 * @see SWT#RADIO
	 * @see SWT#SEPARATOR
	 * @see SWT#DROP_DOWN
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public ToolItem(ToolBar parent, int style, int index) {
		super(parent, checkStyle(style));
		this.parent = parent;
		int count = parent.getItemCount();
		if (!(0 <= index && index <= count)) {
			error(SWT.ERROR_INVALID_RANGE);
		}
		createWidget(index);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the control is selected by the user, by sending it one of the
	 * messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * When <code>widgetSelected</code> is called when the mouse is over the
	 * arrow portion of a drop-down tool, the event object detail field contains
	 * the value <code>SWT.ARROW</code>. <code>widgetDefaultSelected</code> is
	 * not called.
	 * </p>
	 * <p>
	 * When the <code>SWT.RADIO</code> style bit is set, the
	 * <code>widgetSelected</code> method is also called when the receiver loses
	 * selection because another item in the same radio group was selected by
	 * the user. During <code>widgetSelected</code> the application can use
	 * <code>getSelection()</code> to determine the current selected state of
	 * the receiver.
	 * </p>
	 * 
	 * @param listener
	 *            the listener which should be notified when the control is
	 *            selected by the user,
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

	static int checkStyle(int style) {
		return checkBits(style, SWT.PUSH, SWT.CHECK, SWT.RADIO, SWT.SEPARATOR,
				SWT.DROP_DOWN, 0);
	}

	protected void checkSubclass() {
		if (!isValidSubclass())
			error(SWT.ERROR_INVALID_SUBCLASS);
	}

	void createHandle(int index) {
	}

	void createWidget(int index) {
		super.createWidget(index);
		showWidget(index);
		parent.relayout();
	}

	Widget[] computeTabList() {
		if (isTabGroup()) {
			if (getEnabled()) {
				if ((style & SWT.SEPARATOR) != 0) {
					if (control != null)
						return control.computeTabList();
				} else {
					return new Widget[] { this };
				}
			}
		}
		return new Widget[0];
	}

	public void dispose() {
		if (isDisposed())
			return;
		ToolBar parent = this.parent;
		super.dispose();
		parent.relayout();
	}

	/**
	 * Returns a rectangle describing the receiver's size and location relative
	 * to its parent.
	 * 
	 * @return the receiver's bounding rectangle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Rectangle getBounds() {
		return null;
	}

	/**
	 * Returns the control that is used to fill the bounds of the item when the
	 * item is a <code>SEPARATOR</code>.
	 * 
	 * @return the control
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Control getControl() {
		checkWidget();
		return control;
	}

	/**
	 * Returns the receiver's disabled image if it has one, or null if it does
	 * not.
	 * <p>
	 * The disabled image is displayed when the receiver is disabled.
	 * </p>
	 * 
	 * @return the receiver's disabled image
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Image getDisabledImage() {
		checkWidget();
		return disabledImage;
	}

	/**
	 * Returns <code>true</code> if the receiver is enabled, and
	 * <code>false</code> otherwise. A disabled control is typically not
	 * selectable from the user interface and draws with an inactive or "grayed"
	 * look.
	 * 
	 * @return the receiver's enabled state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #isEnabled
	 */
	public boolean getEnabled() {
		return false;
	}

	/**
	 * Returns the receiver's hot image if it has one, or null if it does not.
	 * <p>
	 * The hot image is displayed when the mouse enters the receiver.
	 * </p>
	 * 
	 * @return the receiver's hot image
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Image getHotImage() {
		checkWidget();
		return hotImage;
	}

	/**
	 * Returns the receiver's parent, which must be a <code>ToolBar</code>.
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
	public ToolBar getParent() {
		checkWidget();
		if (parent == null)
			error(SWT.ERROR_WIDGET_DISPOSED);
		return parent;
	}

	/**
	 * Returns <code>true</code> if the receiver is selected, and false
	 * otherwise.
	 * <p>
	 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>, it
	 * is selected when it is checked (which some platforms draw as a pushed in
	 * button). If the receiver is of any other type, this method returns false.
	 * </p>
	 * 
	 * @return the selection state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getSelection() {
		return false;
	}

	/**
	 * Returns the receiver's tool tip text, or null if it has not been set.
	 * 
	 * @return the receiver's tool tip text
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public String getToolTipText() {
		checkWidget();
		return toolTipText;
	}

	/**
	 * Gets the width of the receiver.
	 * 
	 * @return the width
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getWidth() {
		return 0;
	}

	boolean hasFocus() {
		return false;
	}

	void hookEvents() {
	}

	/**
	 * Returns <code>true</code> if the receiver is enabled and all of the
	 * receiver's ancestors are enabled, and <code>false</code> otherwise. A
	 * disabled control is typically not selectable from the user interface and
	 * draws with an inactive or "grayed" look.
	 * 
	 * @return the receiver's enabled state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #getEnabled
	 */
	public boolean isEnabled() {
		checkWidget();
		return getEnabled() && parent.isEnabled();
	}

	boolean isTabGroup() {
		ToolItem[] tabList = parent._getTabItemList();
		if (tabList != null) {
			for (int i = 0; i < tabList.length; i++) {
				if (tabList[i] == this)
					return true;
			}
		}
		if ((style & SWT.SEPARATOR) != 0)
			return true;
		int index = parent.indexOf(this);
		if (index == 0)
			return true;
		ToolItem previous = parent.getItem(index - 1);
		return (previous.getStyle() & SWT.SEPARATOR) != 0;
	}

	void register() {
		super.register();
		if (labelHandle != 0)
			display.addWidget(labelHandle, this);
	}

	void releaseHandle() {
		super.releaseHandle();
		boxHandle = arrowHandle = separatorHandle = labelHandle = imageHandle = 0;
	}

	void releaseWidget() {
		super.releaseWidget();
		if (parent.lastFocus == this)
			parent.lastFocus = null;
		parent = null;
		control = null;
		hotImage = disabledImage = null;
		toolTipText = null;
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the control is selected by the user.
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
	 * @see #addSelectionListener
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

	void resizeControl() {
	}

	/**
	 * Sets the control that is used to fill the bounds of the item when the
	 * item is a <code>SEPARATOR</code>.
	 * 
	 * @param control
	 *            the new control
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the control has been
	 *                disposed</li>
	 *                <li>ERROR_INVALID_PARENT - if the control is not in the
	 *                same widget tree</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setControl(Control control) {
		checkWidget();
		if (control != null) {
			if (control.isDisposed())
				error(SWT.ERROR_INVALID_ARGUMENT);
			if (control.parent != parent)
				error(SWT.ERROR_INVALID_PARENT);
		}
		if ((style & SWT.SEPARATOR) == 0)
			return;
		if (this.control == control)
			return;
		this.control = control;
		parent.relayout();
	}

	/**
	 * Sets the receiver's disabled image to the argument, which may be null
	 * indicating that no disabled image should be displayed.
	 * <p>
	 * The disabled image is displayed when the receiver is disabled.
	 * </p>
	 * 
	 * @param image
	 *            the disabled image to display on the receiver (may be null)
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
	public void setDisabledImage(Image image) {
		checkWidget();
		if ((style & SWT.SEPARATOR) != 0)
			return;
		disabledImage = image;
	}

	/**
	 * Enables the receiver if the argument is <code>true</code>, and disables
	 * it otherwise.
	 * <p>
	 * A disabled control is typically not selectable from the user interface
	 * and draws with an inactive or "grayed" look.
	 * </p>
	 * 
	 * @param enabled
	 *            the new enabled state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setEnabled(boolean enabled) {
	}

	boolean setFocus() {
		return false;
	}

	void setFontDescription(long /* int */font) {
	}

	/**
	 * Sets the receiver's hot image to the argument, which may be null
	 * indicating that no hot image should be displayed.
	 * <p>
	 * The hot image is displayed when the mouse enters the receiver.
	 * </p>
	 * 
	 * @param image
	 *            the hot image to display on the receiver (may be null)
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
	public void setHotImage(Image image) {
		checkWidget();
		if ((style & SWT.SEPARATOR) != 0)
			return;
		hotImage = image;
		if (image != null) {
			ImageList imageList = parent.imageList;
			if (imageList == null)
				imageList = parent.imageList = new ImageList();
			int imageIndex = imageList.indexOf(image);
			if (imageIndex == -1) {
				imageIndex = imageList.add(image);
			} else {
				imageList.put(imageIndex, image);
			}
		}
	}

	public void setImage(Image image) {
	}

	void setOrientation(boolean create) {
	}

	/**
	 * Sets the selection state of the receiver.
	 * <p>
	 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>, it
	 * is selected when it is checked (which some platforms draw as a pushed in
	 * button).
	 * </p>
	 * 
	 * @param selected
	 *            the new selection state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelection(boolean selected) {
	}

	boolean setTabItemFocus(boolean next) {
		return setFocus();
	}

	/**
	 * Sets the receiver's text. The string may include the mnemonic character.
	 * </p>
	 * <p>
	 * Mnemonics are indicated by an '&amp;' that causes the next character to
	 * be the mnemonic. When the user presses a key sequence that matches the
	 * mnemonic, a selection event occurs. On most platforms, the mnemonic
	 * appears underlined but may be emphasised in a platform specific manner.
	 * The mnemonic indicator character '&amp;' can be escaped by doubling it in
	 * the string, causing a single '&amp;' to be displayed.
	 * </p>
	 * 
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
	public void setText(String string) {
	}

	/**
	 * Sets the receiver's tool tip text to the argument, which may be null
	 * indicating that the default tool tip for the control will be shown. For a
	 * control that has a default tool tip, such as the Tree control on Windows,
	 * setting the tool tip text to an empty string replaces the default,
	 * causing no tool tip text to be shown.
	 * <p>
	 * The mnemonic indicator (character '&amp;') is not displayed in a tool
	 * tip. To display a single '&amp;' in the tool tip, the character '&amp;'
	 * can be escaped by doubling it in the string.
	 * </p>
	 * 
	 * @param string
	 *            the new tool tip text (or null)
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setToolTipText(String string) {
	}

	/**
	 * Sets the width of the receiver, for <code>SEPARATOR</code> ToolItems.
	 * 
	 * @param width
	 *            the new width. If the new value is <code>SWT.DEFAULT</code>,
	 *            the width is a fixed-width area whose amount is determined by
	 *            the platform. If the new value is 0 a vertical or horizontal
	 *            line will be drawn, depending on the setting of the
	 *            corresponding style bit (<code>SWT.VERTICAL</code> or
	 *            <code>SWT.HORIZONTAL</code>). If the new value is
	 *            <code>SWT.SEPARATOR_FILL</code> a variable-width space is
	 *            inserted that acts as a spring between the two adjoining items
	 *            which will push them out to the extent of the containing
	 *            ToolBar.
	 * 
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setWidth(int width) {
	}

	void showWidget(int index) {
	}
}
