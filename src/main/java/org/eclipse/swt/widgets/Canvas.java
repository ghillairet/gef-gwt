/*******************************************************************************
 * Copyright (c) 2000, 2008, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Instances of this class provide a surface for drawing arbitrary graphics.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * This class may be subclassed by custom control implementors who are building
 * controls that are <em>not</em> constructed from aggregates of other controls.
 * That is, they are either painted using SWT graphics calls or are handled by
 * native methods.
 * </p>
 * 
 * @see Composite
 * @see <a href="http://www.eclipse.org/swt/snippets/#canvas">Canvas
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 */
public class Canvas extends Composite {
	Caret caret;
	IME ime;
	protected com.google.gwt.canvas.client.Canvas canvas;
	private LayoutPanel simplePanel;

	Canvas() {
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
	 *                </ul>
	 * 
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public Canvas(Composite parent, int style) {
		super(com.google.gwt.canvas.client.Canvas.createIfSupported(), parent, checkStyle(style));
		this.canvas = (com.google.gwt.canvas.client.Canvas) getGwtWidget();
		com.google.gwt.user.client.ui.Widget parentGwtWidget = parent.getGwtWidget();

		LayoutPanel parentWidget = (LayoutPanel) parentGwtWidget;
		LayoutPanel innerPanel = new LayoutPanel() {
			@Override
			public void onResize() {
				super.onResize();
				Canvas.this.onResize();
			}
		};

		this.canvas.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							Canvas.this.onResize();
						}
					});
				}
			}
		});

		this.canvas.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				turnTextSelectionOff();
			}
		});

		this.canvas.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				turnTextSelectionOn();
			}
		});

		parentWidget.add(innerPanel);
		ScrollPanel sp = new ScrollPanel();
		sp.getElement().setId("SCROLL_PANEL");
		setScrollPanel(sp);
		simplePanel = new LayoutPanel();
		simplePanel.getElement().setId("SIMPLE_PANEL");
		sp.add(simplePanel);
		setSimplePanel(simplePanel);
		innerPanel.add(sp);
		innerPanel.add(canvas);
	}

	private native void turnTextSelectionOff()/*-{
												$doc.onselectstart = function(){ return false; };
												}-*/;

	private native void turnTextSelectionOn()/*-{
												$doc.onselectstart = function(){ return true; };
												}-*/;

	public Canvas(com.google.gwt.canvas.client.Canvas canvas) {
		super(canvas, null, SWT.None);
		this.canvas = canvas;
	}

	@Override
	public Rectangle getClientArea() {
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		return new Rectangle(0, 0, width, height);
	}

	/**
	 * Fills the interior of the rectangle specified by the arguments, with the
	 * receiver's background.
	 * 
	 * @param gc
	 *            the gc where the rectangle is to be filled
	 * @param x
	 *            the x coordinate of the rectangle to be filled
	 * @param y
	 *            the y coordinate of the rectangle to be filled
	 * @param width
	 *            the width of the rectangle to be filled
	 * @param height
	 *            the height of the rectangle to be filled
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the gc is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the gc has been disposed</li>
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
	public void drawBackground(GC gc, int x, int y, int width, int height) {
		drawBackground(gc, x, y, width, height, 0, 0);
	}

	/**
	 * Returns the caret.
	 * <p>
	 * The caret for the control is automatically hidden and shown when the
	 * control is painted or resized, when focus is gained or lost and when an
	 * the control is scrolled. To avoid drawing on top of the caret, the
	 * programmer must hide and show the caret when drawing in the window any
	 * other time.
	 * </p>
	 * 
	 * @return the caret for the receiver, may be null
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Caret getCaret() {
		checkWidget();
		return caret;
	}

	/**
	 * Returns the IME.
	 * 
	 * @return the IME
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.4
	 */
	public IME getIME() {
		checkWidget();
		return ime;
	}

	void redrawWidget(int x, int y, int width, int height, boolean redrawAll,
			boolean all, boolean trim) {
		boolean isFocus = caret != null && caret.isFocusCaret();
		if (isFocus)
			caret.killFocus();
		super.redrawWidget(x, y, width, height, redrawAll, all, trim);
		if (isFocus)
			caret.setFocus();
	}

	void releaseChildren(boolean destroy) {
		if (caret != null) {
			caret.release(false);
			caret = null;
		}
		if (ime != null) {
			ime.release(false);
			ime = null;
		}
		super.releaseChildren(destroy);
	}

	void reskinChildren(int flags) {
		if (caret != null)
			caret.reskin(flags);
		if (ime != null)
			ime.reskin(flags);
		super.reskinChildren(flags);
	}

	public void onResize() {
	}

	/**
	 * Scrolls a rectangular area of the receiver by first copying the source
	 * area to the destination and then causing the area of the source which is
	 * not covered by the destination to be repainted. Children that intersect
	 * the rectangle are optionally moved during the operation. In addition, all
	 * outstanding paint events are flushed before the source area is copied to
	 * ensure that the contents of the canvas are drawn correctly.
	 * 
	 * @param destX
	 *            the x coordinate of the destination
	 * @param destY
	 *            the y coordinate of the destination
	 * @param x
	 *            the x coordinate of the source
	 * @param y
	 *            the y coordinate of the source
	 * @param width
	 *            the width of the area
	 * @param height
	 *            the height of the area
	 * @param all
	 *            <code>true</code>if children should be scrolled, and
	 *            <code>false</code> otherwise
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void scroll(int destX, int destY, int x, int y, int width,
			int height, boolean all) {
	}

	/**
	 * Sets the receiver's caret.
	 * <p>
	 * The caret for the control is automatically hidden and shown when the
	 * control is painted or resized, when focus is gained or lost and when an
	 * the control is scrolled. To avoid drawing on top of the caret, the
	 * programmer must hide and show the caret when drawing in the window any
	 * other time.
	 * </p>
	 * 
	 * @param caret
	 *            the new caret for the receiver, may be null
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the caret has been
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
	public void setCaret(Caret caret) {
		checkWidget();
		Caret newCaret = caret;
		Caret oldCaret = this.caret;
		this.caret = newCaret;
		if (hasFocus()) {
			if (oldCaret != null)
				oldCaret.killFocus();
			if (newCaret != null) {
				if (newCaret.isDisposed())
					error(SWT.ERROR_INVALID_ARGUMENT);
				newCaret.setFocus();
			}
		}
	}

	public void setFont(Font font) {
		checkWidget();
		if (caret != null)
			caret.setFont(font);
		super.setFont(font);
	}

	/**
	 * Sets the receiver's IME.
	 * 
	 * @param ime
	 *            the new IME for the receiver, may be null
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the IME has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.4
	 */
	public void setIME(IME ime) {
		checkWidget();
		if (ime != null && ime.isDisposed())
			error(SWT.ERROR_INVALID_ARGUMENT);
		this.ime = ime;
	}

	void updateCaret() {
	}

}
