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
import org.eclipse.swt.graphics.Rectangle;

/**
 * This class is the abstract superclass of all classes which represent controls
 * that have standard scroll bars.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>H_SCROLL, V_SCROLL</dd>
 * <dt><b>Events:</b>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em> within the
 * SWT implementation.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class Scrollable extends Control {
	long /* int */scrolledHandle;
	ScrollBar horizontalBar, verticalBar;

	/**
	 * Prevents uninitialized instances from being created outside the package.
	 */
	Scrollable() {
	}

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
	 * @see SWT#H_SCROLL
	 * @see SWT#V_SCROLL
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public Scrollable(Composite parent, int style) {
		super(parent, style);
	}

	Scrollable(com.google.gwt.user.client.ui.Widget gwtWidget, Widget parent,
			int style) {
		super(gwtWidget, parent, style);
	}

	/**
	 * Given a desired <em>client area</em> for the receiver (as described by
	 * the arguments), returns the bounding rectangle which would be required to
	 * produce that client area.
	 * <p>
	 * In other words, it returns a rectangle such that, if the receiver's
	 * bounds were set to that rectangle, the area of the receiver which is
	 * capable of displaying data (that is, not covered by the "trimmings")
	 * would be the rectangle described by the arguments (relative to the
	 * receiver's parent).
	 * </p>
	 * 
	 * @param x
	 *            the desired x coordinate of the client area
	 * @param y
	 *            the desired y coordinate of the client area
	 * @param width
	 *            the desired width of the client area
	 * @param height
	 *            the desired height of the client area
	 * @return the required bounds to produce the given client area
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #getClientArea
	 */
	public Rectangle computeTrim(int x, int y, int width, int height) {
		checkWidget();
		int border = 0;
		if (fixedHandle != 0)
			// border += OS.gtk_container_get_border_width(fixedHandle);
			if (scrolledHandle != 0)
				;
		// border += OS.gtk_container_get_border_width(scrolledHandle);
		int trimX = x - border, trimY = y - border;
		int trimWidth = width + (border * 2), trimHeight = height
				+ (border * 2);
		trimHeight += hScrollBarWidth();
		trimWidth += vScrollBarWidth();
		if (scrolledHandle != 0) {
			// if (OS.gtk_scrolled_window_get_shadow_type(scrolledHandle) !=
			// OS.GTK_SHADOW_NONE) {
			// long /* int */style = OS.gtk_widget_get_style(scrolledHandle);
			// int xthickness = OS.gtk_style_get_xthickness(style);
			// int ythickness = OS.gtk_style_get_ythickness(style);
			// trimX -= xthickness;
			// trimY -= ythickness;
			// trimWidth += xthickness * 2;
			// trimHeight += ythickness * 2;
			// }
		}
		return new Rectangle(trimX, trimY, trimWidth, trimHeight);
	}

	ScrollBar createScrollBar(int style) {
		ScrollBar bar = new ScrollBar(this, style);
		bar.style = style;
		return bar;
	}

	void createWidget(int index) {
		super.createWidget(index);
		if ((style & SWT.H_SCROLL) != 0)
			horizontalBar = createScrollBar(SWT.H_SCROLL);
		if ((style & SWT.V_SCROLL) != 0)
			verticalBar = createScrollBar(SWT.V_SCROLL);
	}

	void destroyScrollBar(ScrollBar bar) {
		setScrollBarVisible(bar, false);
	}

	public int getBorderWidth() {
		return 0;
	}

	/**
	 * Returns a rectangle which describes the area of the receiver which is
	 * capable of displaying data (that is, not covered by the "trimmings").
	 * 
	 * @return the client area
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #computeTrim
	 */
	public Rectangle getClientArea() {
		com.google.gwt.user.client.ui.Widget gwtWidget = getGwtWidget();
		int clientWidth = gwtWidget.getElement().getOffsetWidth();
		int clientHeight = gwtWidget.getElement().getOffsetHeight();
		return new Rectangle(0, 0, clientWidth, clientHeight);
	}

	/**
	 * Returns the receiver's horizontal scroll bar if it has one, and null if
	 * it does not.
	 * 
	 * @return the horizontal scroll bar (or null)
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public ScrollBar getHorizontalBar() {
		checkWidget();
		return horizontalBar = createScrollBar(SWT.H_SCROLL);
	}

	/**
	 * Returns the receiver's vertical scroll bar if it has one, and null if it
	 * does not.
	 * 
	 * @return the vertical scroll bar (or null)
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public ScrollBar getVerticalBar() {
		checkWidget();
		return verticalBar = createScrollBar(SWT.V_SCROLL);
	}

	int hScrollBarWidth() {
		// if (horizontalBar == null)
		// return 0;
		// long /* int */hBarHandle = OS
		// .GTK_SCROLLED_WINDOW_HSCROLLBAR(scrolledHandle);
		// if (hBarHandle == 0)
		// return 0;
		// GtkRequisition requisition = new GtkRequisition();
		// OS.gtk_widget_size_request(hBarHandle, requisition);
		// int spacing =
		// OS.GTK_SCROLLED_WINDOW_SCROLLBAR_SPACING(scrolledHandle);
		// return requisition.height + spacing;
		return 0;
	}

	void reskinChildren(int flags) {
		if (horizontalBar != null)
			horizontalBar.reskin(flags);
		if (verticalBar != null)
			verticalBar.reskin(flags);
		super.reskinChildren(flags);
	}

	boolean sendLeaveNotify() {
		return scrolledHandle != 0;
	}

	void setOrientation(boolean create) {
	}

	boolean setScrollBarVisible(ScrollBar bar, boolean visible) {
		return true;
	}

	void redrawBackgroundImage() {
	}

	void redrawWidget(int x, int y, int width, int height, boolean redrawAll,
			boolean all, boolean trim) {
	}

	void register() {
		super.register();
		if (scrolledHandle != 0)
			display.addWidget(scrolledHandle, this);
	}

	void releaseHandle() {
		super.releaseHandle();
		scrolledHandle = 0;
	}

	void releaseChildren(boolean destroy) {
		if (horizontalBar != null) {
			horizontalBar.release(false);
			horizontalBar = null;
		}
		if (verticalBar != null) {
			verticalBar.release(false);
			verticalBar = null;
		}
		super.releaseChildren(destroy);
	}

	int vScrollBarWidth() {
		return 0;
	}
}
