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
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Instances of this class implement rubber banding rectangles that are drawn
 * onto a parent <code>Composite</code> or <code>Display</code>. These
 * rectangles can be specified to respond to mouse and key events by either
 * moving or resizing themselves accordingly. Trackers are typically used to
 * represent window geometries in a lightweight manner.
 * 
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LEFT, RIGHT, UP, DOWN, RESIZE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Move, Resize</dd>
 * </dl>
 * <p>
 * Note: Rectangle move behavior is assumed unless RESIZE is specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/snippets/#tracker">Tracker
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Tracker extends Widget {
	Composite parent;
	Cursor cursor;
	long /* int */lastCursor, window;
	boolean tracking, cancelled, grabbed, stippled;
	Rectangle[] rectangles = new Rectangle[0], proportions = rectangles;
	Rectangle bounds;
	int cursorOrientation = SWT.NONE;
	int oldX, oldY;

	final static int STEPSIZE_SMALL = 1;
	final static int STEPSIZE_LARGE = 9;

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
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
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
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#UP
	 * @see SWT#DOWN
	 * @see SWT#RESIZE
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public Tracker(Composite parent, int style) {
		super(parent, checkStyle(style));
		this.parent = parent;
	}

	/**
	 * Constructs a new instance of this class given the display to create it on
	 * and a style value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must
	 * be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * <p>
	 * Note: Currently, null can be passed in for the display argument. This has
	 * the effect of creating the tracker on the currently active display if
	 * there is one. If there is no current display, the tracker is created on a
	 * "default" display. <b>Passing in null as the display argument is not
	 * considered to be good coding style, and may not be supported in a future
	 * release of SWT.</b>
	 * </p>
	 * 
	 * @param display
	 *            the display to create the tracker on
	 * @param style
	 *            the style of control to construct
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 * 
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#UP
	 * @see SWT#DOWN
	 * @see SWT#RESIZE
	 */
	public Tracker(Display display, int style) {
		if (display == null)
			display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		if (!display.isValidThread()) {
			error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}
		this.style = checkStyle(style);
		this.display = display;
		reskinWidget();
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the control is moved or resized, by sending it one of the messages
	 * defined in the <code>ControlListener</code> interface.
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
	 * @see ControlListener
	 * @see #removeControlListener
	 */
	public void addControlListener(ControlListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Resize, typedListener);
		addListener(SWT.Move, typedListener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when keys are pressed and released on the system keyboard, by sending it
	 * one of the messages defined in the <code>KeyListener</code> interface.
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
	 * @see KeyListener
	 * @see #removeKeyListener
	 */
	public void addKeyListener(KeyListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.KeyUp, typedListener);
		addListener(SWT.KeyDown, typedListener);
	}

	/**
	 * Stops displaying the tracker rectangles. Note that this is not considered
	 * to be a cancelation by the user.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void close() {
		checkWidget();
		tracking = false;
	}

	static int checkStyle(int style) {
		if ((style & (SWT.LEFT | SWT.RIGHT | SWT.UP | SWT.DOWN)) == 0) {
			style |= SWT.LEFT | SWT.RIGHT | SWT.UP | SWT.DOWN;
		}
		return style;
	}

	Rectangle computeBounds() {
		if (rectangles.length == 0)
			return null;
		int xMin = rectangles[0].x;
		int yMin = rectangles[0].y;
		int xMax = rectangles[0].x + rectangles[0].width;
		int yMax = rectangles[0].y + rectangles[0].height;

		for (int i = 1; i < rectangles.length; i++) {
			if (rectangles[i].x < xMin)
				xMin = rectangles[i].x;
			if (rectangles[i].y < yMin)
				yMin = rectangles[i].y;
			int rectRight = rectangles[i].x + rectangles[i].width;
			if (rectRight > xMax)
				xMax = rectRight;
			int rectBottom = rectangles[i].y + rectangles[i].height;
			if (rectBottom > yMax)
				yMax = rectBottom;
		}

		return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
	}

	Rectangle[] computeProportions(Rectangle[] rects) {
		Rectangle[] result = new Rectangle[rects.length];
		bounds = computeBounds();
		if (bounds != null) {
			for (int i = 0; i < rects.length; i++) {
				int x = 0, y = 0, width = 0, height = 0;
				if (bounds.width != 0) {
					x = (rects[i].x - bounds.x) * 100 / bounds.width;
					width = rects[i].width * 100 / bounds.width;
				} else {
					width = 100;
				}
				if (bounds.height != 0) {
					y = (rects[i].y - bounds.y) * 100 / bounds.height;
					height = rects[i].height * 100 / bounds.height;
				} else {
					height = 100;
				}
				result[i] = new Rectangle(x, y, width, height);
			}
		}
		return result;
	}

	/**
	 * Returns the bounds that are being drawn, expressed relative to the parent
	 * widget. If the parent is a <code>Display</code> then these are screen
	 * coordinates.
	 * 
	 * @return the bounds of the Rectangles being drawn
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Rectangle[] getRectangles() {
		checkWidget();
		Rectangle[] result = new Rectangle[rectangles.length];
		for (int i = 0; i < rectangles.length; i++) {
			Rectangle current = rectangles[i];
			result[i] = new Rectangle(current.x, current.y, current.width,
					current.height);
		}
		return result;
	}

	/**
	 * Returns <code>true</code> if the rectangles are drawn with a stippled
	 * line, <code>false</code> otherwise.
	 * 
	 * @return the stippled effect of the rectangles
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getStippled() {
		checkWidget();
		return stippled;
	}

	/**
	 * Displays the Tracker rectangles for manipulation by the user. Returns
	 * when the user has either finished manipulating the rectangles or has
	 * cancelled the Tracker.
	 * 
	 * @return <code>true</code> if the user did not cancel the Tracker,
	 *         <code>false</code> otherwise
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean open() {
		return false;
	}

	void releaseWidget() {
		super.releaseWidget();
		parent = null;
		rectangles = proportions = null;
		bounds = null;
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the control is moved or resized.
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
	 * @see ControlListener
	 * @see #addControlListener
	 */
	public void removeControlListener(ControlListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.Resize, listener);
		eventTable.unhook(SWT.Move, listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when keys are pressed and released on the system keyboard.
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
	 * @see KeyListener
	 * @see #addKeyListener
	 */
	public void removeKeyListener(KeyListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.KeyUp, listener);
		eventTable.unhook(SWT.KeyDown, listener);
	}

	/**
	 * Sets the <code>Cursor</code> of the Tracker. If this cursor is
	 * <code>null</code> then the cursor reverts to the default.
	 * 
	 * @param newCursor
	 *            the new <code>Cursor</code> to display
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setCursor(Cursor value) {
		checkWidget();
		cursor = value;
	}

	/**
	 * Specifies the rectangles that should be drawn, expressed relative to the
	 * parent widget. If the parent is a Display then these are screen
	 * coordinates.
	 * 
	 * @param rectangles
	 *            the bounds of the rectangles to be drawn
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the set of rectangles is null
	 *                or contains a null rectangle</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setRectangles(Rectangle[] rectangles) {
		checkWidget();
		if (rectangles == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		int length = rectangles.length;
		this.rectangles = new Rectangle[length];
		for (int i = 0; i < length; i++) {
			Rectangle current = rectangles[i];
			if (current == null)
				error(SWT.ERROR_NULL_ARGUMENT);
			this.rectangles[i] = new Rectangle(current.x, current.y,
					current.width, current.height);
		}
		proportions = computeProportions(rectangles);
	}

	/**
	 * Changes the appearance of the line used to draw the rectangles.
	 * 
	 * @param stippled
	 *            <code>true</code> if rectangle should appear stippled
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setStippled(boolean stippled) {
		checkWidget();
		this.stippled = stippled;
	}
}
