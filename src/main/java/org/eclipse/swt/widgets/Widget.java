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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SWTEventListener;
import org.eclipse.swt.internal.gwt.GdkColor;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * This class is the abstract superclass of all user interface objects. Widgets
 * are created, disposed and issue notification to listeners when events occur
 * which affect them.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Dispose</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em> within the
 * SWT implementation. However, it has not been marked final to allow those
 * outside of the SWT development team to implement patched versions of the
 * class in order to get around specific limitations in advance of when those
 * limitations can be addressed by the team. Any class built using subclassing
 * to access the internals of this class will likely fail to compile or run
 * between releases and may be strongly platform specific. Subclassing should
 * not be attempted without an intimate and detailed understanding of the
 * workings of the hierarchy. No support is provided for user-written classes
 * which are implemented as subclasses of this class.
 * </p>
 * 
 * @see #checkSubclass
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 */
public abstract class Widget {
	/**
	 * the handle to the OS resource (Warning: This field is platform dependent)
	 * <p>
	 * <b>IMPORTANT:</b> This field is <em>not</em> part of the SWT public API.
	 * It is marked public only so that it can be shared within the packages
	 * provided by SWT. It is not available on all platforms and should never be
	 * accessed from application code.
	 * </p>
	 * 
	 * @noreference This field is not intended to be referenced by clients.
	 */
	protected boolean isMouseDown = false;
	protected boolean isMoved = false;
	protected long touchStart = 0l;
	private List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
	private List<MouseMoveListener> mouseMoveListeners = new ArrayList<MouseMoveListener>();
	private List<KeyListener> keyListeners = new ArrayList<KeyListener>();

	int style, state;
	Display display;
	Object data;
	protected EventTable eventTable = new EventTable();
	protected com.google.gwt.user.client.ui.Widget gwtWidget;

	/* Global state flags */
	static final int DISPOSED = 1 << 0;
	static final int CANVAS = 1 << 1;
	static final int KEYED_DATA = 1 << 2;
	static final int HANDLE = 1 << 3;
	static final int DISABLED = 1 << 4;
	static final int MENU = 1 << 5;
	static final int OBSCURED = 1 << 6;
	static final int MOVED = 1 << 7;
	static final int RESIZED = 1 << 8;
	static final int ZERO_WIDTH = 1 << 9;
	static final int ZERO_HEIGHT = 1 << 10;
	static final int HIDDEN = 1 << 11;
	static final int FOREGROUND = 1 << 12;
	static final int BACKGROUND = 1 << 13;
	static final int FONT = 1 << 14;
	static final int PARENT_BACKGROUND = 1 << 15;
	static final int THEME_BACKGROUND = 1 << 16;

	/* A layout was requested on this widget */
	static final int LAYOUT_NEEDED = 1 << 17;

	/* The preferred size of a child has changed */
	static final int LAYOUT_CHANGED = 1 << 18;

	/* A layout was requested in this widget hierachy */
	static final int LAYOUT_CHILD = 1 << 19;

	/* More global state flags */
	static final int RELEASED = 1 << 20;
	static final int DISPOSE_SENT = 1 << 21;
	static final int FOREIGN_HANDLE = 1 << 22;
	static final int DRAG_DETECT = 1 << 23;

	/* Notify of the opportunity to skin this widget */
	static final int SKIN_NEEDED = 1 << 24;

	/* Should sub-windows be checked when EnterNotify received */
	static final int CHECK_SUBWINDOW = 1 << 25;

	/* Default size for widgets */
	static final int DEFAULT_WIDTH = 64;
	static final int DEFAULT_HEIGHT = 64;

