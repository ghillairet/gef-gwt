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
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;

/**
 * Instances of this class represent the "windows" which the desktop or
 * "window manager" is managing. Instances that do not have a parent (that is,
 * they are built using the constructor, which takes a <code>Display</code> as
 * the argument) are described as <em>top level</em> shells. Instances that do
 * have a parent are described as <em>secondary</em> or <em>dialog</em> shells.
 * <p>
 * Instances are always displayed in one of the maximized, minimized or normal
 * states:
 * <ul>
 * <li>
 * When an instance is marked as <em>maximized</em>, the window manager will
 * typically resize it to fill the entire visible area of the display, and the
 * instance is usually put in a state where it can not be resized (even if it
 * has style <code>RESIZE</code>) until it is no longer maximized.</li>
 * <li>
 * When an instance is in the <em>normal</em> state (neither maximized or
 * minimized), its appearance is controlled by the style constants which were
 * specified when it was created and the restrictions of the window manager (see
 * below).</li>
 * <li>
 * When an instance has been marked as <em>minimized</em>, its contents (client
 * area) will usually not be visible, and depending on the window manager, it
 * may be "iconified" (that is, replaced on the desktop by a small simplified
 * representation of itself), relocated to a distinguished area of the screen,
 * or hidden. Combinations of these changes are also possible.</li>
 * </ul>
 * </p>
 * <p>
 * The <em>modality</em> of an instance may be specified using style bits. The
 * modality style bits are used to determine whether input is blocked for other
 * shells on the display. The <code>PRIMARY_MODAL</code> style allows an
 * instance to block input to its parent. The <code>APPLICATION_MODAL</code>
 * style allows an instance to block input to every other shell in the display.
 * The <code>SYSTEM_MODAL</code> style allows an instance to block input to all
 * shells, including shells belonging to different applications.
 * </p>
 * <p>
 * Note: The styles supported by this class are treated as <em>HINT</em>s, since
 * the window manager for the desktop on which the instance is visible has
 * ultimate control over the appearance and behavior of decorations and
 * modality. For example, some window managers only support resizable windows
 * and will always assume the RESIZE style, even if it is not set. In addition,
 * if a modality style is not supported, it is "upgraded" to a more restrictive
 * modality style that is supported. For example, if <code>PRIMARY_MODAL</code>
 * is not supported, it would be upgraded to <code>APPLICATION_MODAL</code>. A
 * modality style may also be "downgraded" to a less restrictive style. For
 * example, most operating systems no longer support <code>SYSTEM_MODAL</code>
 * because it can freeze up the desktop, so this is typically downgraded to
 * <code>APPLICATION_MODAL</code>.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER, CLOSE, MIN, MAX, NO_TRIM, RESIZE, TITLE, ON_TOP, TOOL, SHEET</dd>
 * <dd>APPLICATION_MODAL, MODELESS, PRIMARY_MODAL, SYSTEM_MODAL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Activate, Close, Deactivate, Deiconify, Iconify</dd>
 * </dl>
 * Class <code>SWT</code> provides two "convenience constants" for the most
 * commonly required style combinations:
 * <dl>
 * <dt><code>SHELL_TRIM</code></dt>
 * <dd>
 * the result of combining the constants which are required to produce a typical
 * application top level shell: (that is,
 * <code>CLOSE | TITLE | MIN | MAX | RESIZE</code>)</dd>
 * <dt><code>DIALOG_TRIM</code></dt>
 * <dd>
 * the result of combining the constants which are required to produce a typical
 * application dialog shell: (that is, <code>TITLE | CLOSE | BORDER</code>)</dd>
 * </dl>
 * </p>
 * <p>
 * Note: Only one of the styles APPLICATION_MODAL, MODELESS, PRIMARY_MODAL and
 * SYSTEM_MODAL may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see Decorations
 * @see SWT
 * @see <a href="http://www.eclipse.org/swt/snippets/#shell">Shell snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Shell extends Decorations {
	long /* int */shellHandle, tooltipsHandle, tooltipWindow, group, modalGroup;
	boolean mapped, moved, resized, opened, fullScreen, showWithParent,
			modified, center;
	int oldX, oldY, oldWidth, oldHeight;
	int minWidth, minHeight;
	Control lastActive;
	ToolTip[] toolTips;

	static final int MAXIMUM_TRIM = 128;
	static final int BORDER = 3;

	/**
	 * Constructs a new instance of this class. This is equivalent to calling
	 * <code>Shell((Display) null)</code>.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 */
	public Shell() {
		this((Display) null);
	}

	/**
	 * Constructs a new instance of this class given only the style value
	 * describing its behavior and appearance. This is equivalent to calling
	 * <code>Shell((Display) null, style)</code>.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must
	 * be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * 
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
	 * @see SWT#BORDER
	 * @see SWT#CLOSE
	 * @see SWT#MIN
	 * @see SWT#MAX
	 * @see SWT#RESIZE
	 * @see SWT#TITLE
	 * @see SWT#TOOL
	 * @see SWT#NO_TRIM
	 * @see SWT#SHELL_TRIM
	 * @see SWT#DIALOG_TRIM
	 * @see SWT#ON_TOP
	 * @see SWT#MODELESS
	 * @see SWT#PRIMARY_MODAL
	 * @see SWT#APPLICATION_MODAL
	 * @see SWT#SYSTEM_MODAL
	 * @see SWT#SHEET
	 */
	public Shell(int style) {
		this((Display) null, style);
	}

	/**
	 * Constructs a new instance of this class given only the display to create
	 * it on. It is created with style <code>SWT.SHELL_TRIM</code>.
	 * <p>
	 * Note: Currently, null can be passed in for the display argument. This has
	 * the effect of creating the shell on the currently active display if there
	 * is one. If there is no current display, the shell is created on a
	 * "default" display. <b>Passing in null as the display argument is not
	 * considered to be good coding style, and may not be supported in a future
	 * release of SWT.</b>
	 * </p>
	 * 
	 * @param display
	 *            the display to create the shell on
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 */
	public Shell(Display display) {
		this(display, SWT.SHELL_TRIM);
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
	 * the effect of creating the shell on the currently active display if there
	 * is one. If there is no current display, the shell is created on a
	 * "default" display. <b>Passing in null as the display argument is not
	 * considered to be good coding style, and may not be supported in a future
	 * release of SWT.</b>
	 * </p>
	 * 
	 * @param display
	 *            the display to create the shell on
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
	 * @see SWT#BORDER
	 * @see SWT#CLOSE
	 * @see SWT#MIN
	 * @see SWT#MAX
	 * @see SWT#RESIZE
	 * @see SWT#TITLE
	 * @see SWT#TOOL
	 * @see SWT#NO_TRIM
	 * @see SWT#SHELL_TRIM
	 * @see SWT#DIALOG_TRIM
	 * @see SWT#ON_TOP
	 * @see SWT#MODELESS
	 * @see SWT#PRIMARY_MODAL
	 * @see SWT#APPLICATION_MODAL
	 * @see SWT#SYSTEM_MODAL
	 * @see SWT#SHEET
	 */
	public Shell(Display display, int style) {
		this(display, null, style, 0, false);
	}

	Shell(Display display, Shell parent, int style, long /* int */handle,
			boolean embedded) {
	}

	/**
	 * Constructs a new instance of this class given only its parent. It is
	 * created with style <code>SWT.DIALOG_TRIM</code>.
	 * <p>
	 * Note: Currently, null can be passed in for the parent. This has the
	 * effect of creating the shell on the currently active display if there is
	 * one. If there is no current display, the shell is created on a "default"
	 * display. <b>Passing in null as the parent is not considered to be good
	 * coding style, and may not be supported in a future release of SWT.</b>
	 * </p>
	 * 
	 * @param parent
	 *            a shell which will be the parent of the new instance
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parent is disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 */
	public Shell(Shell parent) {
		this(parent, SWT.DIALOG_TRIM);
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
	 * <p>
	 * Note: Currently, null can be passed in for the parent. This has the
	 * effect of creating the shell on the currently active display if there is
	 * one. If there is no current display, the shell is created on a "default"
	 * display. <b>Passing in null as the parent is not considered to be good
	 * coding style, and may not be supported in a future release of SWT.</b>
	 * </p>
	 * 
	 * @param parent
	 *            a shell which will be the parent of the new instance
	 * @param style
	 *            the style of control to construct
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
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
	 * @see SWT#BORDER
	 * @see SWT#CLOSE
	 * @see SWT#MIN
	 * @see SWT#MAX
	 * @see SWT#RESIZE
	 * @see SWT#TITLE
	 * @see SWT#NO_TRIM
	 * @see SWT#SHELL_TRIM
	 * @see SWT#DIALOG_TRIM
	 * @see SWT#ON_TOP
	 * @see SWT#TOOL
	 * @see SWT#MODELESS
	 * @see SWT#PRIMARY_MODAL
	 * @see SWT#APPLICATION_MODAL
	 * @see SWT#SYSTEM_MODAL
	 * @see SWT#SHEET
	 */
	public Shell(Shell parent, int style) {
		this(parent != null ? parent.display : null, parent, style, 0, false);
	}

	public static Shell gtk_new(Display display, long /* int */handle) {
		return new Shell(display, null, SWT.NO_TRIM, handle, true);
	}

	/**
	 * Invokes platform specific functionality to allocate a new shell that is
	 * not embedded.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Shell</code>. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms, and should never be called from application code.
	 * </p>
	 * 
	 * @param display
	 *            the display for the shell
	 * @param handle
	 *            the handle for the shell
	 * @return a new shell object containing the specified display and handle
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 * 
	 * @since 3.3
	 */
	public static Shell internal_new(Display display, long /* int */handle) {
		return new Shell(display, null, SWT.NO_TRIM, handle, false);
	}

	static int checkStyle(Shell parent, int style) {
		style = Decorations.checkStyle(style);
		style &= ~SWT.TRANSPARENT;
		if ((style & SWT.ON_TOP) != 0)
			style &= ~(SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX);
		int mask = SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL | SWT.PRIMARY_MODAL;
		if ((style & SWT.SHEET) != 0) {
			style &= ~SWT.SHEET;
			style |= parent == null ? SWT.SHELL_TRIM : SWT.DIALOG_TRIM;
			if ((style & mask) == 0) {
				style |= parent == null ? SWT.APPLICATION_MODAL
						: SWT.PRIMARY_MODAL;
			}
		}
		int bits = style & ~mask;
		if ((style & SWT.SYSTEM_MODAL) != 0)
			return bits | SWT.SYSTEM_MODAL;
		if ((style & SWT.APPLICATION_MODAL) != 0)
			return bits | SWT.APPLICATION_MODAL;
		if ((style & SWT.PRIMARY_MODAL) != 0)
			return bits | SWT.PRIMARY_MODAL;
		return bits;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when operations are performed on the receiver, by sending the listener
	 * one of the messages defined in the <code>ShellListener</code> interface.
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
	 * @see ShellListener
	 * @see #removeShellListener
	 */
	public void addShellListener(ShellListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Close, typedListener);
		addListener(SWT.Iconify, typedListener);
		addListener(SWT.Deiconify, typedListener);
		addListener(SWT.Activate, typedListener);
		addListener(SWT.Deactivate, typedListener);
	}

	void addToolTip(ToolTip toolTip) {
		if (toolTips == null)
			toolTips = new ToolTip[4];
		for (int i = 0; i < toolTips.length; i++) {
			if (toolTips[i] == null) {
				toolTips[i] = toolTip;
				return;
			}
		}
		ToolTip[] newToolTips = new ToolTip[toolTips.length + 4];
		newToolTips[toolTips.length] = toolTip;
		System.arraycopy(toolTips, 0, newToolTips, 0, toolTips.length);
		toolTips = newToolTips;
	}

	void bringToTop(boolean force) {
	}

	void checkOpen() {
		if (!opened)
			resized = false;
	}

	/**
	 * Requests that the window manager close the receiver in the same way it
	 * would be closed when the user clicks on the "close box" or performs some
	 * other platform specific key or mouse combination that indicates the
	 * window should be removed.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SWT#Close
	 * @see #dispose
	 */
	public void close() {
		checkWidget();
		closeWidget();
	}

	void closeWidget() {
		Event event = new Event();
		sendEvent(SWT.Close, event);
		if (event.doit && !isDisposed())
			dispose();
	}

	public Rectangle computeTrim(int x, int y, int width, int height) {
		return null;
	}

	void createHandle(int index) {
	}

	Control findBackgroundControl() {
		return (state & BACKGROUND) != 0 || backgroundImage != null ? this
				: null;
	}

	Composite findDeferredControl() {
		return layoutCount > 0 ? this : null;
	}

	/**
	 * Returns a ToolBar object representing the tool bar that can be shown in
	 * the receiver's trim. This will return <code>null</code> if the platform
	 * does not support tool bars that are not part of the content area of the
	 * shell, or if the Shell's style does not support having a tool bar.
	 * <p>
	 * 
	 * @return a ToolBar object representing the Shell's tool bar, or
	 *         <ocde>null</code>.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li> <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                called from the thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.7
	 */
	public ToolBar getToolBar() {
		checkWidget();
		return null;
	}

	void hookEvents() {
	}

	public boolean isEnabled() {
		checkWidget();
		return getEnabled();
	}

	public boolean isVisible() {
		checkWidget();
		return getVisible();
	}

	void register() {
		super.register();
		display.addWidget(shellHandle, this);
	}

	void releaseParent() {
		/* Do nothing */
	}

	/**
	 * Returns the receiver's alpha value. The alpha value is between 0
	 * (transparent) and 255 (opaque).
	 * 
	 * @return the alpha value
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
	public int getAlpha() {
		return 0;
	}

	/**
	 * Returns <code>true</code> if the receiver is currently in fullscreen
	 * state, and false otherwise.
	 * <p>
	 * 
	 * @return the fullscreen state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li> <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                called from the thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.4
	 */
	public boolean getFullScreen() {
		checkWidget();
		return fullScreen;
	}

	public Point getLocation() {
		return null;
	}

	public boolean getMaximized() {
		checkWidget();
		return !fullScreen && super.getMaximized();
	}

	/**
	 * Returns a point describing the minimum receiver's size. The x coordinate
	 * of the result is the minimum width of the receiver. The y coordinate of
	 * the result is the minimum height of the receiver.
	 * 
	 * @return the receiver's size
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public Point getMinimumSize() {
		checkWidget();
		int width = Math.max(1, minWidth + trimWidth());
		int height = Math.max(1, minHeight + trimHeight());
		return new Point(width, height);
	}

	Shell getModalShell() {
		Shell shell = null;
		Shell[] modalShells = display.modalShells;
		if (modalShells != null) {
			int bits = SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;
			int index = modalShells.length;
			while (--index >= 0) {
				Shell modal = modalShells[index];
				if (modal != null) {
					if ((modal.style & bits) != 0) {
						Control control = this;
						while (control != null) {
							if (control == modal)
								break;
							control = control.parent;
						}
						if (control != modal)
							return modal;
						break;
					}
					if ((modal.style & SWT.PRIMARY_MODAL) != 0) {
						if (shell == null)
							shell = getShell();
						if (modal.parent == shell)
							return modal;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the receiver's modified state.
	 * 
	 * @return <code>true</code> if the receiver is marked as modified, or
	 *         <code>false</code> otherwise
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.5
	 */
	public boolean getModified() {
		checkWidget();
		return modified;
	}

	public Point getSize() {
		return null;
	}

	public boolean getVisible() {
		return false;
	}

	/**
	 * Returns the region that defines the shape of the shell, or
	 * <code>null</code> if the shell has the default shape.
	 * 
	 * @return the region that defines the shape of the shell, or
	 *         <code>null</code>
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
	 * 
	 */
	public Region getRegion() {
		/* This method is needed for @since 3.0 Javadoc */
		checkWidget();
		return region;
	}

	/**
	 * Returns the receiver's input method editor mode. This will be the result
	 * of bitwise OR'ing together one or more of the following constants defined
	 * in class <code>SWT</code>: <code>NONE</code>, <code>ROMAN</code>,
	 * <code>DBCS</code>, <code>PHONETIC</code>, <code>NATIVE</code>,
	 * <code>ALPHA</code>.
	 * 
	 * @return the IME mode
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
	public int getImeInputMode() {
		checkWidget();
		return SWT.NONE;
	}

	Shell _getShell() {
		return this;
	}

	/**
	 * Returns an array containing all shells which are descendants of the
	 * receiver.
	 * <p>
	 * 
	 * @return the dialog shells
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li> <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Shell[] getShells() {
		checkWidget();
		int count = 0;
		Shell[] shells = display.getShells();
		for (int i = 0; i < shells.length; i++) {
			Control shell = shells[i];
			do {
				shell = shell.getParent();
			} while (shell != null && shell != this);
			if (shell == this)
				count++;
		}
		int index = 0;
		Shell[] result = new Shell[count];
		for (int i = 0; i < shells.length; i++) {
			Control shell = shells[i];
			do {
				shell = shell.getParent();
			} while (shell != null && shell != this);
			if (shell == this) {
				result[index++] = shells[i];
			}
		}
		return result;
	}

	/**
	 * Moves the receiver to the top of the drawing order for the display on
	 * which it was created (so that all other shells on that display, which are
	 * not the receiver's children will be drawn behind it), marks it visible,
	 * sets the focus and asks the window manager to make the shell active.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see Control#moveAbove
	 * @see Control#setFocus
	 * @see Control#setVisible
	 * @see Display#getActiveShell
	 * @see Decorations#setDefaultButton(Button)
	 * @see Shell#setActive
	 * @see Shell#forceActive
	 */
	public void open() {
		checkWidget();
		bringToTop(false);
		setVisible(true);
		if (isDisposed())
			return;
		if (!restoreFocus() && !traverseGroup(true))
			setFocus();
	}

	public boolean print(GC gc) {
		checkWidget();
		if (gc == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (gc.isDisposed())
			error(SWT.ERROR_INVALID_ARGUMENT);
		return false;
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when operations are performed on the receiver.
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
	 * @see ShellListener
	 * @see #addShellListener
	 */
	public void removeShellListener(ShellListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.Close, listener);
		eventTable.unhook(SWT.Iconify, listener);
		eventTable.unhook(SWT.Deiconify, listener);
		eventTable.unhook(SWT.Activate, listener);
		eventTable.unhook(SWT.Deactivate, listener);
	}

	void reskinChildren(int flags) {
		Shell[] shells = getShells();
		for (int i = 0; i < shells.length; i++) {
			Shell shell = shells[i];
			if (shell != null)
				shell.reskin(flags);
		}
		if (toolTips != null) {
			for (int i = 0; i < toolTips.length; i++) {
				ToolTip toolTip = toolTips[i];
				if (toolTip != null)
					toolTip.reskin(flags);
			}
		}
		super.reskinChildren(flags);
	}

	/**
	 * If the receiver is visible, moves it to the top of the drawing order for
	 * the display on which it was created (so that all other shells on that
	 * display, which are not the receiver's children will be drawn behind it)
	 * and asks the window manager to make the shell active
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
	 * @see Control#moveAbove
	 * @see Control#setFocus
	 * @see Control#setVisible
	 * @see Display#getActiveShell
	 * @see Decorations#setDefaultButton(Button)
	 * @see Shell#open
	 * @see Shell#setActive
	 */
	public void setActive() {
		checkWidget();
		bringToTop(false);
	}

	/**
	 * Sets the receiver's alpha value which must be between 0 (transparent) and
	 * 255 (opaque).
	 * <p>
	 * This operation requires the operating system's advanced widgets subsystem
	 * which may not be available on some platforms.
	 * </p>
	 * 
	 * @param alpha
	 *            the alpha value
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
	public void setAlpha(int alpha) {
	}

	int setBounds(int x, int y, int width, int height, boolean move,
			boolean resize) {
		return 0;
	}

	public void setEnabled(boolean enabled) {
	}

	/**
	 * Sets the full screen state of the receiver. If the argument is
	 * <code>true</code> causes the receiver to switch to the full screen state,
	 * and if the argument is <code>false</code> and the receiver was previously
	 * switched into full screen state, causes the receiver to switch back to
	 * either the maximized or normal states.
	 * <p>
	 * Note: The result of intermixing calls to <code>setFullScreen(true)</code>, <code>setMaximized(true)</code> and <code>setMinimized(true)</code>
	 * will vary by platform. Typically, the behavior will match the platform
	 * user's expectations, but not always. This should be avoided if possible.
	 * </p>
	 * 
	 * @param fullScreen
	 *            the new fullscreen state
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
	public void setFullScreen(boolean fullScreen) {
	}

	/**
	 * Sets the input method editor mode to the argument which should be the
	 * result of bitwise OR'ing together one or more of the following constants
	 * defined in class <code>SWT</code>: <code>NONE</code>, <code>ROMAN</code>,
	 * <code>DBCS</code>, <code>PHONETIC</code>, <code>NATIVE</code>,
	 * <code>ALPHA</code>.
	 * 
	 * @param mode
	 *            the new IME mode
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
	public void setImeInputMode(int mode) {
		checkWidget();
	}

	public void setMaximized(boolean maximized) {
	}

	public void setMenuBar(Menu menu) {
	}

	public void setMinimized(boolean minimized) {
	}

	/**
	 * Sets the receiver's minimum size to the size specified by the arguments.
	 * If the new minimum size is larger than the current size of the receiver,
	 * the receiver is resized to the new minimum size.
	 * 
	 * @param width
	 *            the new minimum width for the receiver
	 * @param height
	 *            the new minimum height for the receiver
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public void setMinimumSize(int width, int height) {
	}

	/**
	 * Sets the receiver's minimum size to the size specified by the argument.
	 * If the new minimum size is larger than the current size of the receiver,
	 * the receiver is resized to the new minimum size.
	 * 
	 * @param size
	 *            the new minimum size for the receiver
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
	 * 
	 * @since 3.1
	 */
	public void setMinimumSize(Point size) {
		checkWidget();
		if (size == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		setMinimumSize(size.x, size.y);
	}

	/**
	 * Sets the receiver's modified state as specified by the argument.
	 * 
	 * @param modified
	 *            the new modified state for the receiver
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
	 * @since 3.5
	 */
	public void setModified(boolean modified) {
		checkWidget();
		this.modified = modified;
	}

	/**
	 * Sets the shape of the shell to the region specified by the argument. When
	 * the argument is null, the default shape of the shell is restored. The
	 * shell must be created with the style SWT.NO_TRIM in order to specify a
	 * region.
	 * 
	 * @param region
	 *            the region that defines the shape of the shell (or null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the region has been
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
	 * 
	 */
	public void setRegion(Region region) {
		checkWidget();
		if ((style & SWT.NO_TRIM) == 0)
			return;
		super.setRegion(region);
	}

	public void setText(String string) {
	}

	public void setVisible(boolean visible) {
	}

	void setZOrder(Control sibling, boolean above, boolean fixRelations) {
		/*
		 * Bug in GTK+. Changing the toplevel window Z-order causes X to send a
		 * resize event. Before the shell is mapped, these resize events always
		 * have a size of 200x200, causing extra layout work to occur. The fix
		 * is to modify the Z-order only if the shell has already been mapped at
		 * least once.
		 */
		/* Shells are never included in labelled-by relations */
		if (mapped)
			setZOrder(sibling, above, false, false);
	}

	boolean traverseEscape() {
		if (parent == null)
			return false;
		if (!isVisible() || !isEnabled())
			return false;
		close();
		return true;
	}

	int trimHeight() {
		if ((style & SWT.NO_TRIM) != 0)
			return 0;
		if (fullScreen)
			return 0;
		boolean hasTitle = false, hasResize = false, hasBorder = false;
		hasTitle = (style & (SWT.MIN | SWT.MAX | SWT.TITLE | SWT.MENU)) != 0;
		hasResize = (style & SWT.RESIZE) != 0;
		hasBorder = (style & SWT.BORDER) != 0;
		if (hasTitle) {
			if (hasResize)
				return display.titleResizeTrimHeight;
			if (hasBorder)
				return display.titleBorderTrimHeight;
			return display.titleTrimHeight;
		}
		if (hasResize)
			return display.resizeTrimHeight;
		if (hasBorder)
			return display.borderTrimHeight;
		return 0;
	}

	int trimWidth() {
		if ((style & SWT.NO_TRIM) != 0)
			return 0;
		if (fullScreen)
			return 0;
		boolean hasTitle = false, hasResize = false, hasBorder = false;
		hasTitle = (style & (SWT.MIN | SWT.MAX | SWT.TITLE | SWT.MENU)) != 0;
		hasResize = (style & SWT.RESIZE) != 0;
		hasBorder = (style & SWT.BORDER) != 0;
		if (hasTitle) {
			if (hasResize)
				return display.titleResizeTrimWidth;
			if (hasBorder)
				return display.titleBorderTrimWidth;
			return display.titleTrimWidth;
		}
		if (hasResize)
			return display.resizeTrimWidth;
		if (hasBorder)
			return display.borderTrimWidth;
		return 0;
	}

	public void dispose() {
	}

	/**
	 * If the receiver is visible, moves it to the top of the drawing order for
	 * the display on which it was created (so that all other shells on that
	 * display, which are not the receiver's children will be drawn behind it)
	 * and forces the window manager to make the shell active.
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
	 * @see Control#moveAbove
	 * @see Control#setFocus
	 * @see Control#setVisible
	 * @see Display#getActiveShell
	 * @see Decorations#setDefaultButton(Button)
	 * @see Shell#open
	 * @see Shell#setActive
	 */
	public void forceActive() {
		checkWidget();
		bringToTop(true);
	}

	public Rectangle getBounds() {
		return null;
	}

	void releaseHandle() {
		super.releaseHandle();
		shellHandle = 0;
	}

	void releaseChildren(boolean destroy) {
		Shell[] shells = getShells();
		for (int i = 0; i < shells.length; i++) {
			Shell shell = shells[i];
			if (shell != null && !shell.isDisposed()) {
				shell.release(false);
			}
		}
		if (toolTips != null) {
			for (int i = 0; i < toolTips.length; i++) {
				ToolTip toolTip = toolTips[i];
				if (toolTip != null && !toolTip.isDisposed()) {
					toolTip.dispose();
				}
			}
			toolTips = null;
		}
		super.releaseChildren(destroy);
	}

	void releaseWidget() {
	}

	void setToolTipText(long /* int */tipWidget, String string) {
		setToolTipText(tipWidget, tipWidget, string);
	}

	void setToolTipText(long /* int */rootWidget, long /* int */tipWidget,
			String string) {

	}
}
