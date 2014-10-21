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
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.gwt.GdkColor;
import org.eclipse.swt.internal.gwt.GdkRectangle;
import org.eclipse.swt.internal.gwt.XExposeEvent;
import org.eclipse.swt.internal.gwt.XVisibilityEvent;

import com.google.gwt.user.client.Timer;

/**
 * Instances of this class are responsible for managing the connection between
 * SWT and the underlying operating system. Their most important function is to
 * implement the SWT event loop in terms of the platform event model. They also
 * provide various methods for accessing information about the operating system,
 * and have overall control over the operating system resources which SWT
 * allocates.
 * <p>
 * Applications which are built with SWT will <em>almost always</em> require
 * only a single display. In particular, some platforms which SWT supports will
 * not allow more than one <em>active</em> display. In other words, some
 * platforms do not support creating a new display if one already exists that
 * has not been sent the <code>dispose()</code> message.
 * <p>
 * In SWT, the thread which creates a <code>Display</code> instance is
 * distinguished as the <em>user-interface thread</em> for that display.
 * </p>
 * The user-interface thread for a particular display has the following special
 * attributes:
 * <ul>
 * <li>
 * The event loop for that display must be run from the thread.</li>
 * <li>
 * Some SWT API methods (notably, most of the public methods in
 * <code>Widget</code> and its subclasses), may only be called from the thread.
 * (To support multi-threaded user-interface applications, class
 * <code>Display</code> provides inter-thread communication methods which allow
 * threads other than the user-interface thread to request that it perform
 * operations on their behalf.)</li>
 * <li>
 * The thread is not allowed to construct other <code>Display</code>s until that
 * display has been disposed. (Note that, this is in addition to the restriction
 * mentioned above concerning platform support for multiple displays. Thus, the
 * only way to have multiple simultaneously active displays, even on platforms
 * which support it, is to have multiple threads.)</li>
 * </ul>
 * Enforcing these attributes allows SWT to be implemented directly on the
 * underlying operating system's event model. This has numerous benefits
 * including smaller footprint, better use of resources, safer memory
 * management, clearer program logic, better performance, and fewer overall
 * operating system threads required. The down side however, is that care must
 * be taken (only) when constructing multi-threaded applications to use the
 * inter-thread communication mechanisms which this class provides when
 * required. </p>
 * <p>
 * All SWT API methods which may only be called from the user-interface thread
 * are distinguished in their documentation by indicating that they throw the "
 * <code>ERROR_THREAD_INVALID_ACCESS</code>" SWT exception.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Close, Dispose, OpenDocument, Settings, Skin</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see #syncExec
 * @see #asyncExec
 * @see #wake
 * @see #readAndDispatch
 * @see #sleep
 * @see Device#dispose
 * @see <a href="http://www.eclipse.org/swt/snippets/#display">Display
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Display extends Device {

	/* Events Dispatching and Callback */
	int gdkEventCount;
	long /* int */[] gdkEvents;
	Widget[] gdkEventWidgets;
	int[] dispatchEvents;
	Event[] eventQueue;
	long /* int */fds;
	int allocated_nfds;
	boolean wake;
	int[] max_priority = new int[1], timeout = new int[1];
	Callback eventCallback, filterCallback;
	long /* int */eventProc, filterProc, windowProc2, windowProc3, windowProc4,
			windowProc5;
	Callback windowCallback2, windowCallback3, windowCallback4,
			windowCallback5;
	EventTable eventTable, filterTable;
	static String APP_NAME = "SWT"; //$NON-NLS-1$
	static String APP_VERSION = ""; //$NON-NLS-1$
	static final String DISPATCH_EVENT_KEY = "org.eclipse.swt.internal.gtk.dispatchEvent"; //$NON-NLS-1$
	static final String ADD_WIDGET_KEY = "org.eclipse.swt.internal.addWidget"; //$NON-NLS-1$
	long /* int */[] closures;
	int[] signalIds;
	long /* int */shellMapProcClosure;

	/* Widget Table */
	int[] indexTable;
	int freeSlot;
	long /* int */lastHandle;
	Widget lastWidget;
	Widget[] widgetTable;
	final static int GROW_SIZE = 1024;

	/* Modality */
	Shell[] modalShells;
	Dialog modalDialog;
	static final String GET_MODAL_DIALOG = "org.eclipse.swt.internal.gtk.getModalDialog"; //$NON-NLS-1$
	static final String SET_MODAL_DIALOG = "org.eclipse.swt.internal.gtk.setModalDialog"; //$NON-NLS-1$

	/* Focus */
	int focusEvent;
	Control focusControl;
	Shell activeShell;
	boolean activePending;
	boolean ignoreActivate, ignoreFocus;

	/* Input method resources */
	Control imControl;
	long /* int */preeditWindow, preeditLabel;

	/* Sync/Async Widget Communication */
	Synchronizer synchronizer = new Synchronizer(this);

	/**
	 * TODO Thread thread;
	 */

	/* Display Shutdown */
	Runnable[] disposeList;

	/* Deferred Layout list */
	Composite[] layoutDeferred;
	int layoutDeferredCount;

	/* System Tray */
	Tray tray;
	TrayItem currentTrayItem;

	/* Timers */
	int[] timerIds;
	Runnable[] timerList;
	Callback timerCallback;
	long /* int */timerProc;
	Callback windowTimerCallback;
	long /* int */windowTimerProc;

	/* Caret */
	Caret currentCaret;
	Callback caretCallback;
	int caretId;
	long /* int */caretProc;

	/* Mnemonics */
	Control mnemonicControl;

	/* Mouse hover */
	int mouseHoverId;
	long /* int */mouseHoverHandle, mouseHoverProc;
	Callback mouseHoverCallback;

	/* Menu position callback */
	long /* int */menuPositionProc;
	Callback menuPositionCallback;

	/* Tooltip size allocate callback */
	long /* int */sizeAllocateProc;
	Callback sizeAllocateCallback;
	long /* int */sizeRequestProc;
	Callback sizeRequestCallback;

	/* Shell map callback */
	long /* int */shellMapProc;
	Callback shellMapCallback;

	/* Idle proc callback */
	long /* int */idleProc;
	int idleHandle;
	Callback idleCallback;
	static final String ADD_IDLE_PROC_KEY = "org.eclipse.swt.internal.gtk.addIdleProc"; //$NON-NLS-1$
	static final String REMOVE_IDLE_PROC_KEY = "org.eclipse.swt.internal.gtk.removeIdleProc"; //$NON-NLS-1$
	Object idleLock = new Object();
	boolean idleNeeded;

	/* GtkTreeView callbacks */
	int[] treeSelection;
	int treeSelectionLength;
	long /* int */treeSelectionProc;
	Callback treeSelectionCallback;
	long /* int */cellDataProc;
	Callback cellDataCallback;

	/* Set direction callback */
	long /* int */setDirectionProc;
	Callback setDirectionCallback;
	static final String GET_DIRECTION_PROC_KEY = "org.eclipse.swt.internal.gtk.getDirectionProc"; //$NON-NLS-1$

	/* Set emissionProc callback */
	long /* int */emissionProc;
	Callback emissionProcCallback;
	static final String GET_EMISSION_PROC_KEY = "org.eclipse.swt.internal.gtk.getEmissionProc"; //$NON-NLS-1$

	/* Get all children callback */
	long /* int */allChildrenProc, allChildren;
	Callback allChildrenCallback;

	/* Settings callbacks */
	long /* int */signalProc;
	Callback signalCallback;
	long /* int */shellHandle;
	boolean settingsChanged, runSettings;
	static final int STYLE_SET = 1;
	static final int PROPERTY_NOTIFY = 2;

	/* Entry focus behaviour */
	boolean entrySelectOnFocus;

	/* Enter/Exit events */
	Control currentControl;

	/* Flush exposes */
	long /* int */checkIfEventProc;
	Callback checkIfEventCallback;
	long /* int */flushWindow;
	boolean flushAll;
	GdkRectangle flushRect = new GdkRectangle();
	XExposeEvent exposeEvent = new XExposeEvent();
	XVisibilityEvent visibilityEvent = new XVisibilityEvent();
	long /* int */[] flushData = new long /* int */[1];

	/* System Resources */
	Font systemFont;
	Image errorImage, infoImage, questionImage, warningImage;
	Cursor[] cursors = new Cursor[SWT.CURSOR_HAND + 1];
	Resource[] resources;
	static final int RESOURCE_SIZE = 1 + 4 + SWT.CURSOR_HAND + 1;

	/* Colors */
	GdkColor COLOR_WIDGET_DARK_SHADOW, COLOR_WIDGET_NORMAL_SHADOW,
			COLOR_WIDGET_LIGHT_SHADOW;
	GdkColor COLOR_WIDGET_HIGHLIGHT_SHADOW, COLOR_WIDGET_BACKGROUND,
			COLOR_WIDGET_FOREGROUND, COLOR_WIDGET_BORDER;
	GdkColor COLOR_LIST_FOREGROUND, COLOR_LIST_BACKGROUND,
			COLOR_LIST_SELECTION, COLOR_LIST_SELECTION_TEXT;
	GdkColor COLOR_INFO_BACKGROUND, COLOR_INFO_FOREGROUND;
	GdkColor COLOR_TITLE_FOREGROUND, COLOR_TITLE_BACKGROUND,
			COLOR_TITLE_BACKGROUND_GRADIENT;
	GdkColor COLOR_TITLE_INACTIVE_FOREGROUND, COLOR_TITLE_INACTIVE_BACKGROUND,
			COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT;

	/* Popup Menus */
	Menu[] popups;

	/* Click count */
	int clickCount = 1;

	/* Entry inner border */
	static final int INNER_BORDER = 2;

	/* Timestamp of the Last Received Events */
	int lastEventTime, lastUserEventTime;

	/* Pango layout constructor */
	long /* int */pangoLayoutNewProc;

	/* Custom Resize */
	double resizeLocationX, resizeLocationY;
	int resizeBoundsX, resizeBoundsY, resizeBoundsWidth, resizeBoundsHeight;
	int resizeMode;

	/* Fixed Subclass */
	static long /* int */fixed_type;
	static long /* int */fixed_info_ptr;
	static Callback fixedClassInitCallback, fixedMapCallback,
			fixedSizeAllocateCallback;
	static long /* int */fixedClassInitProc, fixedMapProc,
			fixedSizeAllocateProc, oldFixedSizeAllocateProc;

	/* Renderer Subclass */
	static long /* int */text_renderer_type, pixbuf_renderer_type,
			toggle_renderer_type;
	static long /* int */text_renderer_info_ptr, pixbuf_renderer_info_ptr,
			toggle_renderer_info_ptr;
	static Callback rendererClassInitCallback, rendererRenderCallback,
			rendererGetSizeCallback;
	static long /* int */rendererClassInitProc, rendererRenderProc,
			rendererGetSizeProc;

	/* Multiple Displays. */
	static Display Default;
	static Display[] Displays = new Display[4];

	/* Skinning support */
	Widget[] skinList = new Widget[GROW_SIZE];
	int skinCount;

	/* Package name */
	static final String PACKAGE_PREFIX = "org.eclipse.swt.widgets."; //$NON-NLS-1$

	/* GTK Version */
	static final int MAJOR = 2;
	static final int MINOR = 2;
	static final int MICRO = 0;

	/* Display Data */
	Object data;
	String[] keys;
	Object[] values;

	/* Initial Guesses for Shell Trimmings. */
	int borderTrimWidth = 4, borderTrimHeight = 4;
	int resizeTrimWidth = 6, resizeTrimHeight = 6;
	int titleBorderTrimWidth = 5, titleBorderTrimHeight = 28;
	int titleResizeTrimWidth = 6, titleResizeTrimHeight = 29;
	int titleTrimWidth = 0, titleTrimHeight = 23;
	boolean ignoreTrim;

	/* Window Manager */
	String windowManager;

	/*
	 * TEMPORARY CODE. Install the runnable that gets the current display. This
	 * code will be removed in the future.
	 */
	static {
		DeviceFinder = new Runnable() {
			public void run() {
				Device device = getCurrent();
				if (device == null) {
					device = getDefault();
				}
				setDevice(device);
			}
		};
	}

	/*
	 * TEMPORARY CODE.
	 */
	static void setDevice(Device device) {
		CurrentDevice = device;
	}

	/**
	 * Constructs a new instance of this class.
	 * <p>
	 * Note: The resulting display is marked as the <em>current</em> display. If
	 * this is the first display which has been constructed since the
	 * application started, it is also marked as the <em>default</em> display.
	 * </p>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if called from a thread
	 *                that already created an existing display</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 * 
	 * @see #getCurrent
	 * @see #getDefault
	 * @see Widget#checkSubclass
	 * @see Shell
	 */
	public Display() {
		this(null);
	}

	/**
	 * Constructs a new instance of this class using the parameter.
	 * 
	 * @param data
	 *            the device data
	 */
	public Display(DeviceData data) {
		super(data);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when an event of the given type occurs anywhere in a widget. The event
	 * type is one of the event constants defined in class <code>SWT</code>.
	 * When the event does occur, the listener is notified by sending it the
	 * <code>handleEvent()</code> message.
	 * <p>
	 * Setting the type of an event to <code>SWT.None</code> from within the
	 * <code>handleEvent()</code> method can be used to change the event type
	 * and stop subsequent Java listeners from running. Because event filters
	 * run before other listeners, event filters can both block other listeners
	 * and set arbitrary fields within an event. For this reason, event filters
	 * are both powerful and dangerous. They should generally be avoided for
	 * performance, debugging and code maintenance reasons.
	 * </p>
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Listener
	 * @see SWT
	 * @see #removeFilter
	 * @see #removeListener
	 * 
	 * @since 3.0
	 */
	public void addFilter(int eventType, Listener listener) {
		checkDevice();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (filterTable == null)
			filterTable = new EventTable();
		filterTable.hook(eventType, listener);
	}

	void addLayoutDeferred(Composite comp) {
		if (layoutDeferred == null)
			layoutDeferred = new Composite[64];
		if (layoutDeferredCount == layoutDeferred.length) {
			Composite[] temp = new Composite[layoutDeferred.length + 64];
			System.arraycopy(layoutDeferred, 0, temp, 0, layoutDeferred.length);
			layoutDeferred = temp;
		}
		layoutDeferred[layoutDeferredCount++] = comp;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when an event of the given type occurs. The event type is one of the
	 * event constants defined in class <code>SWT</code>. When the event does
	 * occur in the display, the listener is notified by sending it the
	 * <code>handleEvent()</code> message.
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Listener
	 * @see SWT
	 * @see #removeListener
	 * 
	 * @since 2.0
	 */
	public void addListener(int eventType, Listener listener) {
		checkDevice();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			eventTable = new EventTable();
		eventTable.hook(eventType, listener);
	}

	void addPopup(Menu menu) {
		if (popups == null)
			popups = new Menu[4];
		int length = popups.length;
		for (int i = 0; i < length; i++) {
			if (popups[i] == menu)
				return;
		}
		int index = 0;
		while (index < length) {
			if (popups[index] == null)
				break;
			index++;
		}
		if (index == length) {
			Menu[] newPopups = new Menu[length + 4];
			System.arraycopy(popups, 0, newPopups, 0, length);
			popups = newPopups;
		}
		popups[index] = menu;
	}

	void addSkinnableWidget(Widget widget) {
		if (skinCount >= skinList.length) {
			Widget[] newSkinWidgets = new Widget[skinList.length + GROW_SIZE];
			System.arraycopy(skinList, 0, newSkinWidgets, 0, skinList.length);
			skinList = newSkinWidgets;
		}
		skinList[skinCount++] = widget;
	}

	void addWidget(long /* int */handle, Widget widget) {
	}

	/**
	 * Causes the <code>run()</code> method of the runnable to be invoked by the
	 * user-interface thread at the next reasonable opportunity. The caller of
	 * this method continues to run in parallel, and is not notified when the
	 * runnable has completed. Specifying <code>null</code> as the runnable
	 * simply wakes the user-interface thread when run.
	 * <p>
	 * Note that at the time the runnable is invoked, widgets that have the
	 * receiver as their display may have been disposed. Therefore, it is
	 * necessary to check for this case inside the runnable before accessing the
	 * widget.
	 * </p>
	 * 
	 * @param runnable
	 *            code to run on the user-interface thread or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #syncExec
	 */
	public void asyncExec(Runnable runnable) {
		timerExec(1, runnable);
	}

	/**
	 * Causes the system hardware to emit a short sound (if it supports this
	 * capability).
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void beep() {
	}

	protected void checkDevice() {
	}

	/**
	 * Checks that this class can be subclassed.
	 * <p>
	 * IMPORTANT: See the comment in <code>Widget.checkSubclass()</code>.
	 * </p>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 * 
	 * @see Widget#checkSubclass
	 */
	protected void checkSubclass() {
		if (!isValidClass(getClass()))
			error(SWT.ERROR_INVALID_SUBCLASS);
	}

	/**
	 * Requests that the connection between SWT and the underlying operating
	 * system be closed.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Device#dispose
	 * 
	 * @since 2.0
	 */
	public void close() {
		checkDevice();
		Event event = new Event();
		sendEvent(SWT.Close, event);
		if (event.doit)
			dispose();
	}

	/**
	 * Creates the device in the operating system. If the device does not have a
	 * handle, this method may do nothing depending on the device.
	 * <p>
	 * This method is called before <code>init</code>.
	 * </p>
	 * 
	 * @param data
	 *            the DeviceData which describes the receiver
	 * 
	 * @see #init
	 */
	protected void create(DeviceData data) {
		// checkSubclass();
		// checkDisplay(thread = Thread.currentThread(), false);
		// createDisplay(data);
		// register(this);
		// if (Default == null)
		// Default = this;
	}

	Image createImage(String name) {
		return null;
	}

	static void deregister(Display display) {
		synchronized (Device.class) {
			for (int i = 0; i < Displays.length; i++) {
				if (display == Displays[i])
					Displays[i] = null;
			}
		}
	}

	/**
	 * Destroys the device in the operating system and releases the device's
	 * handle. If the device does not have a handle, this method may do nothing
	 * depending on the device.
	 * <p>
	 * This method is called after <code>release</code>.
	 * </p>
	 * 
	 * @see Device#dispose
	 * @see #release
	 */
	protected void destroy() {
		if (this == Default)
			Default = null;
		deregister(this);
		destroyDisplay();
	}

	void destroyDisplay() {
	}

	/**
	 * Returns the display which the given thread is the user-interface thread
	 * for, or null if the given thread is not a user-interface thread for any
	 * display. Specifying <code>null</code> as the thread will return
	 * <code>null</code> for the display.
	 * 
	 * @param thread
	 *            the user-interface thread
	 * @return the display for the given thread
	 */
	public static Display findDisplay(/* Thread thread */) {
		return null;
	}

	/**
	 * Causes the <code>run()</code> method of the runnable to be invoked by the
	 * user-interface thread just before the receiver is disposed. Specifying a
	 * <code>null</code> runnable is ignored.
	 * 
	 * @param runnable
	 *            code to run at dispose time.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void disposeExec(Runnable runnable) {
		checkDevice();
		if (disposeList == null)
			disposeList = new Runnable[4];
		for (int i = 0; i < disposeList.length; i++) {
			if (disposeList[i] == null) {
				disposeList[i] = runnable;
				return;
			}
		}
		Runnable[] newDisposeList = new Runnable[disposeList.length + 4];
		System.arraycopy(disposeList, 0, newDisposeList, 0, disposeList.length);
		newDisposeList[disposeList.length] = runnable;
		disposeList = newDisposeList;
	}

	/**
	 * Does whatever display specific cleanup is required, and then uses the
	 * code in <code>SWTError.error</code> to handle the error.
	 * 
	 * @param code
	 *            the descriptive error code
	 * 
	 * @see SWTError#error
	 */
	void error(int code) {
		SWT.error(code);
	}

	/**
	 * Given the operating system handle for a widget, returns the instance of
	 * the <code>Widget</code> subclass which represents it in the currently
	 * running application, if such exists, or null if no matching widget can be
	 * found.
	 * <p>
	 * <b>IMPORTANT:</b> This method should not be called from application code.
	 * The arguments are platform-specific.
	 * </p>
	 * 
	 * @param handle
	 *            the handle for the widget
	 * @return the SWT widget that the handle represents
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public Widget findWidget(long /* int */handle) {
		checkDevice();
		return getWidget(handle);
	}

	/**
	 * Given the operating system handle for a widget, and widget-specific id,
	 * returns the instance of the <code>Widget</code> subclass which represents
	 * the handle/id pair in the currently running application, if such exists,
	 * or null if no matching widget can be found.
	 * <p>
	 * <b>IMPORTANT:</b> This method should not be called from application code.
	 * The arguments are platform-specific.
	 * </p>
	 * 
	 * @param handle
	 *            the handle for the widget
	 * @param id
	 *            the id for the subwidget (usually an item)
	 * @return the SWT widget that the handle/id pair represents
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 * 
	 * @since 3.1
	 */
	public Widget findWidget(long /* int */handle, long /* int */id) {
		checkDevice();
		return null;
	}

	/**
	 * Given a widget and a widget-specific id, returns the instance of the
	 * <code>Widget</code> subclass which represents the widget/id pair in the
	 * currently running application, if such exists, or null if no matching
	 * widget can be found.
	 * 
	 * @param widget
	 *            the widget
	 * @param id
	 *            the id for the subwidget (usually an item)
	 * @return the SWT subwidget (usually an item) that the widget/id pair
	 *         represents
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.3
	 */
	public Widget findWidget(Widget widget, long /* int */id) {
		checkDevice();
		return null;
	}

	/**
	 * Returns the currently active <code>Shell</code>, or null if no shell
	 * belonging to the currently running application is active.
	 * 
	 * @return the active shell or null
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Shell getActiveShell() {
		checkDevice();
		return activeShell;
	}

	/**
	 * Returns a rectangle describing the receiver's size and location. Note
	 * that on multi-monitor systems the origin can be negative.
	 * 
	 * @return the bounding rectangle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Rectangle getBounds() {
		return null;
	}

	/**
	 * Returns the display which the currently running thread is the
	 * user-interface thread for, or null if the currently running thread is not
	 * a user-interface thread for any display.
	 * 
	 * @return the current display
	 */
	public static Display getCurrent() {
		// return findDisplay(Thread.currentThread());
		return getDefault();
	}

	int getCaretBlinkTime() {
		return 0;
	}

	/**
	 * Returns the control which the on-screen pointer is currently over top of,
	 * or null if it is not currently over one of the controls built by the
	 * currently running application.
	 * 
	 * @return the control under the cursor or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Control getCursorControl() {
		return null;
	}

	boolean filterEvent(Event event) {
		if (filterTable != null)
			filterTable.sendEvent(event);
		return false;
	}

	boolean filters(int eventType) {
		if (filterTable == null)
			return false;
		return filterTable.hooks(eventType);
	}

	/**
	 * Returns the location of the on-screen pointer relative to the top left
	 * corner of the screen.
	 * 
	 * @return the cursor location
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Point getCursorLocation() {
		return new Point(100, 100);
	}

	/**
	 * Returns an array containing the recommended cursor sizes.
	 * 
	 * @return the array of cursor sizes
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.0
	 */
	public Point[] getCursorSizes() {
		checkDevice();
		return new Point[] { new Point(16, 16), new Point(32, 32) };
	}

	/**
	 * Returns the application defined property of the receiver with the
	 * specified name, or null if it has not been set.
	 * <p>
	 * Applications may have associated arbitrary objects with the receiver in
	 * this fashion. If the objects stored in the properties need to be notified
	 * when the display is disposed of, it is the application's responsibility
	 * to provide a <code>disposeExec()</code> handler which does so.
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #setData(String, Object)
	 * @see #disposeExec(Runnable)
	 */
	public Object getData(String key) {
		return null;
	}

	/**
	 * Returns the application defined, display specific data associated with
	 * the receiver, or null if it has not been set. The
	 * <em>display specific data</em> is a single, unnamed field that is stored
	 * with every display.
	 * <p>
	 * Applications may put arbitrary objects in this field. If the object
	 * stored in the display specific data needs to be notified when the display
	 * is disposed of, it is the application's responsibility to provide a
	 * <code>disposeExec()</code> handler which does so.
	 * </p>
	 * 
	 * @return the display specific data
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #setData(Object)
	 * @see #disposeExec(Runnable)
	 */
	public Object getData() {
		checkDevice();
		return data;
	}

	/**
	 * Returns the default display. One is created (making the thread that
	 * invokes this method its user-interface thread) if it did not already
	 * exist.
	 * 
	 * @return the default display
	 */
	public static Display getDefault() {
		synchronized (Device.class) {
			if (Default == null)
				Default = new Display();
			return Default;
		}
	}

	static boolean isValidClass(Class clazz) {
		String name = clazz.getName();
		int index = name.lastIndexOf('.');
		return name.substring(0, index + 1).equals(PACKAGE_PREFIX);
	}

	/**
	 * Returns the single instance of the application menu bar, or
	 * <code>null</code> if there is no application menu bar for the platform.
	 * 
	 * @return the application menu bar, or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.7
	 */
	public Menu getMenuBar() {
		checkDevice();
		return null;
	}

	/**
	 * Returns the button dismissal alignment, one of <code>LEFT</code> or
	 * <code>RIGHT</code>. The button dismissal alignment is the ordering that
	 * should be used when positioning the default dismissal button for a
	 * dialog. For example, in a dialog that contains an OK and CANCEL button,
	 * on platforms where the button dismissal alignment is <code>LEFT</code>,
	 * the button ordering should be OK/CANCEL. When button dismissal alignment
	 * is <code>RIGHT</code>, the button ordering should be CANCEL/OK.
	 * 
	 * @return the button dismissal order
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 2.1
	 */
	public int getDismissalAlignment() {
		return 0;
	}

	/**
	 * Returns the longest duration, in milliseconds, between two mouse button
	 * clicks that will be considered a <em>double click</em> by the underlying
	 * operating system.
	 * 
	 * @return the double click time
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public int getDoubleClickTime() {
		return 0;
	}

	/**
	 * Returns the control which currently has keyboard focus, or null if
	 * keyboard events are not currently going to any of the controls built by
	 * the currently running application.
	 * 
	 * @return the focus control or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Control getFocusControl() {
		return null;
	}

	/**
	 * Returns true when the high contrast mode is enabled. Otherwise, false is
	 * returned.
	 * <p>
	 * Note: This operation is a hint and is not supported on platforms that do
	 * not have this concept.
	 * </p>
	 * 
	 * @return the high contrast mode
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.0
	 */
	public boolean getHighContrast() {
		checkDevice();
		return false;
	}

	public int getDepth() {
		return 0;
	}

	/**
	 * Returns the maximum allowed depth of icons on this display, in bits per
	 * pixel. On some platforms, this may be different than the actual depth of
	 * the display.
	 * 
	 * @return the maximum icon depth
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Device#getDepth
	 */
	public int getIconDepth() {
		checkDevice();
		return getDepth();
	}

	/**
	 * Returns an array containing the recommended icon sizes.
	 * 
	 * @return the array of icon sizes
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Decorations#setImages(Image[])
	 * 
	 * @since 3.0
	 */
	public Point[] getIconSizes() {
		checkDevice();
		return new Point[] { new Point(16, 16), new Point(32, 32) };
	}

	int getLastEventTime() {
		return lastEventTime;
	}

	Dialog getModalDialog() {
		return modalDialog;
	}

	/**
	 * Returns an array of monitors attached to the device.
	 * 
	 * @return the array of monitors
	 * 
	 * @since 3.0
	 */
	public Monitor[] getMonitors() {
		return null;
	}

	/**
	 * Returns the primary monitor for that device.
	 * 
	 * @return the primary monitor
	 * 
	 * @since 3.0
	 */
	public Monitor getPrimaryMonitor() {
		checkDevice();
		Monitor[] monitors = getMonitors();
		return monitors[0];
	}

	/**
	 * Returns a (possibly empty) array containing all shells which have not
	 * been disposed and have the receiver as their display.
	 * 
	 * @return the receiver's shells
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Shell[] getShells() {
		return null;
	}

	/**
	 * Gets the synchronizer used by the display.
	 * 
	 * @return the receiver's synchronizer
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.4
	 */
	public Synchronizer getSynchronizer() {
		checkDevice();
		return synchronizer;
	}

	/**
	 * Returns the thread that has invoked <code>syncExec</code> or null if no
	 * such runnable is currently being invoked by the user-interface thread.
	 * <p>
	 * Note: If a runnable invoked by asyncExec is currently running, this
	 * method will return null.
	 * </p>
	 * 
	 * @return the receiver's sync-interface thread
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */

	/**
	 * TODO public Thread getSyncThread() { synchronized (Device.class) { if
	 * (isDisposed()) error(SWT.ERROR_DEVICE_DISPOSED); return
	 * synchronizer.syncThread; } }
	 */

	/**
	 * Returns the matching standard color for the given constant, which should
	 * be one of the color constants specified in class <code>SWT</code>. Any
	 * value other than one of the SWT color constants which is passed in will
	 * result in the color black. This color should not be free'd because it was
	 * allocated by the system, not the application.
	 * 
	 * @param id
	 *            the color constant
	 * @return the matching color
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see SWT
	 */
	public Color getSystemColor(int id) {
		checkDevice();
		GdkColor gdkColor = null;
		switch (id) {
		case SWT.COLOR_INFO_FOREGROUND:
			gdkColor = COLOR_INFO_FOREGROUND;
			break;
		case SWT.COLOR_INFO_BACKGROUND:
			gdkColor = COLOR_INFO_BACKGROUND;
			break;
		case SWT.COLOR_TITLE_FOREGROUND:
			gdkColor = COLOR_TITLE_FOREGROUND;
			break;
		case SWT.COLOR_TITLE_BACKGROUND:
			gdkColor = COLOR_TITLE_BACKGROUND;
			break;
		case SWT.COLOR_TITLE_BACKGROUND_GRADIENT:
			gdkColor = COLOR_TITLE_BACKGROUND_GRADIENT;
			break;
		case SWT.COLOR_TITLE_INACTIVE_FOREGROUND:
			gdkColor = COLOR_TITLE_INACTIVE_FOREGROUND;
			break;
		case SWT.COLOR_TITLE_INACTIVE_BACKGROUND:
			gdkColor = COLOR_TITLE_INACTIVE_BACKGROUND;
			break;
		case SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT:
			gdkColor = COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT;
			break;
		case SWT.COLOR_WIDGET_DARK_SHADOW:
			gdkColor = COLOR_WIDGET_DARK_SHADOW;
			break;
		case SWT.COLOR_WIDGET_NORMAL_SHADOW:
			gdkColor = COLOR_WIDGET_NORMAL_SHADOW;
			break;
		case SWT.COLOR_WIDGET_LIGHT_SHADOW:
			gdkColor = COLOR_WIDGET_LIGHT_SHADOW;
			break;
		case SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW:
			gdkColor = COLOR_WIDGET_HIGHLIGHT_SHADOW;
			break;
		case SWT.COLOR_WIDGET_BACKGROUND:
			gdkColor = COLOR_WIDGET_BACKGROUND;
			break;
		case SWT.COLOR_WIDGET_FOREGROUND:
			gdkColor = COLOR_WIDGET_FOREGROUND;
			break;
		case SWT.COLOR_WIDGET_BORDER:
			gdkColor = COLOR_WIDGET_BORDER;
			break;
		case SWT.COLOR_LIST_FOREGROUND:
			gdkColor = COLOR_LIST_FOREGROUND;
			break;
		case SWT.COLOR_LIST_BACKGROUND:
			gdkColor = COLOR_LIST_BACKGROUND;
			break;
		case SWT.COLOR_LIST_SELECTION:
			gdkColor = COLOR_LIST_SELECTION;
			break;
		case SWT.COLOR_LIST_SELECTION_TEXT:
			gdkColor = COLOR_LIST_SELECTION_TEXT;
			break;
		default:
			return super.getSystemColor(id);
		}
		if (gdkColor == null)
			return super.getSystemColor(SWT.COLOR_BLACK);
		return Color.gtk_new(this, gdkColor);
	}

	/**
	 * Returns the matching standard platform cursor for the given constant,
	 * which should be one of the cursor constants specified in class
	 * <code>SWT</code>. This cursor should not be free'd because it was
	 * allocated by the system, not the application. A value of
	 * <code>null</code> will be returned if the supplied constant is not an SWT
	 * cursor constant.
	 * 
	 * @param id
	 *            the SWT cursor constant
	 * @return the corresponding cursor or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see SWT#CURSOR_ARROW
	 * @see SWT#CURSOR_WAIT
	 * @see SWT#CURSOR_CROSS
	 * @see SWT#CURSOR_APPSTARTING
	 * @see SWT#CURSOR_HELP
	 * @see SWT#CURSOR_SIZEALL
	 * @see SWT#CURSOR_SIZENESW
	 * @see SWT#CURSOR_SIZENS
	 * @see SWT#CURSOR_SIZENWSE
	 * @see SWT#CURSOR_SIZEWE
	 * @see SWT#CURSOR_SIZEN
	 * @see SWT#CURSOR_SIZES
	 * @see SWT#CURSOR_SIZEE
	 * @see SWT#CURSOR_SIZEW
	 * @see SWT#CURSOR_SIZENE
	 * @see SWT#CURSOR_SIZESE
	 * @see SWT#CURSOR_SIZESW
	 * @see SWT#CURSOR_SIZENW
	 * @see SWT#CURSOR_UPARROW
	 * @see SWT#CURSOR_IBEAM
	 * @see SWT#CURSOR_NO
	 * @see SWT#CURSOR_HAND
	 * 
	 * @since 3.0
	 */
	public Cursor getSystemCursor(int id) {
		checkDevice();
		if (!(0 <= id && id < cursors.length))
			return null;
		if (cursors[id] == null) {
			cursors[id] = new Cursor(this, id);
		}
		return cursors[id];
	}

	/**
	 * Returns the matching standard platform image for the given constant,
	 * which should be one of the icon constants specified in class
	 * <code>SWT</code>. This image should not be free'd because it was
	 * allocated by the system, not the application. A value of
	 * <code>null</code> will be returned either if the supplied constant is not
	 * an SWT icon constant or if the platform does not define an image that
	 * corresponds to the constant.
	 * 
	 * @param id
	 *            the SWT icon constant
	 * @return the corresponding image or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see SWT#ICON_ERROR
	 * @see SWT#ICON_INFORMATION
	 * @see SWT#ICON_QUESTION
	 * @see SWT#ICON_WARNING
	 * @see SWT#ICON_WORKING
	 * 
	 * @since 3.0
	 */
	public Image getSystemImage(int id) {
		checkDevice();
		switch (id) {
		case SWT.ICON_ERROR:
			if (errorImage == null) {
				errorImage = createImage("gtk-dialog-error"); //$NON-NLS-1$
			}
			return errorImage;
		case SWT.ICON_INFORMATION:
		case SWT.ICON_WORKING:
			if (infoImage == null) {
				infoImage = createImage("gtk-dialog-info"); //$NON-NLS-1$
			}
			return infoImage;
		case SWT.ICON_QUESTION:
			if (questionImage == null) {
				questionImage = createImage("gtk-dialog-question"); //$NON-NLS-1$
			}
			return questionImage;
		case SWT.ICON_WARNING:
			if (warningImage == null) {
				warningImage = createImage("gtk-dialog-warning"); //$NON-NLS-1$
			}
			return warningImage;
		}
		return null;
	}

	/**
	 * Returns the single instance of the system-provided menu for the
	 * application, or <code>null</code> on platforms where no menu is provided
	 * for the application.
	 * 
	 * @return the system menu, or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.7
	 */
	public Menu getSystemMenu() {
		checkDevice();
		return null;
	}

	void initializeSystemColors() {
		COLOR_INFO_FOREGROUND = gdkColorGenerator(0, 0, 0);
		COLOR_INFO_BACKGROUND = gdkColorGenerator(255, 255, 255);
		COLOR_WIDGET_DARK_SHADOW = gdkColorGenerator(64, 64, 64);
		COLOR_WIDGET_NORMAL_SHADOW = gdkColorGenerator(128, 128, 128);
		COLOR_WIDGET_LIGHT_SHADOW = gdkColorGenerator(212, 208, 200);
		COLOR_WIDGET_HIGHLIGHT_SHADOW = gdkColorGenerator(192, 192, 192);
		COLOR_WIDGET_FOREGROUND = gdkColorGenerator(0, 0, 0);
		COLOR_WIDGET_BACKGROUND = gdkColorGenerator(212, 208, 200);
		COLOR_LIST_FOREGROUND = gdkColorGenerator(0, 0, 0);
		COLOR_LIST_BACKGROUND = gdkColorGenerator(255, 255, 255);
		COLOR_LIST_SELECTION_TEXT = gdkColorGenerator(0, 0, 0);
		COLOR_LIST_SELECTION = gdkColorGenerator(10, 36, 106);
		COLOR_TITLE_BACKGROUND = gdkColorGenerator(10, 36, 106);
		COLOR_TITLE_FOREGROUND = gdkColorGenerator(255, 255, 255);
		COLOR_TITLE_BACKGROUND_GRADIENT = gdkColorGenerator(166, 202, 240);
		COLOR_TITLE_INACTIVE_BACKGROUND = gdkColorGenerator(128, 128, 128);
		COLOR_TITLE_INACTIVE_FOREGROUND = gdkColorGenerator(212, 208, 200);
		COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT = gdkColorGenerator(192, 192,
				192);
	}

	private GdkColor gdkColorGenerator(int red, int green, int blue) {
		GdkColor gdkColor = new GdkColor();
		gdkColor.red = (short) ((red & 0xFF) | ((red & 0xFF) << 8));
		gdkColor.green = (short) ((green & 0xFF) | ((green & 0xFF) << 8));
		gdkColor.blue = (short) ((blue & 0xFF) | ((blue & 0xFF) << 8));
		return gdkColor;
	}

	/**
	 * Returns a reasonable font for applications to use. On some platforms,
	 * this will match the "default font" or "system font" if such can be found.
	 * This font should not be free'd because it was allocated by the system,
	 * not the application.
	 * <p>
	 * Typically, applications which want the default look should simply not set
	 * the font on the widgets they create. Widgets are always created with the
	 * correct default font for the class of user-interface component they
	 * represent.
	 * </p>
	 * 
	 * @return a font
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Font getSystemFont() {
		return new Font(null, new FontData());
	}

	/**
	 * Returns the single instance of the system taskBar or null when there is
	 * no system taskBar available for the platform.
	 * 
	 * @return the system taskBar or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.6
	 */
	public TaskBar getSystemTaskBar() {
		checkDevice();
		return null;
	}

	/**
	 * Returns the single instance of the system tray or null when there is no
	 * system tray available for the platform.
	 * 
	 * @return the system tray or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.0
	 */
	public Tray getSystemTray() {
		checkDevice();
		if (tray != null)
			return tray;
		return tray = new Tray(this, SWT.NONE);
	}

	/**
	 * Returns the user-interface thread for the receiver.
	 * 
	 * @return the receiver's user-interface thread
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */

	/**
	 * TODO public Thread getThread() { // synchronized (Device.class) { // if
	 * (isDisposed()) // error(SWT.ERROR_DEVICE_DISPOSED); // return thread; //
	 * } System.out.println("Display.getThread() returns null"); return null; }
	 */

	/**
	 * Returns a boolean indicating whether a touch-aware input device is
	 * attached to the system and is ready for use.
	 * 
	 * @return <code>true</code> if a touch-aware input device is detected, or
	 *         <code>false</code> otherwise
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.7
	 */
	public boolean getTouchEnabled() {
		checkDevice();
		return false;
	}

	Widget getWidget(long /* int */handle) {
		return null;
	}

	/**
	 * Initializes any internal resources needed by the device.
	 * <p>
	 * This method is called after <code>create</code>.
	 * </p>
	 * 
	 * @see #create
	 */
	protected void init() {
		super.init();
		initializeCallbacks();
		initializeSubclasses();
		initializeSystemColors();
		initializeSystemSettings();
		initializeWidgetTable();
		initializeWindowManager();
	}

	void initializeCallbacks() {
	}

	void initializeSubclasses() {
	}

	void initializeSystemSettings() {
	}

	void initializeWidgetTable() {
		indexTable = new int[GROW_SIZE];
		widgetTable = new Widget[GROW_SIZE];
		for (int i = 0; i < GROW_SIZE - 1; i++)
			indexTable[i] = i + 1;
		indexTable[GROW_SIZE - 1] = -1;
	}

	void initializeWindowManager() {
	}

	/**
	 * Invokes platform specific functionality to dispose a GC handle.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Display</code>. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms, and should never be called from application code.
	 * </p>
	 * 
	 * @param hDC
	 *            the platform specific GC handle
	 * @param data
	 *            the platform specific GC data
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void internal_dispose_GC(long /* int */gdkGC, GCData data) {
	}

	/**
	 * Invokes platform specific functionality to allocate a new GC handle.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Display</code>. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms, and should never be called from application code.
	 * </p>
	 * 
	 * @param data
	 *            the platform specific GC data
	 * @return the platform specific GC handle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                gc creation</li>
	 *                </ul>
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public long /* int */internal_new_GC(GCData data) {
		return 0;
	}

	boolean isValidThread() {
		return true;
	}

	/**
	 * Maps a point from one coordinate system to another. When the control is
	 * null, coordinates are mapped to the display.
	 * <p>
	 * NOTE: On right-to-left platforms where the coordinate systems are
	 * mirrored, special care needs to be taken when mapping coordinates from
	 * one control to another to ensure the result is correctly mirrored.
	 * 
	 * Mapping a point that is the origin of a rectangle and then adding the
	 * width and height is not equivalent to mapping the rectangle. When one
	 * control is mirrored and the other is not, adding the width and height to
	 * a point that was mapped causes the rectangle to extend in the wrong
	 * direction. Mapping the entire rectangle instead of just one point causes
	 * both the origin and the corner of the rectangle to be mapped.
	 * </p>
	 * 
	 * @param from
	 *            the source <code>Control</code> or <code>null</code>
	 * @param to
	 *            the destination <code>Control</code> or <code>null</code>
	 * @param point
	 *            to be mapped
	 * @return point with mapped coordinates
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the point is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the Control from or the
	 *                Control to have been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 2.1.2
	 */
	public Point map(Control from, Control to, Point point) {
		checkDevice();
		if (point == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		return map(from, to, point.x, point.y);
	}

	/**
	 * Maps a point from one coordinate system to another. When the control is
	 * null, coordinates are mapped to the display.
	 * <p>
	 * NOTE: On right-to-left platforms where the coordinate systems are
	 * mirrored, special care needs to be taken when mapping coordinates from
	 * one control to another to ensure the result is correctly mirrored.
	 * 
	 * Mapping a point that is the origin of a rectangle and then adding the
	 * width and height is not equivalent to mapping the rectangle. When one
	 * control is mirrored and the other is not, adding the width and height to
	 * a point that was mapped causes the rectangle to extend in the wrong
	 * direction. Mapping the entire rectangle instead of just one point causes
	 * both the origin and the corner of the rectangle to be mapped.
	 * </p>
	 * 
	 * @param from
	 *            the source <code>Control</code> or <code>null</code>
	 * @param to
	 *            the destination <code>Control</code> or <code>null</code>
	 * @param x
	 *            coordinates to be mapped
	 * @param y
	 *            coordinates to be mapped
	 * @return point with mapped coordinates
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the Control from or the
	 *                Control to have been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 2.1.2
	 */
	public Point map(Control from, Control to, int x, int y) {
		return null;
	}

	/**
	 * Maps a point from one coordinate system to another. When the control is
	 * null, coordinates are mapped to the display.
	 * <p>
	 * NOTE: On right-to-left platforms where the coordinate systems are
	 * mirrored, special care needs to be taken when mapping coordinates from
	 * one control to another to ensure the result is correctly mirrored.
	 * 
	 * Mapping a point that is the origin of a rectangle and then adding the
	 * width and height is not equivalent to mapping the rectangle. When one
	 * control is mirrored and the other is not, adding the width and height to
	 * a point that was mapped causes the rectangle to extend in the wrong
	 * direction. Mapping the entire rectangle instead of just one point causes
	 * both the origin and the corner of the rectangle to be mapped.
	 * </p>
	 * 
	 * @param from
	 *            the source <code>Control</code> or <code>null</code>
	 * @param to
	 *            the destination <code>Control</code> or <code>null</code>
	 * @param rectangle
	 *            to be mapped
	 * @return rectangle with mapped coordinates
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the rectangle is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the Control from or the
	 *                Control to have been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 2.1.2
	 */
	public Rectangle map(Control from, Control to, Rectangle rectangle) {
		checkDevice();
		if (rectangle == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		return map(from, to, rectangle.x, rectangle.y, rectangle.width,
				rectangle.height);
	}

	/**
	 * Maps a point from one coordinate system to another. When the control is
	 * null, coordinates are mapped to the display.
	 * <p>
	 * NOTE: On right-to-left platforms where the coordinate systems are
	 * mirrored, special care needs to be taken when mapping coordinates from
	 * one control to another to ensure the result is correctly mirrored.
	 * 
	 * Mapping a point that is the origin of a rectangle and then adding the
	 * width and height is not equivalent to mapping the rectangle. When one
	 * control is mirrored and the other is not, adding the width and height to
	 * a point that was mapped causes the rectangle to extend in the wrong
	 * direction. Mapping the entire rectangle instead of just one point causes
	 * both the origin and the corner of the rectangle to be mapped.
	 * </p>
	 * 
	 * @param from
	 *            the source <code>Control</code> or <code>null</code>
	 * @param to
	 *            the destination <code>Control</code> or <code>null</code>
	 * @param x
	 *            coordinates to be mapped
	 * @param y
	 *            coordinates to be mapped
	 * @param width
	 *            coordinates to be mapped
	 * @param height
	 *            coordinates to be mapped
	 * @return rectangle with mapped coordinates
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the Control from or the
	 *                Control to have been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 2.1.2
	 */
	public Rectangle map(Control from, Control to, int x, int y, int width,
			int height) {
		return null;
	}

	/**
	 * Generate a low level system event.
	 * 
	 * <code>post</code> is used to generate low level keyboard and mouse
	 * events. The intent is to enable automated UI testing by simulating the
	 * input from the user. Most SWT applications should never need to call this
	 * method.
	 * <p>
	 * Note that this operation can fail when the operating system fails to
	 * generate the event for any reason. For example, this can happen when
	 * there is no such key or mouse button or when the system event queue is
	 * full.
	 * </p>
	 * <p>
	 * <b>Event Types:</b>
	 * <p>
	 * KeyDown, KeyUp
	 * <p>
	 * The following fields in the <code>Event</code> apply:
	 * <ul>
	 * <li>(in) type KeyDown or KeyUp</li>
	 * <p>
	 * Either one of:
	 * <li>(in) character a character that corresponds to a keyboard key</li>
	 * <li>(in) keyCode the key code of the key that was typed, as defined by
	 * the key code constants in class <code>SWT</code></li>
	 * </ul>
	 * <p>
	 * MouseDown, MouseUp
	 * </p>
	 * <p>
	 * The following fields in the <code>Event</code> apply:
	 * <ul>
	 * <li>(in) type MouseDown or MouseUp
	 * <li>(in) button the button that is pressed or released
	 * </ul>
	 * <p>
	 * MouseMove
	 * </p>
	 * <p>
	 * The following fields in the <code>Event</code> apply:
	 * <ul>
	 * <li>(in) type MouseMove
	 * <li>(in) x the x coordinate to move the mouse pointer to in screen
	 * coordinates
	 * <li>(in) y the y coordinate to move the mouse pointer to in screen
	 * coordinates
	 * </ul>
	 * <p>
	 * MouseWheel
	 * </p>
	 * <p>
	 * The following fields in the <code>Event</code> apply:
	 * <ul>
	 * <li>(in) type MouseWheel
	 * <li>(in) detail either SWT.SCROLL_LINE or SWT.SCROLL_PAGE
	 * <li>(in) count the number of lines or pages to scroll
	 * </ul>
	 * </dl>
	 * 
	 * @param event
	 *            the event to be generated
	 * 
	 * @return true if the event was generated or false otherwise
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the event is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.0
	 * 
	 */
	public boolean post(Event event) {
		return false;
	}

	void postEvent(Event event) {
		/*
		 * Place the event at the end of the event queue. This code is always
		 * called in the Display's thread so it must be re-enterant but does not
		 * need to be synchronized.
		 */
		if (eventQueue == null)
			eventQueue = new Event[4];
		int index = 0;
		int length = eventQueue.length;
		while (index < length) {
			if (eventQueue[index] == null)
				break;
			index++;
		}
		if (index == length) {
			Event[] newQueue = new Event[length + 4];
			System.arraycopy(eventQueue, 0, newQueue, 0, length);
			eventQueue = newQueue;
		}
		eventQueue[index] = event;
	}

	/**
	 * Reads an event from the operating system's event queue, dispatches it
	 * appropriately, and returns <code>true</code> if there is potentially more
	 * work to do, or <code>false</code> if the caller can sleep until another
	 * event is placed on the event queue.
	 * <p>
	 * In addition to checking the system event queue, this method also checks
	 * if any inter-thread messages (created by <code>syncExec()</code> or
	 * <code>asyncExec()</code>) are waiting to be processed, and if so handles
	 * them before returning.
	 * </p>
	 * 
	 * @return <code>false</code> if the caller can sleep upon return from this
	 *         method
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_FAILED_EXEC - if an exception occurred while
	 *                running an inter-thread message</li>
	 *                </ul>
	 * 
	 * @see #sleep
	 * @see #wake
	 */
	public boolean readAndDispatch() {
		return false;
	}

	static void register(Display display) {
		synchronized (Device.class) {
			for (int i = 0; i < Displays.length; i++) {
				if (Displays[i] == null) {
					Displays[i] = display;
					return;
				}
			}
			Display[] newDisplays = new Display[Displays.length + 4];
			System.arraycopy(Displays, 0, newDisplays, 0, Displays.length);
			newDisplays[Displays.length] = display;
			Displays = newDisplays;
		}
	}

	/**
	 * Releases any internal resources back to the operating system and clears
	 * all fields except the device handle.
	 * <p>
	 * Disposes all shells which are currently open on the display. After this
	 * method has been invoked, all related related shells will answer
	 * <code>true</code> when sent the message <code>isDisposed()</code>.
	 * </p>
	 * <p>
	 * When a device is destroyed, resources that were acquired on behalf of the
	 * programmer need to be returned to the operating system. For example, if
	 * the device allocated a font to be used as the system font, this font
	 * would be freed in <code>release</code>. Also,to assist the garbage
	 * collector and minimize the amount of memory that is not reclaimed when
	 * the programmer keeps a reference to a disposed device, all fields except
	 * the handle are zero'd. The handle is needed by <code>destroy</code>.
	 * </p>
	 * This method is called before <code>destroy</code>.
	 * 
	 * @see Device#dispose
	 * @see #destroy
	 */
	protected void release() {
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when an event of the given type occurs anywhere in a widget. The
	 * event type is one of the event constants defined in class
	 * <code>SWT</code>.
	 * 
	 * @param eventType
	 *            the type of event to listen for
	 * @param listener
	 *            the listener which should no longer be notified when the event
	 *            occurs
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Listener
	 * @see SWT
	 * @see #addFilter
	 * @see #addListener
	 * 
	 * @since 3.0
	 */
	public void removeFilter(int eventType, Listener listener) {
		checkDevice();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (filterTable == null)
			return;
		filterTable.unhook(eventType, listener);
		if (filterTable.size() == 0)
			filterTable = null;
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Listener
	 * @see SWT
	 * @see #addListener
	 * 
	 * @since 2.0
	 */
	public void removeListener(int eventType, Listener listener) {
		checkDevice();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(eventType, listener);
	}

	void removePopup(Menu menu) {
		if (popups == null)
			return;
		for (int i = 0; i < popups.length; i++) {
			if (popups[i] == menu) {
				popups[i] = null;
				return;
			}
		}
	}

	boolean runSkin() {
		return false;
	}

	/**
	 * Returns the application name.
	 * 
	 * @return the application name
	 * 
	 * @see #setAppName(String)
	 * 
	 * @since 3.6
	 */
	public static String getAppName() {
		return APP_NAME;
	}

	/**
	 * Returns the application version.
	 * 
	 * @return the application version
	 * 
	 * @see #setAppVersion(String)
	 * 
	 * @since 3.6
	 */
	public static String getAppVersion() {
		return APP_VERSION;
	}

	/**
	 * Sets the application name to the argument.
	 * <p>
	 * The application name can be used in several ways, depending on the
	 * platform and tools being used. On Motif, for example, this can be used to
	 * set the name used for resource lookup. Accessibility tools may also ask
	 * for the application name.
	 * </p>
	 * <p>
	 * Specifying <code>null</code> for the name clears it.
	 * </p>
	 * 
	 * @param name
	 *            the new app name or <code>null</code>
	 */
	public static void setAppName(String name) {
		APP_NAME = name;
	}

	/**
	 * Sets the application version to the argument.
	 * 
	 * @param version
	 *            the new app version
	 * 
	 * @since 3.6
	 */
	public static void setAppVersion(String version) {
		APP_VERSION = version;
	}

	/**
	 * Sets the location of the on-screen pointer relative to the top left
	 * corner of the screen. <b>Note: It is typically considered bad practice
	 * for a program to move the on-screen pointer location.</b>
	 * 
	 * @param x
	 *            the new x coordinate for the cursor
	 * @param y
	 *            the new y coordinate for the cursor
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 2.1
	 */
	public void setCursorLocation(int x, int y) {
	}

	/**
	 * Sets the location of the on-screen pointer relative to the top left
	 * corner of the screen. <b>Note: It is typically considered bad practice
	 * for a program to move the on-screen pointer location.</b>
	 * 
	 * @param point
	 *            new position
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the point is null
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 2.0
	 */
	public void setCursorLocation(Point point) {
		checkDevice();
		if (point == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		setCursorLocation(point.x, point.y);
	}

	/**
	 * Sets the application defined property of the receiver with the specified
	 * name to the given argument.
	 * <p>
	 * Applications may have associated arbitrary objects with the receiver in
	 * this fashion. If the objects stored in the properties need to be notified
	 * when the display is disposed of, it is the application's responsibility
	 * provide a <code>disposeExec()</code> handler which does so.
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
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #getData(String)
	 * @see #disposeExec(Runnable)
	 */
	public void setData(String key, Object value) {
	}

	/**
	 * Sets the application defined, display specific data associated with the
	 * receiver, to the argument. The <em>display specific data</em> is a
	 * single, unnamed field that is stored with every display.
	 * <p>
	 * Applications may put arbitrary objects in this field. If the object
	 * stored in the display specific data needs to be notified when the display
	 * is disposed of, it is the application's responsibility provide a
	 * <code>disposeExec()</code> handler which does so.
	 * </p>
	 * 
	 * @param data
	 *            the new display specific data
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #getData()
	 * @see #disposeExec(Runnable)
	 */
	public void setData(Object data) {
		checkDevice();
		this.data = data;
	}

	/**
	 * Sets the synchronizer used by the display to be the argument, which can
	 * not be null.
	 * 
	 * @param synchronizer
	 *            the new synchronizer for the display (must not be null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the synchronizer is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_FAILED_EXEC - if an exception occurred while
	 *                running an inter-thread message</li>
	 *                </ul>
	 */
	public void setSynchronizer(Synchronizer synchronizer) {
	}

	/**
	 * Causes the user-interface thread to <em>sleep</em> (that is, to be put in
	 * a state where it does not consume CPU cycles) until an event is received
	 * or it is otherwise awakened.
	 * 
	 * @return <code>true</code> if an event requiring dispatching was placed on
	 *         the queue.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #wake
	 */
	public boolean sleep() {
		return false;
	}

	/**
	 * Causes the <code>run()</code> method of the runnable to be invoked by the
	 * user-interface thread after the specified number of milliseconds have
	 * elapsed. If milliseconds is less than zero, the runnable is not executed.
	 * <p>
	 * Note that at the time the runnable is invoked, widgets that have the
	 * receiver as their display may have been disposed. Therefore, it is
	 * necessary to check for this case inside the runnable before accessing the
	 * widget.
	 * </p>
	 * 
	 * @param milliseconds
	 *            the delay before running the runnable
	 * @param runnable
	 *            code to run on the user-interface thread
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the runnable is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #asyncExec
	 */
	public void timerExec(int milliseconds, final Runnable runnable) {
		Timer execTimer = new Timer() {

			@Override
			public void run() {
				runnable.run();
			}

		};
		execTimer.schedule(milliseconds);
	}

	void sendEvent(int eventType, Event event) {
		if (eventTable == null && filterTable == null) {
			return;
		}
		if (event == null)
			event = new Event();
		event.display = this;
		event.type = eventType;
		if (event.time == 0)
			event.time = getLastEventTime();
		if (!filterEvent(event)) {
			if (eventTable != null)
				eventTable.sendEvent(event);
		}
	}

	void setCurrentCaret(Caret caret) {
	}

	/**
	 * Causes the <code>run()</code> method of the runnable to be invoked by the
	 * user-interface thread at the next reasonable opportunity. The thread
	 * which calls this method is suspended until the runnable completes.
	 * Specifying <code>null</code> as the runnable simply wakes the
	 * user-interface thread.
	 * <p>
	 * Note that at the time the runnable is invoked, widgets that have the
	 * receiver as their display may have been disposed. Therefore, it is
	 * necessary to check for this case inside the runnable before accessing the
	 * widget.
	 * </p>
	 * 
	 * @param runnable
	 *            code to run on the user-interface thread or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_FAILED_EXEC - if an exception occurred when
	 *                executing the runnable</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #asyncExec
	 */
	public void syncExec(Runnable runnable) {
	}

	public void wake() {
	}

	void wakeThread() {
	}

	/**
	 * Forces all outstanding paint requests for the display to be processed
	 * before this method returns.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Control#update()
	 */
	public void update() {
	}

}