	/* GTK signals data */
	static final int ACTIVATE = 1;
	static final int BUTTON_PRESS_EVENT = 2;
	static final int BUTTON_PRESS_EVENT_INVERSE = 3;
	static final int BUTTON_RELEASE_EVENT = 4;
	static final int BUTTON_RELEASE_EVENT_INVERSE = 5;
	static final int CHANGED = 6;
	static final int CHANGE_VALUE = 7;
	static final int CLICKED = 8;
	static final int COMMIT = 9;
	static final int CONFIGURE_EVENT = 10;
	static final int DELETE_EVENT = 11;
	static final int DELETE_RANGE = 12;
	static final int DELETE_TEXT = 13;
	static final int ENTER_NOTIFY_EVENT = 14;
	static final int EVENT = 15;
	static final int EVENT_AFTER = 16;
	static final int EXPAND_COLLAPSE_CURSOR_ROW = 17;
	static final int EXPOSE_EVENT = 18;
	static final int EXPOSE_EVENT_INVERSE = 19;
	static final int FOCUS = 20;
	static final int FOCUS_IN_EVENT = 21;
	static final int FOCUS_OUT_EVENT = 22;
	static final int GRAB_FOCUS = 23;
	static final int HIDE = 24;
	static final int INPUT = 25;
	static final int INSERT_TEXT = 26;
	static final int KEY_PRESS_EVENT = 27;
	static final int KEY_RELEASE_EVENT = 28;
	static final int LEAVE_NOTIFY_EVENT = 29;
	static final int MAP = 30;
	static final int MAP_EVENT = 31;
	static final int MNEMONIC_ACTIVATE = 32;
	static final int MOTION_NOTIFY_EVENT = 33;
	static final int MOTION_NOTIFY_EVENT_INVERSE = 34;
	static final int MOVE_FOCUS = 35;
	static final int OUTPUT = 36;
	static final int POPULATE_POPUP = 37;
	static final int POPUP_MENU = 38;
	static final int PREEDIT_CHANGED = 39;
	static final int REALIZE = 40;
	static final int ROW_ACTIVATED = 41;
	static final int SCROLL_CHILD = 42;
	static final int SCROLL_EVENT = 43;
	static final int SELECT = 44;
	static final int SHOW = 45;
	static final int SHOW_HELP = 46;
	static final int SIZE_ALLOCATE = 47;
	static final int STYLE_SET = 48;
	static final int SWITCH_PAGE = 49;
	static final int TEST_COLLAPSE_ROW = 50;
	static final int TEST_EXPAND_ROW = 51;
	static final int TEXT_BUFFER_INSERT_TEXT = 52;
	static final int TOGGLED = 53;
	static final int UNMAP = 54;
	static final int UNMAP_EVENT = 55;
	static final int UNREALIZE = 56;
	static final int VALUE_CHANGED = 57;
	static final int VISIBILITY_NOTIFY_EVENT = 58;
	static final int WINDOW_STATE_EVENT = 59;
	static final int ACTIVATE_INVERSE = 60;
	static final int DAY_SELECTED = 61;
	static final int MONTH_CHANGED = 62;
	static final int STATUS_ICON_POPUP_MENU = 63;
	static final int ROW_INSERTED = 64;
	static final int ROW_DELETED = 65;
	static final int DAY_SELECTED_DOUBLE_CLICK = 66;
	static final int ICON_RELEASE = 67;
	static final int SELECTION_DONE = 68;
	static final int START_INTERACTIVE_SEARCH = 69;
	static final int LAST_SIGNAL = 70;

	static final String IS_ACTIVE = "org.eclipse.swt.internal.control.isactive"; //$NON-NLS-1$
	static final String KEY_CHECK_SUBWINDOW = "org.eclipse.swt.internal.control.checksubwindow"; //$NON-NLS-1$

