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
package org.eclipse.draw2d;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleControlListener;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.GWTDrawable;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * The LightweightSystem is the link between SWT and Draw2d. It is the component
 * that provides the ability for {@link Figure Figures} to be hosted on an SWT
 * Canvas.
 * <p>
 * Normal procedure for using a LightweightSystem:
 * <ol>
 * <li>Create an SWT Canvas.
 * <li>Create a LightweightSystem passing it that Canvas.
 * <li>Create a Draw2d Figure and call setContents(IFigure). This Figure will be
 * the top-level Figure of the Draw2d application.
 * </ol>
 */
public class LightweightSystem {

	private Canvas canvas;
	IFigure contents;
	private IFigure root;
	private EventDispatcher dispatcher;
	private UpdateManager manager = new DeferredUpdateManager();
	private int ignoreResize;

	/**
	 * Constructs a LightweightSystem on Canvas <i>c</i>.
	 * 
	 * @param c
	 *            the canvas
	 * @since 2.0
	 */
	public LightweightSystem(Canvas c) {
		this();
		setControl(c);
	}

	/**
	 * Constructs a LightweightSystem <b>without</b> a Canvas.
	 */
	public LightweightSystem() {
		init();
	}

	/**
	 * Adds SWT listeners to the LightWeightSystem's Canvas. This allows for SWT
	 * events to be dispatched and handled by its {@link EventDispatcher}.
	 * <P>
	 * <EM>WARNING:</EM> This method should not be overridden.
	 * 
	 * @since 2.0
	 */
	protected void addListeners() {
		EventHandler handler = createEventHandler();
		canvas.getAccessible().addAccessibleListener(handler);
		canvas.getAccessible().addAccessibleControlListener(handler);
		canvas.addMouseListener(handler);
		canvas.addMouseMoveListener(handler);
		canvas.addMouseTrackListener(handler);
		canvas.addKeyListener(handler);
		canvas.addTraverseListener(handler);
		canvas.addFocusListener(handler);
		canvas.addDisposeListener(handler);
		canvas.addListener(SWT.MouseWheel, handler);

		canvas.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				LightweightSystem.this.controlResized();
			}
		});
		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				LightweightSystem.this.paint(e.gc);
			}
		});
	}

	/**
	 * Resizes and revalidates the root figure when the control is resized.
	 */
	protected void controlResized() {
		if (ignoreResize > 0)
			return;
		Rectangle r = new Rectangle(canvas.getClientArea());
		r.setLocation(0, 0);
		root.setBounds(r);
		root.revalidate();
		getUpdateManager().performUpdate();
	}

	/**
	 * Returns this LightwightSystem's EventDispatcher.
	 * 
	 * @return the event dispatcher
	 * @since 2.0
	 */
	protected EventDispatcher getEventDispatcher() {
		if (dispatcher == null)
			setEventDispatcher(new SWTEventDispatcher());
		return dispatcher;
	}

	/**
	 * Returns this LightweightSystem's root figure.
	 * 
	 * @return the root figure
	 * @since 2.0
	 */
	public IFigure getRootFigure() {
		return root;
	}

	/**
	 * Returns a new instance of this LightweightSystem's EventHandler.
	 * 
	 * @return the newly created event handler
	 * @since 2.0
	 */
	protected final EventHandler createEventHandler() {
		return internalCreateEventHandler();
	}

	/**
	 * Creates and returns the root figure.
	 * 
	 * @return the newly created root figure
	 */
	protected RootFigure createRootFigure() {
		RootFigure f = new RootFigure();
		f.addNotify();
		f.setOpaque(true);
		f.setLayoutManager(new StackLayout());
		return f;
	}

	/**
	 * Returns this LightweightSystem's UpdateManager.
	 * 
	 * @return the update manager
	 * @since 2.0
	 */
	public UpdateManager getUpdateManager() {
		return manager;
	}

	/**
	 * Initializes this LightweightSystem by setting the root figure.
	 */
	protected void init() {
		setRootPaneFigure(createRootFigure());
	}

	EventHandler internalCreateEventHandler() {
		return new EventHandler();
	}

	/**
	 * Invokes this LightweightSystem's {@link UpdateManager} to paint this
	 * LightweightSystem's Canvas and contents.
	 * 
	 * @param gc
	 *            the GC used for painting
	 * @since 2.0
	 */
	public void paint(GC gc) {
		getUpdateManager().paint(gc);
	}

	/**
	 * Sets the contents of the LightweightSystem to the passed figure. This
	 * figure should be the top-level Figure in a Draw2d application.
	 * 
	 * @param figure
	 *            the new root figure
	 * @since 2.0
	 */
	public void setContents(IFigure figure) {
		if (contents != null)
			root.remove(contents);
		contents = figure;
		root.add(contents);
	}

	/**
	 * Sets the LightweightSystem's control to the passed Canvas.
	 * 
	 * @param c
	 *            the canvas
	 * @since 2.0
	 */
	public void setControl(final Canvas c) {
		if (canvas == c)
			return;
		canvas = c;

		if (c.getNativeWidget() != null) {
			getUpdateManager().setGraphicsSource(new GraphicsSource() {

				Graphics gg;
				private GC gc;

				@Override
				public Graphics getGraphics(Rectangle region) {
					if (gg == null) {
						gc = new GC(new GWTDrawable() {

							@Override
							public long internal_new_GC(GCData data) {
								return 0;
							}

							@Override
							public void internal_dispose_GC(long handle,
									GCData data) {
							}

							@Override
							public Context2d internal_getContext() {
								return ((com.google.gwt.canvas.client.Canvas) c
										.getNativeWidget()).getContext2d();
							}
						});
						gg = new SWTGraphics(gc);
						// gc.setClipping(region.x, region.y, region.width,
						// region.height);
					}
					gg.setBackgroundColor(ColorConstants.white);
					return gg;
				}

				@Override
				public void flushGraphics(Rectangle region) {
				}
			});
		} else {
			if ((c.getStyle() & SWT.DOUBLE_BUFFERED) != 0)
				getUpdateManager().setGraphicsSource(
						new NativeGraphicsSource(canvas));
			else
				getUpdateManager().setGraphicsSource(
						new BufferedGraphicsSource(canvas));
		}
		getEventDispatcher().setControl(c);
		addListeners();

		// Size the root figure and contents to the current control's size
		Rectangle r = new Rectangle(canvas.getClientArea());
		r.setLocation(0, 0);
		root.setBounds(r);
		root.revalidate();
	}

	/**
	 * Sets this LightweightSystem's EventDispatcher.
	 * 
	 * @param dispatcher
	 *            the new event dispatcher
	 * @since 2.0
	 */
	public void setEventDispatcher(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		dispatcher.setRoot(root);
		dispatcher.setControl(canvas);
	}

	void setIgnoreResize(boolean value) {
		if (value)
			ignoreResize++;
		else
			ignoreResize--;
	}

	/**
	 * Sets this LightweightSystem's root figure.
	 * 
	 * @param root
	 *            the new root figure
	 */
	protected void setRootPaneFigure(RootFigure root) {
		getUpdateManager().setRoot(root);
		this.root = root;
	}

	/**
	 * Sets this LightweightSystem's UpdateManager.
	 * 
	 * @param um
	 *            the new update manager
	 * @since 2.0
	 */
	public void setUpdateManager(UpdateManager um) {
		manager = um;
		manager.setRoot(root);
	}

	/**
	 * The figure at the root of the LightweightSystem. If certain properties
	 * (i.e. font, background/foreground color) are not set, the RootFigure will
	 * obtain these properties from LightweightSystem's Canvas.
	 */
	protected class RootFigure extends Figure {
		/** @see IFigure#getBackgroundColor() */
		public Color getBackgroundColor() {
			if (bgColor != null)
				return bgColor;
			if (canvas != null)
				return canvas.getBackground();
			return null;

		}

		/** @see IFigure#getFont() */
		public Font getFont() {
			if (font != null)
				return font;
			if (canvas != null)
				return canvas.getFont();
			return null;
		}

		/** @see IFigure#getForegroundColor() */
		public Color getForegroundColor() {
			if (fgColor != null)
				return fgColor;
			if (canvas != null)
				return canvas.getForeground();
			return null;
		}

		/** @see IFigure#getUpdateManager() */
		public UpdateManager getUpdateManager() {
			return LightweightSystem.this.getUpdateManager();
		}

		/** @see IFigure#internalGetEventDispatcher() */
		public EventDispatcher internalGetEventDispatcher() {
			return getEventDispatcher();
		}

		/**
		 * @see IFigure#isMirrored()
		 */
		public boolean isMirrored() {
			return (LightweightSystem.this.canvas.getStyle() & SWT.MIRRORED) != 0;
		}

		/** @see Figure#isShowing() */
		public boolean isShowing() {
			return true;
		}
	}

	/**
	 * Listener used to get all necessary events from the Canvas and pass them
	 * on to the {@link EventDispatcher}.
	 */
	protected class EventHandler implements MouseMoveListener, MouseListener,
			AccessibleControlListener, KeyListener, TraverseListener,
			FocusListener, AccessibleListener, MouseTrackListener, Listener,
			DisposeListener {
		/** @see FocusListener#focusGained(FocusEvent) */
		public void focusGained(FocusEvent e) {
			getEventDispatcher().dispatchFocusGained(e);
		}

		/** @see FocusListener#focusLost(FocusEvent) */
		public void focusLost(FocusEvent e) {
			getEventDispatcher().dispatchFocusLost(e);
		}

		/** @see AccessibleControlListener#getChild(AccessibleControlEvent) */
		public void getChild(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getChild(e);
		}

		/** @see AccessibleControlListener#getChildAtPoint(AccessibleControlEvent) */
		public void getChildAtPoint(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getChildAtPoint(e);
		}

		/** @see AccessibleControlListener#getChildCount(AccessibleControlEvent) */
		public void getChildCount(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getChildCount(e);
		}

		/** @see AccessibleControlListener#getChildren(AccessibleControlEvent) */
		public void getChildren(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getChildren(e);
		}

		/** @see AccessibleControlListener#getDefaultAction(AccessibleControlEvent) */
		public void getDefaultAction(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getDefaultAction(e);
		}

		/** @see AccessibleListener#getDescription(AccessibleEvent) */
		public void getDescription(AccessibleEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getDescription(e);
		}

		/** @see AccessibleControlListener#getFocus(AccessibleControlEvent) */
		public void getFocus(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getFocus(e);
		}

		/** @see AccessibleListener#getHelp(AccessibleEvent) */
		public void getHelp(AccessibleEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getHelp(e);
		}

		/** @see AccessibleListener#getKeyboardShortcut(AccessibleEvent) */
		public void getKeyboardShortcut(AccessibleEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getKeyboardShortcut(e);
		}

		/** @see AccessibleControlListener#getLocation(AccessibleControlEvent) */
		public void getLocation(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getLocation(e);
		}

		/** @see AccessibleListener#getName(AccessibleEvent) */
		public void getName(AccessibleEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getName(e);
		}

		/** @see AccessibleControlListener#getRole(AccessibleControlEvent) */
		public void getRole(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getRole(e);
		}

		/** @see AccessibleControlListener#getSelection(AccessibleControlEvent) */
		public void getSelection(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getSelection(e);
		}

		/** @see AccessibleControlListener#getState(AccessibleControlEvent) */
		public void getState(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getState(e);
		}

		/** @see AccessibleControlListener#getValue(AccessibleControlEvent) */
		public void getValue(AccessibleControlEvent e) {
			EventDispatcher.AccessibilityDispatcher ad;
			ad = getEventDispatcher().getAccessibilityDispatcher();
			if (ad != null)
				ad.getValue(e);
		}

		/**
		 * @see Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 * @since 3.1
		 */
		public void handleEvent(Event event) {
			// Mouse wheel events
			if (event.type == SWT.MouseWheel)
				getEventDispatcher().dispatchMouseWheelScrolled(event);
		}

		/** @see KeyListener#keyPressed(KeyEvent) */
		public void keyPressed(KeyEvent e) {
			getEventDispatcher().dispatchKeyPressed(e);
		}

		/** @see KeyListener#keyReleased(KeyEvent) */
		public void keyReleased(KeyEvent e) {
			getEventDispatcher().dispatchKeyReleased(e);
		}

		/** @see TraverseListener#keyTraversed(TraverseEvent) */
		public void keyTraversed(TraverseEvent e) {
			/*
			 * Doit is almost always false by default for Canvases with
			 * KeyListeners. Set to true to allow normal behavior. For example,
			 * in Dialogs ESC should close.
			 */
			e.doit = true;
			getEventDispatcher().dispatchKeyTraversed(e);
		}

		/** @see MouseListener#mouseDoubleClick(MouseEvent) */
		public void mouseDoubleClick(MouseEvent e) {
			getEventDispatcher().dispatchMouseDoubleClicked(e);
		}

		/** @see MouseListener#mouseDown(MouseEvent) */
		public void mouseDown(MouseEvent e) {
			getEventDispatcher().dispatchMousePressed(e);
		}

		/** @see MouseTrackListener#mouseEnter(MouseEvent) */
		public void mouseEnter(MouseEvent e) {
			getEventDispatcher().dispatchMouseEntered(e);
		}

		/** @see MouseTrackListener#mouseExit(MouseEvent) */
		public void mouseExit(MouseEvent e) {
			getEventDispatcher().dispatchMouseExited(e);
		}

		/** @see MouseTrackListener#mouseHover(MouseEvent) */
		public void mouseHover(MouseEvent e) {
			getEventDispatcher().dispatchMouseHover(e);
		}

		/** @see MouseMoveListener#mouseMove(MouseEvent) */
		public void mouseMove(MouseEvent e) {
			getEventDispatcher().dispatchMouseMoved(e);
		}

		/** @see MouseListener#mouseUp(MouseEvent) */
		public void mouseUp(MouseEvent e) {
			getEventDispatcher().dispatchMouseReleased(e);
		}

		/** @see DisposeListener#widgetDisposed(DisposeEvent) */
		public void widgetDisposed(DisposeEvent e) {
			getUpdateManager().dispose();
		}
	}

}
