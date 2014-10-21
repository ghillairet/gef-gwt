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

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Instances of this class represent a selectable user interface object that
 * issues notification when pressed and released.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>CHECK, CASCADE, PUSH, RADIO, SEPARATOR</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Arm, Help, Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles CHECK, CASCADE, PUSH, RADIO and SEPARATOR may be
 * specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class MenuItem extends Item {
	Menu parent, menu;
	long /* int */groupHandle;
	int accelerator, userId;
	private java.util.List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	private boolean enabled = true;

	/**
	 * Constructs a new instance of this class given its parent (which must be a
	 * <code>Menu</code>) and a style value describing its behavior and
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
	 *            a menu control which will be the parent of the new instance
	 *            (cannot be null)
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
	 * @see SWT#CHECK
	 * @see SWT#CASCADE
	 * @see SWT#PUSH
	 * @see SWT#RADIO
	 * @see SWT#SEPARATOR
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public MenuItem(Menu parent, int style) {
		super(parent, checkStyle(style));
		this.parent = parent;
		createWidget(parent.getItemCount());
		parent.addMenuItem(this);
	}

	/**
	 * Constructs a new instance of this class given its parent (which must be a
	 * <code>Menu</code>), a style value describing its behavior and appearance,
	 * and the index at which to place it in the items maintained by its parent.
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
	 *            a menu control which will be the parent of the new instance
	 *            (cannot be null)
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
	 * @see SWT#CHECK
	 * @see SWT#CASCADE
	 * @see SWT#PUSH
	 * @see SWT#RADIO
	 * @see SWT#SEPARATOR
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public MenuItem(Menu parent, int style, int index) {
		super(parent, checkStyle(style));
		this.parent = parent;
		int count = parent.getItemCount();
		if (!(0 <= index && index <= count)) {
			error(SWT.ERROR_INVALID_RANGE);
		}
		createWidget(index);
		parent.addMenuItem(this);
	}

	void addAccelerator(long /* int */accelGroup) {
		updateAccelerator(accelGroup, true);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the arm events are generated for the control, by sending it one of
	 * the messages defined in the <code>ArmListener</code> interface.
	 * 
	 * @param listener
	 *            the listener which should be notified
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
	 * @see ArmListener
	 * @see #removeArmListener
	 */
	public void addArmListener(ArmListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Arm, typedListener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the help events are generated for the control, by sending it one of
	 * the messages defined in the <code>HelpListener</code> interface.
	 * 
	 * @param listener
	 *            the listener which should be notified
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
	 * @see HelpListener
	 * @see #removeHelpListener
	 */
	public void addHelpListener(HelpListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Help, typedListener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the menu item is selected by the user, by sending it one of the
	 * messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * When <code>widgetSelected</code> is called, the stateMask field of the
	 * event object is valid. <code>widgetDefaultSelected</code> is not called.
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
	 *            the listener which should be notified when the menu item is
	 *            selected by the user
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
		selectionListeners.add(listener);
	}

	static int checkStyle(int style) {
		return checkBits(style, SWT.PUSH, SWT.CHECK, SWT.RADIO, SWT.SEPARATOR,
				SWT.CASCADE, 0);
	}

	protected void checkSubclass() {
		if (!isValidSubclass())
			error(SWT.ERROR_INVALID_SUBCLASS);
	}

	void createHandle(int index) {
	}

	/**
	 * Returns the widget accelerator. An accelerator is the bit-wise OR of zero
	 * or more modifier masks and a key. Examples:
	 * <code>SWT.CONTROL | SWT.SHIFT | 'T', SWT.ALT | SWT.F2</code>. The default
	 * value is zero, indicating that the menu item does not have an
	 * accelerator.
	 * 
	 * @return the accelerator or 0
	 * 
	 *         </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getAccelerator() {
		checkWidget();
		return accelerator;
	}

	long /* int */getAccelGroup() {
		Menu menu = parent;
		while (menu != null && menu.cascade != null) {
			menu = menu.cascade.parent;
		}
		if (menu == null)
			return 0;
		Decorations shell = menu.parent;
		return shell.menuBar == menu ? shell.accelGroup : 0;
	}

	/* public */Rectangle getBounds() {
		return null;
	}

	/**
	 * Returns <code>true</code> if the receiver is enabled, and
	 * <code>false</code> otherwise. A disabled menu item is typically not
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
		// checkWidget();
		// return OS.GTK_WIDGET_SENSITIVE(handle);
		return this.enabled;
	}

	/**
	 * Gets the identifier associated with the receiver.
	 * 
	 * @return the receiver's identifier
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.7
	 */
	public int getID() {
		checkWidget();
		return userId;
	}

	/**
	 * Returns the receiver's cascade menu if it has one or null if it does not.
	 * Only <code>CASCADE</code> menu items can have a pull down menu. The
	 * sequence of key strokes, button presses and/or button releases that are
	 * used to request a pull down menu is platform specific.
	 * 
	 * @return the receiver's menu
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Menu getMenu() {
		checkWidget();
		return menu;
	}

	String getNameText() {
		if ((style & SWT.SEPARATOR) != 0)
			return "|";
		return super.getNameText();
	}

	/**
	 * Returns the receiver's parent, which must be a <code>Menu</code>.
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
	public Menu getParent() {
		checkWidget();
		return parent;
	}

	/**
	 * Returns <code>true</code> if the receiver is selected, and false
	 * otherwise.
	 * <p>
	 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>, it
	 * is selected when it is checked.
	 * 
	 * @return the selection state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li> <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getSelection() {
		return false;
	}

	void hookEvents() {
	}

	/**
	 * Returns <code>true</code> if the receiver is enabled and all of the
	 * receiver's ancestors are enabled, and <code>false</code> otherwise. A
	 * disabled menu item is typically not selectable from the user interface
	 * and draws with an inactive or "grayed" look.
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
		return getEnabled() && parent.isEnabled();
	}

	void releaseChildren(boolean destroy) {
		if (menu != null) {
			menu.release(false);
			menu = null;
		}
		super.releaseChildren(destroy);
	}

	void releaseParent() {
		super.releaseParent();
		if (menu != null) {
			if (menu.selectedItem == this)
				menu.selectedItem = null;
			menu.dispose();
		}
		menu = null;
	}

	void releaseWidget() {
		if (parent != null) {
			parent.removeMenuItem(this);
		}
	}

	void removeAccelerator(long /* int */accelGroup) {
		updateAccelerator(accelGroup, false);
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the arm events are generated for the control.
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
	 * @see ArmListener
	 * @see #addArmListener
	 */
	public void removeArmListener(ArmListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.Arm, listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the help events are generated for the control.
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
	 * @see HelpListener
	 * @see #addHelpListener
	 */
	public void removeHelpListener(HelpListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.Help, listener);
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

	void reskinChildren(int flags) {
		if (menu != null) {
			menu.reskin(flags);
		}
		super.reskinChildren(flags);
	}

	/**
	 * Sets the widget accelerator. An accelerator is the bit-wise OR of zero or
	 * more modifier masks and a key. Examples:
	 * <code>SWT.MOD1 | SWT.MOD2 | 'T', SWT.MOD3 | SWT.F2</code>.
	 * <code>SWT.CONTROL | SWT.SHIFT | 'T', SWT.ALT | SWT.F2</code>. The default
	 * value is zero, indicating that the menu item does not have an
	 * accelerator.
	 * 
	 * @param accelerator
	 *            an integer that is the bit-wise OR of masks and a key
	 * 
	 *            </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setAccelerator(int accelerator) {
		checkWidget();
		if (this.accelerator == accelerator)
			return;
		long /* int */accelGroup = getAccelGroup();
		if (accelGroup != 0)
			removeAccelerator(accelGroup);
		this.accelerator = accelerator;
		if (accelGroup != 0)
			addAccelerator(accelGroup);
	}

	/**
	 * Enables the receiver if the argument is <code>true</code>, and disables
	 * it otherwise. A disabled menu item is typically not selectable from the
	 * user interface and draws with an inactive or "grayed" look.
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
		this.enabled = enabled;
	}

	/**
	 * Sets the identifier associated with the receiver to the argument.
	 * 
	 * @param id
	 *            the new identifier. This must be a non-negative value.
	 *            System-defined identifiers are negative values.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if called with an
	 *                negative-valued argument.</li>
	 *                </ul>
	 * 
	 * @since 3.7
	 */
	public void setID(int id) {
		checkWidget();
		if (id < 0)
			error(SWT.ERROR_INVALID_ARGUMENT);
		userId = id;
	}

	/**
	 * Sets the image the receiver will display to the argument.
	 * <p>
	 * Note: This operation is a hint and is not supported on platforms that do
	 * not have this concept (for example, Windows NT). Furthermore, some
	 * platforms (such as GTK), cannot display both a check box and an image at
	 * the same time. Instead, they hide the image and display the check box.
	 * </p>
	 * 
	 * @param image
	 *            the image to display
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setImage(Image image) {
	}

	/**
	 * Sets the receiver's pull down menu to the argument. Only
	 * <code>CASCADE</code> menu items can have a pull down menu. The sequence
	 * of key strokes, button presses and/or button releases that are used to
	 * request a pull down menu is platform specific.
	 * <p>
	 * Note: Disposing of a menu item that has a pull down menu will dispose of
	 * the menu. To avoid this behavior, set the menu to null before the menu
	 * item is disposed.
	 * </p>
	 * 
	 * @param menu
	 *            the new pull down menu
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_MENU_NOT_DROP_DOWN - if the menu is not a drop
	 *                down menu</li>
	 *                <li>ERROR_MENUITEM_NOT_CASCADE - if the menu item is not a
	 *                <code>CASCADE</code></li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the menu has been disposed
	 *                </li>
	 *                <li>ERROR_INVALID_PARENT - if the menu is not in the same
	 *                widget tree</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	void setOrientation(boolean create) {
	}

	/**
	 * Sets the selection state of the receiver.
	 * <p>
	 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>, it
	 * is selected when it is checked.
	 * 
	 * @param selected
	 *            the new selection state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li> <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelection(boolean selected) {
		if (selected) {
			for (SelectionListener sl : selectionListeners) {
				Event e = new Event();
				e.widget = this;
				SelectionEvent selectionEvent = new SelectionEvent(e);
				selectionEvent.item = this;
				sl.widgetSelected(selectionEvent);
			}
		} else {

		}

		Event event = new Event();
		event.type = SWT.Selection;
		event.widget = this;
		event.display = Display.getDefault();
		sendEvent(event);
	}

	/**
	 * Sets the receiver's text. The string may include the mnemonic character
	 * and accelerator text.
	 * <p>
	 * Mnemonics are indicated by an '&amp;' that causes the next character to
	 * be the mnemonic. When the user presses a key sequence that matches the
	 * mnemonic, a selection event occurs. On most platforms, the mnemonic
	 * appears underlined but may be emphasised in a platform specific manner.
	 * The mnemonic indicator character '&amp;' can be escaped by doubling it in
	 * the string, causing a single '&amp;' to be displayed.
	 * </p>
	 * <p>
	 * Accelerator text is indicated by the '\t' character. On platforms that
	 * support accelerator text, the text that follows the '\t' character is
	 * displayed to the user, typically indicating the key stroke that will
	 * cause the item to become selected. On most platforms, the accelerator
	 * text appears right aligned in the menu. Setting the accelerator text does
	 * not install the accelerator key sequence. The accelerator key sequence is
	 * installed using #setAccelerator.
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
	 * 
	 * @see #setAccelerator
	 */
	public void setText(String string) {
		this.text = string;
	}

	void updateAccelerator(long /* int */accelGroup, boolean add) {
	}

	@Override
	public void addListener(int i, Listener listener) {
		super.addListener(i, listener);
		if (listener instanceof SelectionListener && i == SWT.Selection) {
			addSelectionListener((SelectionListener) listener);
		}

	}
}
