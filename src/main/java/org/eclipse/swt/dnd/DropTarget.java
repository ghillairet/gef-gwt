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
package org.eclipse.swt.dnd;

import java.awt.dnd.DropTargetEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * Class <code>DropTarget</code> defines the target object for a drag and drop
 * transfer.
 * 
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * <p>
 * This class identifies the <code>Control</code> over which the user must
 * position the cursor in order to drop the data being transferred. It also
 * specifies what data types can be dropped on this control and what operations
 * can be performed. You may have several DropTragets in an application but
 * there can only be a one to one mapping between a <code>Control</code> and a
 * <code>DropTarget</code>. The DropTarget can receive data from within the same
 * application or from other applications (such as text dragged from a text
 * editor like Word).
 * </p>
 * 
 * <code><pre>
 * 	int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
 * 	Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
 * 	DropTarget target = new DropTarget(label, operations);
 * 	target.setTransfer(types);
 * </code></pre>
 * 
 * <p>
 * The application is notified of data being dragged over this control and of
 * when a drop occurs by implementing the interface
 * <code>DropTargetListener</code> which uses the class
 * <code>DropTargetEvent</code>. The application can modify the type of drag
 * being performed on this Control at any stage of the drag by modifying the
 * <code>event.detail</code> field or the <code>event.currentDataType</code>
 * field. When the data is dropped, it is the responsibility of the application
 * to copy this data for its own purposes.
 * 
 * <code><pre>
 * 	target.addDropListener (new DropTargetListener() {
 * 		public void dragEnter(DropTargetEvent event) {};
 * 		public void dragOver(DropTargetEvent event) {};
 * 		public void dragLeave(DropTargetEvent event) {};
 * 		public void dragOperationChanged(DropTargetEvent event) {};
 * 		public void dropAccept(DropTargetEvent event) {}
 * 		public void drop(DropTargetEvent event) {
 * 			// A drop has occurred, copy over the data
 * 			if (event.data == null) { // no data to copy, indicate failure in event.detail
 * 				event.detail = DND.DROP_NONE;
 * 				return;
 * 			}
 * 			label.setText ((String) event.data); // data copied to label text
 * 		}
 * 	});
 * </pre></code>
 * 
 * <dl>
 * <dt><b>Styles</b></dt>
 * <dd>DND.DROP_NONE, DND.DROP_COPY, DND.DROP_MOVE, DND.DROP_LINK</dd>
 * <dt><b>Events</b></dt>
 * <dd>DND.DragEnter, DND.DragLeave, DND.DragOver, DND.DragOperationChanged,
 * DND.DropAccept, DND.Drop</dd>
 * </dl>
 * 
 * @see <a href="http://www.eclipse.org/swt/snippets/#dnd">Drag and Drop
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      DNDExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class DropTarget extends Widget {

	Control control;
	Listener controlListener;
	Transfer[] transferAgents = new Transfer[0];
	DropTargetEffect dropEffect;

	// Track application selections
	TransferData selectedDataType;
	int selectedOperation;

	// workaround - There is no event for "operation changed" so track operation
	// based on key state
	int keyOperation = -1;

	// workaround - Simulate events when the mouse is not moving
	long dragOverStart;
	Runnable dragOverHeartbeat;
	DNDEvent dragOverEvent;

	int drag_motion_handler;
	int drag_leave_handler;
	int drag_data_received_handler;
	int drag_drop_handler;

	static final String DEFAULT_DROP_TARGET_EFFECT = "DEFAULT_DROP_TARGET_EFFECT"; //$NON-NLS-1$
	static final String IS_ACTIVE = "org.eclipse.swt.internal.control.isactive"; //$NON-NLS-1$
	static final int DRAGOVER_HYSTERESIS = 50;

	static Callback Drag_Motion;
	static Callback Drag_Leave;
	static Callback Drag_Data_Received;
	static Callback Drag_Drop;

	static {
		Drag_Motion = new Callback(DropTarget.class, "Drag_Motion", 5); //$NON-NLS-1$
		if (Drag_Motion.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
		Drag_Leave = new Callback(DropTarget.class, "Drag_Leave", 3); //$NON-NLS-1$
		if (Drag_Leave.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
		Drag_Data_Received = new Callback(DropTarget.class,
				"Drag_Data_Received", 7); //$NON-NLS-1$
		if (Drag_Data_Received.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
		Drag_Drop = new Callback(DropTarget.class, "Drag_Drop", 5); //$NON-NLS-1$
		if (Drag_Drop.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
	}

	/**
	 * Creates a new <code>DropTarget</code> to allow data to be dropped on the
	 * specified <code>Control</code>. Creating an instance of a DropTarget may
	 * cause system resources to be allocated depending on the platform. It is
	 * therefore mandatory that the DropTarget instance be disposed when no
	 * longer required.
	 * 
	 * @param control
	 *            the <code>Control</code> over which the user positions the
	 *            cursor to drop the data
	 * @param style
	 *            the bitwise OR'ing of allowed operations; this may be a
	 *            combination of any of DND.DROP_NONE, DND.DROP_COPY,
	 *            DND.DROP_MOVE, DND.DROP_LINK
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_CANNOT_INIT_DROP - unable to initiate drop
	 *                target; this will occur if more than one drop target is
	 *                created for a control or if the operating system will not
	 *                allow the creation of the drop target</li>
	 *                </ul>
	 * 
	 *                <p>
	 *                NOTE: ERROR_CANNOT_INIT_DROP should be an SWTException,
	 *                since it is a recoverable error, but can not be changed
	 *                due to backward compatibility.
	 *                </p>
	 * 
	 * @see Widget#dispose
	 * @see DropTarget#checkSubclass
	 * @see DND#DROP_NONE
	 * @see DND#DROP_COPY
	 * @see DND#DROP_MOVE
	 * @see DND#DROP_LINK
	 */
	public DropTarget(Control control, int style) {
		super(control, checkStyle(style));
	}

	static int checkStyle(int style) {
		if (style == SWT.NONE)
			return DND.DROP_MOVE;
		return style;
	}

	static long /* int */Drag_Data_Received(long /* int */widget,
			long /* int */context, long /* int */x, long /* int */y,
			long /* int */data, long /* int */info, long /* int */time) {
		DropTarget target = FindDropTarget(widget);
		if (target == null)
			return 0;
		target.drag_data_received(widget, context, (int) /* 64 */x,
				(int) /* 64 */y, data, (int) /* 64 */info, (int) /* 64 */time);
		return 0;
	}

	static long /* int */Drag_Drop(long /* int */widget, long /* int */context,
			long /* int */x, long /* int */y, long /* int */time) {
		DropTarget target = FindDropTarget(widget);
		if (target == null)
			return 0;
		return target.drag_drop(widget, context, (int) /* 64 */x,
				(int) /* 64 */y, (int) /* 64 */time) ? 1 : 0;
	}

	static long /* int */Drag_Leave(long /* int */widget, long /* int */context,
			long /* int */time) {
		DropTarget target = FindDropTarget(widget);
		if (target == null)
			return 0;
		target.drag_leave(widget, context, (int) /* 64 */time);
		return 0;
	}

	static long /* int */Drag_Motion(long /* int */widget, long /* int */context,
			long /* int */x, long /* int */y, long /* int */time) {
		DropTarget target = FindDropTarget(widget);
		if (target == null)
			return 0;
		return target.drag_motion(widget, context, (int) /* 64 */x,
				(int) /* 64 */y, (int) /* 64 */time) ? 1 : 0;
	}

	static DropTarget FindDropTarget(long /* int */handle) {
		return null;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when a drag and drop operation is in progress, by sending it one of the
	 * messages defined in the <code>DropTargetListener</code> interface.
	 * 
	 * <p>
	 * <ul>
	 * <li><code>dragEnter</code> is called when the cursor has entered the drop
	 * target boundaries
	 * <li><code>dragLeave</code> is called when the cursor has left the drop
	 * target boundaries and just before the drop occurs or is cancelled.
	 * <li><code>dragOperationChanged</code> is called when the operation being
	 * performed has changed (usually due to the user changing the selected
	 * modifier key(s) while dragging)
	 * <li><code>dragOver</code> is called when the cursor is moving over the
	 * drop target
	 * <li><code>dropAccept</code> is called just before the drop is performed.
	 * The drop target is given the chance to change the nature of the drop or
	 * veto the drop by setting the <code>event.detail</code> field
	 * <li><code>drop</code> is called when the data is being dropped
	 * </ul>
	 * </p>
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
	 * @see DropTargetListener
	 * @see #getDropListeners
	 * @see #removeDropListener
	 * @see DropTargetEvent
	 */
	public void addDropListener(DropTargetListener listener) {
		if (listener == null)
			DND.error(SWT.ERROR_NULL_ARGUMENT);
		DNDListener typedListener = new DNDListener(listener);
		typedListener.dndWidget = this;
		addListener(DND.DragEnter, typedListener);
		addListener(DND.DragLeave, typedListener);
		addListener(DND.DragOver, typedListener);
		addListener(DND.DragOperationChanged, typedListener);
		addListener(DND.Drop, typedListener);
		addListener(DND.DropAccept, typedListener);
	}

	protected void checkSubclass() {
		String name = getClass().getName();
		String validName = DropTarget.class.getName();
		if (!validName.equals(name)) {
			DND.error(SWT.ERROR_INVALID_SUBCLASS);
		}
	}

	void drag_data_received(long /* int */widget, long /* int */context, int x,
			int y, long /* int */data, int info, int time) {
	}

	boolean drag_drop(long /* int */widget, long /* int */context, int x, int y,
			int time) {
		return true;
	}

	void drag_leave(long /* int */widget, long /* int */context, int time) {
		updateDragOverHover(0, null);

		if (keyOperation == -1)
			return;
		keyOperation = -1;

		DNDEvent event = new DNDEvent();
		event.widget = this;
		event.time = time;
		event.detail = DND.DROP_NONE;
		notifyListeners(DND.DragLeave, event);
	}

	boolean drag_motion(long /* int */widget, long /* int */context, int x, int y,
			int time) {
		return true;
	}

	/**
	 * Returns the Control which is registered for this DropTarget. This is the
	 * control over which the user positions the cursor to drop the data.
	 * 
	 * @return the Control which is registered for this DropTarget
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Returns an array of listeners who will be notified when a drag and drop
	 * operation is in progress, by sending it one of the messages defined in
	 * the <code>DropTargetListener</code> interface.
	 * 
	 * @return the listeners who will be notified when a drag and drop operation
	 *         is in progress
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see DropTargetListener
	 * @see #addDropListener
	 * @see #removeDropListener
	 * @see DropTargetEvent
	 * 
	 * @since 3.4
	 */
	public DropTargetListener[] getDropListeners() {
		Listener[] listeners = getListeners(DND.DragEnter);
		int length = listeners.length;
		DropTargetListener[] dropListeners = new DropTargetListener[length];
		int count = 0;
		for (int i = 0; i < length; i++) {
			Listener listener = listeners[i];
			if (listener instanceof DNDListener) {
				dropListeners[count] = (DropTargetListener) ((DNDListener) listener)
						.getEventListener();
				count++;
			}
		}
		if (count == length)
			return dropListeners;
		DropTargetListener[] result = new DropTargetListener[count];
		System.arraycopy(dropListeners, 0, result, 0, count);
		return result;
	}

	/**
	 * Returns the drop effect for this DropTarget. This drop effect will be
	 * used during a drag and drop to display the drag under effect on the
	 * target widget.
	 * 
	 * @return the drop effect that is registered for this DropTarget
	 * 
	 * @since 3.3
	 */
	public DropTargetEffect getDropTargetEffect() {
		return dropEffect;
	}

	int getOperationFromKeyState() {
		return DND.DROP_DEFAULT;
	}

	/**
	 * Returns a list of the data types that can be transferred to this
	 * DropTarget.
	 * 
	 * @return a list of the data types that can be transferred to this
	 *         DropTarget
	 */
	public Transfer[] getTransfer() {
		return transferAgents;
	}

	void onDispose() {
	}

	int opToOsOp(int operation) {
		int osOperation = 0;
		return osOperation;
	}

	int osOpToOp(int osOperation) {
		int operation = DND.DROP_NONE;
		return operation;
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when a drag and drop operation is in progress.
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
	 * @see DropTargetListener
	 * @see #addDropListener
	 * @see #getDropListeners
	 */
	public void removeDropListener(DropTargetListener listener) {
		if (listener == null)
			DND.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(DND.DragEnter, listener);
		removeListener(DND.DragLeave, listener);
		removeListener(DND.DragOver, listener);
		removeListener(DND.DragOperationChanged, listener);
		removeListener(DND.Drop, listener);
		removeListener(DND.DropAccept, listener);
	}

	/**
	 * Specifies the data types that can be transferred to this DropTarget. If
	 * data is being dragged that does not match one of these types, the drop
	 * target will be notified of the drag and drop operation but the
	 * currentDataType will be null and the operation will be DND.NONE.
	 * 
	 * @param transferAgents
	 *            a list of Transfer objects which define the types of data that
	 *            can be dropped on this target
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if transferAgents is null</li>
	 *                </ul>
	 */
	public void setTransfer(Transfer[] transferAgents) {
	}

	/**
	 * Specifies the drop effect for this DropTarget. This drop effect will be
	 * used during a drag and drop to display the drag under effect on the
	 * target widget.
	 * 
	 * @param effect
	 *            the drop effect that is registered for this DropTarget
	 * 
	 * @since 3.3
	 */
	public void setDropTargetEffect(DropTargetEffect effect) {
		dropEffect = effect;
	}

	boolean setEventData(long /* int */context, int x, int y, int time,
			DNDEvent event) {
		return true;
	}

	void updateDragOverHover(long delay, DNDEvent event) {
		if (delay == 0) {
			dragOverStart = 0;
			dragOverEvent = null;
			return;
		}
		dragOverStart = System.currentTimeMillis() + delay;
		if (dragOverEvent == null)
			dragOverEvent = new DNDEvent();
		dragOverEvent.x = event.x;
		dragOverEvent.y = event.y;
		TransferData[] dataTypes = new TransferData[event.dataTypes.length];
		System.arraycopy(event.dataTypes, 0, dataTypes, 0, dataTypes.length);
		dragOverEvent.dataTypes = dataTypes;
		dragOverEvent.operations = event.operations;
		dragOverEvent.time = event.time;
	}

}