	/**
	 * Prevents uninitialized instances from being created outside the package.
	 */
	Widget() {
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
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parent is disposed</li>
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
	 * @see #checkSubclass
	 * @see #getStyle
	 */
	public Widget(Widget parent, int style) {
		this(null, parent, style);
	}

	Widget(com.google.gwt.user.client.ui.Widget gwtWidget, Widget parent, int style) {
		this.gwtWidget = gwtWidget;
		checkSubclass();
		this.style = style;
		if (parent != null) {
			display = parent.display;
		} else {
			display = Display.getDefault();
		}
		reskinWidget();
		addListeners();
	}

	public com.google.gwt.user.client.ui.Widget getGwtWidget() {
		return gwtWidget;
	}

	public void setGwtWidget(com.google.gwt.user.client.ui.Widget gwtWidget) {
		this.gwtWidget = gwtWidget;
	}

	public Object getNativeWidget() {
		return gwtWidget;
	}

	ScrollPanel scrollPanel;

	public void setScrollPanel(ScrollPanel sp) {
		scrollPanel = sp;
	}

	public ScrollPanel getScrollPanel() {
		return scrollPanel;
	}

	private LayoutPanel simplePanel;

	public LayoutPanel getSimplePanel() {
		return simplePanel;
	}

	public void setSimplePanel(LayoutPanel simplePanel) {
		this.simplePanel = simplePanel;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when an event of the given type occurs. When the event does occur in the
	 * widget, the listener is notified by sending it the
	 * <code>handleEvent()</code> message. The event type is one of the event
	 * constants defined in class <code>SWT</code>.
	 * 
	 * @param eventType
	 *            the type of event to listen for
	 * @param listener
	 *            the listener which should be notified when the event occurs
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
	 * @see Listener
	 * @see SWT
	 * @see #getListeners(int)
	 * @see #removeListener(int, Listener)
	 * @see #notifyListeners
	 */

	void _addListener(int eventType, Listener listener) {
		if (eventType == SWT.KeyDown) {
			if (listener instanceof TypedListener) {
				SWTEventListener eventListener = ((TypedListener) listener)
						.getEventListener();
				addKeyListener((KeyListener) eventListener, 0);
			}
		}
		if (eventTable == null)
			eventTable = new EventTable();
		eventTable.hook(eventType, listener);
	}

	public void addListener(int i, Listener listener) {
		_addListener(i, listener);
	}

	protected int touchStartX;
	protected int touchStartY;

	protected void addListeners() {
		final Event e = new Event();
		e.widget = this;

		if (gwtWidget instanceof HasKeyUpHandlers) {
			((HasKeyUpHandlers) gwtWidget).addKeyUpHandler(new KeyUpHandler() {

				@Override
				public void onKeyUp(KeyUpEvent event) {
					Event e = new Event();
					e.widget = Widget.this;
					int nativeKeyCode = event.getNativeKeyCode();
					switch (nativeKeyCode) {
					case KeyCodes.KEY_DELETE:
						e.keyCode = SWT.DEL;
						break;
					case KeyCodes.KEY_CTRL:
						e.keyCode = SWT.CTRL;
						break;
					default:
						e.keyCode = nativeKeyCode;
						break;
					}
					sendEvent(SWT.KeyUp, e);
				}
			});
		}

		if (gwtWidget instanceof HasMouseOverHandlers) {
			((HasMouseOverHandlers) gwtWidget)
					.addMouseOverHandler(new MouseOverHandler() {

						@Override
						public void onMouseOver(MouseOverEvent event) {
							// workaround for Drag mouse icon disappearing in
							// Chrome
							// turnTextSelectionOff();

							MouseEvent me = new MouseEvent(e);
							me.x = event.getX();
							me.y = event.getY();
							me.button = event.getNativeButton();
							addStateMasks(event, me);
							dispatchMouseEntered(me);
						}
					});
		}

		if (gwtWidget instanceof HasMouseOutHandlers) {
			((HasMouseOutHandlers) gwtWidget)
					.addMouseOutHandler(new MouseOutHandler() {

						@Override
						public void onMouseOut(MouseOutEvent event) {
							// turnTextSelectionOn();
							MouseEvent me = new MouseEvent(e);
							me.x = event.getX();
							me.y = event.getY();
							me.button = event.getNativeButton();
							addStateMasks(event, me);
							dispatchMouseExited(me);
						}
					});
		}

		if (gwtWidget instanceof HasMouseMoveHandlers) {
			((HasMouseMoveHandlers) gwtWidget)
					.addMouseMoveHandler(new MouseMoveHandler() {

						@Override
						public void onMouseMove(MouseMoveEvent event) {
							MouseEvent me = new MouseEvent(e);
							me.x = event.getX();
							me.y = event.getY();
							me.button = event.getNativeButton();
							if (isMouseDown) {
								if (me.button == NativeEvent.BUTTON_LEFT) {
									me.stateMask = SWT.BUTTON1;
								} else if (me.button == NativeEvent.BUTTON_RIGHT) {
									me.stateMask = SWT.BUTTON2;
								}
							}
							addStateMasks(event, me);
							dispatchMouseMoved(me);
						}
					});
		}

		if (gwtWidget instanceof HasMouseUpHandlers) {
			((HasMouseUpHandlers) gwtWidget)
					.addMouseUpHandler(new MouseUpHandler() {

						@Override
						public void onMouseUp(MouseUpEvent event) {
							isMouseDown = false;
							MouseEvent me = new MouseEvent(e);
							me.x = event.getX();
							me.y = event.getY();
							me.button = event.getNativeButton();
							addStateMasks(event, me);
							dispatchMouseReleased(me);
						}
					});
		}

		if (gwtWidget instanceof HasMouseDownHandlers) {
			((HasMouseDownHandlers) gwtWidget)
					.addMouseDownHandler(new MouseDownHandler() {
						@Override
						public void onMouseDown(MouseDownEvent event) {
							// event.stopPropagation();
							// event.preventDefault();
							isMouseDown = true;
							MouseEvent me = new MouseEvent(e);
							me.x = event.getX();
							me.y = event.getY();
							addStateMasks(event, me);
							if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
								me.button = 2;
								dispatchMousePressed(me);
							}
							me.button = event.getNativeButton();
							dispatchMousePressed(me);
						}
					});
		}

		if (gwtWidget instanceof HasKeyDownHandlers) {
			((HasKeyDownHandlers) gwtWidget)
					.addKeyDownHandler(new KeyDownHandler() {

						@Override
						public void onKeyDown(KeyDownEvent event) {
							Event e = new Event();
							e.widget = Widget.this;
							int nativeKeyCode = event.getNativeKeyCode();
							switch (nativeKeyCode) {
							case KeyCodes.KEY_DELETE:
								e.keyCode = SWT.DEL;
								e.character = SWT.DEL;
								break;
							case KeyCodes.KEY_RIGHT:
								e.keyCode = SWT.ARROW_RIGHT;
								break;
							case KeyCodes.KEY_LEFT:
								e.keyCode = SWT.ARROW_LEFT;
								break;
							case KeyCodes.KEY_UP:
								e.keyCode = SWT.ARROW_UP;
								break;
							case KeyCodes.KEY_DOWN:
								e.keyCode = SWT.ARROW_DOWN;
								break;
							case KeyCodes.KEY_ALT:
								e.keyCode = SWT.ALT;
								break;
							case KeyCodes.KEY_CTRL:
								e.keyCode = SWT.CTRL;
								break;
							case KeyCodes.KEY_SHIFT:
								e.keyCode = SWT.SHIFT;
								break;
							case KeyCodes.KEY_ESCAPE:
								e.keyCode = SWT.ESC;
								e.character = SWT.ESC;
								break;
							case KeyCodes.KEY_ENTER:
								e.keyCode = SWT.TRAVERSE_RETURN;
								break;
							case 90:
								e.character = 'z';
								break;
							case 89:
								e.character = 'y';
								break;
							case 67:
								e.character = 'c';
								break;
							case 190:
								e.character = '.';
								break;

							default:
								// System.out.println("nativeKeyCode = "
								// + nativeKeyCode);
								e.keyCode = nativeKeyCode;
								break;
							}
							sendEvent(SWT.KeyDown, e);
							// dispatchKeyPressed(ke);
						}
					});
		}
		if (gwtWidget instanceof HasFocusHandlers) {
			((HasFocusHandlers) gwtWidget).addFocusHandler(new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					Event fe = new Event();
					fe.widget = Widget.this;
					sendEvent(SWT.FocusIn, fe);
				}
			});
		}

	}

	protected void dispatchKeyPressed(KeyEvent ke) {
		List<KeyListener> copy = new ArrayList<KeyListener>(keyListeners);
		for (KeyListener keyListener : copy) {
			keyListener.keyPressed(ke);
		}
	}

	protected void dispatchMouseReleased(MouseEvent me) {
		List<MouseListener> copy = new ArrayList<MouseListener>(mouseListeners);
		for (MouseListener ml : copy) {
			ml.mouseUp(me);
		}

	}

	protected void dispatchMousePressed(MouseEvent me) {
		List<MouseListener> copy = new ArrayList<MouseListener>(mouseListeners);
		for (MouseListener ml : copy) {
			ml.mouseDown(me);
		}
	}

	protected void dispatchMouseMoved(MouseEvent me) {
		List<MouseMoveListener> copy = new ArrayList<MouseMoveListener>(
				mouseMoveListeners);
		for (MouseMoveListener ml : copy) {
			ml.mouseMove(me);
		}

	}

	protected void dispatchMouseExited(MouseEvent me) {

	}

	protected void dispatchMouseEntered(MouseEvent me) {

	}

	public void addKeyListener(KeyListener listener, int i) {
		this.keyListeners.add(listener);
	}

	public void addMouseMoveListener(MouseMoveListener listener) {
		this.mouseMoveListeners.add(listener);
	}

	public void addMouseListener(org.eclipse.swt.events.MouseListener handler) {
		this.mouseListeners.add(handler);
	}

	private static void addStateMasks(HumanInputEvent event, MouseEvent me) {
		if (event.isShiftKeyDown()) {
			me.stateMask |= SWT.SHIFT;
		}
		if (event.isControlKeyDown()) {
			me.stateMask |= SWT.CONTROL;
		}
		if (event.isAltKeyDown()) {
			me.stateMask |= SWT.ALT;
		}
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the widget is disposed. When the widget is disposed, the listener is
	 * notified by sending it the <code>widgetDisposed()</code> message.
	 * 
	 * @param listener
	 *            the listener which should be notified when the receiver is
	 *            disposed
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
	 * @see DisposeListener
	 * @see #removeDisposeListener
	 */
	public void addDisposeListener(DisposeListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		// addListener(SWT.Dispose, typedListener);
	}

	long /* int */paintWindow() {
		return 0;
	}

	static int checkBits(int style, int int0, int int1, int int2, int int3,
			int int4, int int5) {
		int mask = int0 | int1 | int2 | int3 | int4 | int5;
		if ((style & mask) == 0)
			style |= int0;
		if ((style & int0) != 0)
			style = (style & ~mask) | int0;
		if ((style & int1) != 0)
			style = (style & ~mask) | int1;
		if ((style & int2) != 0)
			style = (style & ~mask) | int2;
		if ((style & int3) != 0)
			style = (style & ~mask) | int3;
		if ((style & int4) != 0)
			style = (style & ~mask) | int4;
		if ((style & int5) != 0)
			style = (style & ~mask) | int5;
		return style;
	}

	void checkOpen() {
		/* Do nothing */
	}

	void checkOrientation(Widget parent) {
	}

	/**
	 * Checks that this class can be subclassed.
	 * <p>
	 * The SWT class library is intended to be subclassed only at specific,
	 * controlled points (most notably, <code>Composite</code> and
	 * <code>Canvas</code> when implementing new widgets). This method enforces
	 * this rule unless it is overridden.
	 * </p>
	 * <p>
	 * <em>IMPORTANT:</em> By providing an implementation of this method that
	 * allows a subclass of a class which does not normally allow subclassing to
	 * be created, the implementer agrees to be fully responsible for the fact
	 * that any such subclass will likely fail between SWT releases and will be
	 * strongly platform specific. No support is provided for user-written
	 * classes which are implemented in this fashion.
	 * </p>
	 * <p>
	 * The ability to subclass outside of the allowed SWT classes is intended
	 * purely to enable those not on the SWT development team to implement
	 * patches in order to get around specific limitations in advance of when
	 * those limitations can be addressed by the team. Subclassing should not be
	 * attempted without an intimate and detailed understanding of the
	 * hierarchy.
	 * </p>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 */
	protected void checkSubclass() {
		if (!isValidSubclass())
			error(SWT.ERROR_INVALID_SUBCLASS);
	}

	/**
	 * Throws an <code>SWTException</code> if the receiver can not be accessed
	 * by the caller. This may include both checks on the state of the receiver
	 * and more generally on the entire execution context. This method
	 * <em>should</em> be called by widget implementors to enforce the standard
	 * SWT invariants.
	 * <p>
	 * Currently, it is an error to invoke any method (other than
	 * <code>isDisposed()</code>) on a widget that has had its
	 * <code>dispose()</code> method called. It is also an error to call widget
	 * methods from any thread that is different from the thread that created
	 * the widget.
	 * </p>
	 * <p>
	 * In future releases of SWT, there may be more or fewer error checks and
	 * exceptions may be thrown for different reasons.
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
	protected void checkWidget() {
	}

	void createHandle(int index) {
	}

	void createWidget(int index) {
		createHandle(index);
		setOrientation(true);
		hookEvents();
		register();
	}

	void destroyWidget() {
	}

	/**
	 * Disposes of the operating system resources associated with the receiver
	 * and all its descendants. After this method has been invoked, the receiver
	 * and all descendants will answer <code>true</code> when sent the message
	 * <code>isDisposed()</code>. Any internal connections between the widgets
	 * in the tree will have been removed to facilitate garbage collection. This
	 * method does nothing if the widget is already disposed.
	 * <p>
	 * NOTE: This method is not called recursively on the descendants of the
	 * receiver. This means that, widget implementers can not detect when a
	 * widget is being disposed of by re-implementing this method, but should
	 * instead listen for the <code>Dispose</code> event.
	 * </p>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #addDisposeListener
	 * @see #removeDisposeListener
	 * @see #checkWidget
	 */
	public void dispose() {
		/*
		 * Note: It is valid to attempt to dispose a widget more than once. If
		 * this happens, fail silently.
		 */
		if (isDisposed())
			return;
		release(true);
	}

	void error(int code) {
		SWT.error(code);
	}

	/**
	 * Returns the application defined widget data associated with the receiver,
	 * or null if it has not been set. The <em>widget data</em> is a single,
	 * unnamed field that is stored with every widget.
	 * <p>
	 * Applications may put arbitrary objects in this field. If the object
	 * stored in the widget data needs to be notified when the widget is
	 * disposed of, it is the application's responsibility to hook the Dispose
	 * event on the widget and do so.
	 * </p>
	 * 
	 * @return the widget data
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - when the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - when called from the
	 *                wrong thread</li>
	 *                </ul>
	 * 
	 * @see #setData(Object)
	 */
	public Object getData() {
		checkWidget();
		return (state & KEYED_DATA) != 0 ? ((Object[]) data)[0] : data;
	}

	/**
	 * Returns the application defined property of the receiver with the
	 * specified name, or null if it has not been set.
	 * <p>
	 * Applications may have associated arbitrary objects with the receiver in
	 * this fashion. If the objects stored in the properties need to be notified
	 * when the widget is disposed of, it is the application's responsibility to
	 * hook the Dispose event on the widget and do so.
	 * </p>
	 * 
	 * @param key
	 *            the name of the property
	 * @return the value of the property or null if it has not been set
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the key is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #setData(String, Object)
	 */
	public Object getData(String key) {
		checkWidget();
		if (key == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (key.equals(KEY_CHECK_SUBWINDOW)) {
			return new Boolean((state & CHECK_SUBWINDOW) != 0);
		}
		if (key.equals(IS_ACTIVE))
			return new Boolean(isActive());
		if ((state & KEYED_DATA) != 0) {
			Object[] table = (Object[]) data;
			for (int i = 1; i < table.length; i += 2) {
				if (key.equals(table[i]))
					return table[i + 1];
			}
		}
		return null;
	}

	/**
	 * Returns the <code>Display</code> that is associated with the receiver.
	 * <p>
	 * A widget's display is either provided when it is created (for example,
	 * top level <code>Shell</code>s) or is the same as its parent's display.
	 * </p>
	 * 
	 * @return the receiver's display
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Display getDisplay() {
		Display display = this.display;
		if (display == null)
			error(SWT.ERROR_WIDGET_DISPOSED);
		return display;
	}

	/**
	 * Returns an array of listeners who will be notified when an event of the
	 * given type occurs. The event type is one of the event constants defined
	 * in class <code>SWT</code>.
	 * 
	 * @param eventType
	 *            the type of event to listen for
	 * @return an array of listeners that will be notified when the event occurs
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Listener
	 * @see SWT
	 * @see #addListener(int, Listener)
	 * @see #removeListener(int, Listener)
	 * @see #notifyListeners
	 * 
	 * @since 3.4
	 */
	public Listener[] getListeners(int eventType) {
		checkWidget();
		return null;
	}

	String getName() {
		String string = getClass().getName();
		int index = string.length();
		while ((--index > 0) && (string.charAt(index) != '.')) {
		}
		return string.substring(index + 1, string.length());
	}

	String getNameText() {
		return "";
	}

	/**
	 * Returns the receiver's style information.
	 * <p>
	 * Note that the value which is returned by this method <em>may
	 * not match</em> the value which was provided to the constructor when the
	 * receiver was created. This can occur when the underlying operating system
	 * does not support a particular combination of requested styles. For
	 * example, if the platform widget used to implement a particular SWT widget
	 * always has scroll bars, the result of calling this method would always
	 * have the <code>SWT.H_SCROLL</code> and <code>SWT.V_SCROLL</code> bits
	 * set.
	 * </p>
	 * 
	 * @return the style bits
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getStyle() {
		checkWidget();
		return style;
	}

	boolean filters(int eventType) {
		return display.filters(eventType);
	}

	boolean isActive() {
		return true;
	}

	/**
	 * Returns <code>true</code> if the widget has been disposed, and
	 * <code>false</code> otherwise.
	 * <p>
	 * This method gets the dispose state for the widget. When a widget has been
	 * disposed, it is an error to invoke any other method (except
	 * {@link #dispose()}) using the widget.
	 * </p>
	 * 
	 * @return <code>true</code> when the widget is disposed and
	 *         <code>false</code> otherwise
	 */
	public boolean isDisposed() {
		return (state & DISPOSED) != 0;
	}

	/**
	 * Returns <code>true</code> if there are any listeners for the specified
	 * event type associated with the receiver, and <code>false</code>
	 * otherwise. The event type is one of the event constants defined in class
	 * <code>SWT</code>.
	 * 
	 * @param eventType
	 *            the type of event
	 * @return true if the event is hooked
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SWT
	 */
	public boolean isListening(int eventType) {
		// checkWidget();
		// return hooks(eventType);
		return false;
	}

	boolean isValidThread() {
		return getDisplay().isValidThread();
	}

	boolean isValidSubclass() {
		return Display.isValidClass(getClass());
	}

	void hookEvents() {
	}

	/*
	 * Returns <code>true</code> if the specified eventType is hooked, and
	 * <code>false</code> otherwise. Implementations of SWT can avoid creating
	 * objects and sending events when an event happens in the operating system
	 * but there are no listeners hooked for the event.
	 * 
	 * @param eventType the event to be checked
	 * 
	 * @return <code>true</code> when the eventType is hooked and
	 * <code>false</code> otherwise
	 * 
	 * @see #isListening
	 */

	boolean hooks(int eventType) {
		if (eventTable == null)
			return false;
		return eventTable.hooks(eventType);
	}

	boolean mnemonicHit(long /* int */mnemonicHandle, char key) {
		return false;
	}

	boolean mnemonicMatch(long /* int */mnemonicHandle, char key) {
		return false;
	}

	/**
	 * Notifies all of the receiver's listeners for events of the given type
	 * that one such event has occurred by invoking their
	 * <code>handleEvent()</code> method. The event type is one of the event
	 * constants defined in class <code>SWT</code>.
	 * 
	 * @param eventType
	 *            the type of event which has occurred
	 * @param event
	 *            the event data
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SWT
	 * @see #addListener
	 * @see #getListeners(int)
	 * @see #removeListener(int, Listener)
	 */
	public void notifyListeners(int eventType, Event event) {
		checkWidget();
		if (event == null)
			event = new Event();
		sendEvent(eventType, event);
	}

	void postEvent(int eventType, Event event) {
		sendEvent(eventType, event, false);
	}

	void register() {
	}

	void release(boolean destroy) {
		if ((state & DISPOSE_SENT) == 0) {
			state |= DISPOSE_SENT;
			sendEvent(SWT.Dispose);
		}
		if ((state & DISPOSED) == 0) {
			releaseChildren(destroy);
		}
		if ((state & RELEASED) == 0) {
			state |= RELEASED;
			if (destroy) {
				releaseParent();
				releaseWidget();
				destroyWidget();
			} else {
				releaseWidget();
				releaseHandle();
			}
		}
	}

	void releaseChildren(boolean destroy) {
	}

	void releaseHandle() {
	}

	void releaseParent() {
	}

	void releaseWidget() {
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when an event of the given type occurs. The event type is one of
	 * the event constants defined in class <code>SWT</code>.
	 * 
	 * @param eventType
	 *            the type of event to listen for
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
	 * @see Listener
	 * @see SWT
	 * @see #addListener
	 * @see #getListeners(int)
	 * @see #notifyListeners
	 */
	public void removeListener(int eventType, Listener handler) {
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when an event of the given type occurs.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the SWT public API.
	 * It is marked public only so that it can be shared within the packages
	 * provided by SWT. It should never be referenced from application code.
	 * </p>
	 * 
	 * @param eventType
	 *            the type of event to listen for
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
	 * @see Listener
	 * @see #addListener
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected void removeListener(int eventType, SWTEventListener handler) {
	}

	/**
	 * Marks the widget to be skinned.
	 * <p>
	 * The skin event is sent to the receiver's display when appropriate
	 * (usually before the next event is handled). Widgets are automatically
	 * marked for skinning upon creation as well as when its skin id or class
	 * changes. The skin id and/or class can be changed by calling
	 * <code>Display.setData(String, Object)</code> with the keys SWT.SKIN_ID
	 * and/or SWT.SKIN_CLASS. Once the skin event is sent to a widget, it will
	 * not be sent again unless <code>reskin(int)</code> is called on the widget
	 * or on an ancestor while specifying the <code>SWT.ALL</code> flag.
	 * </p>
	 * <p>
	 * The parameter <code>flags</code> may be either:
	 * <dl>
	 * <dt><b>SWT.ALL</b></dt>
	 * <dd>all children in the receiver's widget tree should be skinned</dd>
	 * <dt><b>SWT.NONE</b></dt>
	 * <dd>only the receiver should be skinned</dd>
	 * </dl>
	 * </p>
	 * 
	 * @param flags
	 *            the flags specifying how to reskin
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * @since 3.6
	 */
	public void reskin(int flags) {
		checkWidget();
		reskinWidget();
		if ((flags & SWT.ALL) != 0)
			reskinChildren(flags);
	}

	void reskinChildren(int flags) {
	}

	void reskinWidget() {
		if ((state & SKIN_NEEDED) != SKIN_NEEDED) {
			this.state |= SKIN_NEEDED;
			display.addSkinnableWidget(this);
		}
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the widget is disposed.
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
	 * @see DisposeListener
	 * @see #addDisposeListener
	 */
	public void removeDisposeListener(DisposeListener listener) {
	}

	void sendEvent(Event event) {
		Display display = event.display;
		if (!display.filterEvent(event)) {
			if (eventTable != null)
				eventTable.sendEvent(event);
		}
	}

	void sendEvent(int eventType) {
		sendEvent(eventType, null, true);
	}

	void sendEvent(int eventType, Event event) {
		sendEvent(eventType, event, true);
	}

	void sendEvent(int eventType, Event event, boolean send) {
		if (eventTable == null && !display.filters(eventType)) {
			return;
		}
		if (event == null)
			event = new Event();
		event.type = eventType;
		event.display = display;
		event.widget = this;
		if (event.time == 0) {
			event.time = display.getLastEventTime();
		}
		if (send) {
			sendEvent(event);
		} else {
			display.postEvent(event);
		}
	}

	void sendSelectionEvent(int eventType, Event event, boolean send) {
	}

	/**
	 * Sets the application defined widget data associated with the receiver to
	 * be the argument. The <em>widget
	 * data</em> is a single, unnamed field that is stored with every widget.
	 * <p>
	 * Applications may put arbitrary objects in this field. If the object
	 * stored in the widget data needs to be notified when the widget is
	 * disposed of, it is the application's responsibility to hook the Dispose
	 * event on the widget and do so.
	 * </p>
	 * 
	 * @param data
	 *            the widget data
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - when the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - when called from the
	 *                wrong thread</li>
	 *                </ul>
	 * 
	 * @see #getData()
	 */
	public void setData(Object data) {
		checkWidget();
		if ((state & KEYED_DATA) != 0) {
			((Object[]) this.data)[0] = data;
		} else {
			this.data = data;
		}
	}

	/**
	 * Sets the application defined property of the receiver with the specified
	 * name to the given value.
	 * <p>
	 * Applications may associate arbitrary objects with the receiver in this
	 * fashion. If the objects stored in the properties need to be notified when
	 * the widget is disposed of, it is the application's responsibility to hook
	 * the Dispose event on the widget and do so.
	 * </p>
	 * 
	 * @param key
	 *            the name of the property
	 * @param value
	 *            the new value for the property
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the key is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see #getData(String)
	 */
	public void setData(String key, Object value) {
		checkWidget();
		if (key == null)
			error(SWT.ERROR_NULL_ARGUMENT);

		if (key.equals(KEY_CHECK_SUBWINDOW)) {
			if (value != null && value instanceof Boolean) {
				if (((Boolean) value).booleanValue()) {
					state |= CHECK_SUBWINDOW;
				} else {
					state &= ~CHECK_SUBWINDOW;
				}
			}
			return;
		}

		int index = 1;
		Object[] table = null;
		if ((state & KEYED_DATA) != 0) {
			table = (Object[]) data;
			while (index < table.length) {
				if (key.equals(table[index]))
					break;
				index += 2;
			}
		}
		if (value != null) {
			if ((state & KEYED_DATA) != 0) {
				if (index == table.length) {
					Object[] newTable = new Object[table.length + 2];
					System.arraycopy(table, 0, newTable, 0, table.length);
					data = table = newTable;
				}
			} else {
				table = new Object[3];
				table[0] = data;
				data = table;
				state |= KEYED_DATA;
			}
			table[index] = key;
			table[index + 1] = value;
		} else {
			if ((state & KEYED_DATA) != 0) {
				if (index != table.length) {
					int length = table.length - 2;
					if (length == 1) {
						data = table[0];
						state &= ~KEYED_DATA;
					} else {
						Object[] newTable = new Object[length];
						System.arraycopy(table, 0, newTable, 0, index);
						System.arraycopy(table, index + 2, newTable, index,
								length - index);
						data = newTable;
					}
				}
			}
		}
		if (key.equals(SWT.SKIN_CLASS) || key.equals(SWT.SKIN_ID))
			this.reskin(SWT.ALL);
	}

	void setForegroundColor(long /* int */handle, GdkColor color) {
	}

	boolean setInputState(Event event, int state) {
		return false;
	}

	void setOrientation(boolean create) {
	}

	boolean setTabGroupFocus(boolean next) {
		return setTabItemFocus(next);
	}

	boolean setTabItemFocus(boolean next) {
		return false;
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the receiver
	 */
	public String toString() {
		String string = "*Disposed*";
		if (!isDisposed()) {
			string = "*Wrong Thread*";
			if (isValidThread())
				string = getNameText();
		}
		return getName() + " {" + string + "}";
	}

}
