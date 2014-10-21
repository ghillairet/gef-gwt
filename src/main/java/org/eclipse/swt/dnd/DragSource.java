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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * <code>DragSource</code> defines the source object for a drag and drop
 * transfer.
 * 
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * <p>
 * A drag source is the object which originates a drag and drop operation. For
 * the specified widget, it defines the type of data that is available for
 * dragging and the set of operations that can be performed on that data. The
 * operations can be any bit-wise combination of DND.MOVE, DND.COPY or DND.LINK.
 * The type of data that can be transferred is specified by subclasses of
 * Transfer such as TextTransfer or FileTransfer. The type of data transferred
 * can be a predefined system type or it can be a type defined by the
 * application. For instructions on how to define your own transfer type, refer
 * to <code>ByteArrayTransfer</code>.
 * </p>
 * 
 * <p>
 * You may have several DragSources in an application but you can only have one
 * DragSource per Control. Data dragged from this DragSource can be dropped on a
 * site within this application or it can be dropped on another application such
 * as an external Text editor.
 * </p>
 * 
 * <p>
 * The application supplies the content of the data being transferred by
 * implementing the <code>DragSourceListener</code> and associating it with the
 * DragSource via DragSource#addDragListener.
 * </p>
 * 
 * <p>
 * When a successful move operation occurs, the application is required to take
 * the appropriate action to remove the data from its display and remove any
 * associated operating system resources or internal references. Typically in a
 * move operation, the drop target makes a copy of the data and the drag source
 * deletes the original. However, sometimes copying the data can take a long
 * time (such as copying a large file). Therefore, on some platforms, the drop
 * target may actually move the data in the operating system rather than make a
 * copy. This is usually only done in file transfers. In this case, the drag
 * source is informed in the DragEnd event that a DROP_TARGET_MOVE was
 * performed. It is the responsibility of the drag source at this point to clean
 * up its displayed information. No action needs to be taken on the operating
 * system resources.
 * </p>
 * 
 * <p>
 * The following example shows a Label widget that allows text to be dragged
 * from it.
 * </p>
 * 
 * <code><pre>
 * 	// Enable a label as a Drag Source
 * 	Label label = new Label(shell, SWT.NONE);
 * 	// This example will allow text to be dragged
 * 	Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
 * 	// This example will allow the text to be copied or moved to the drop target
 * 	int operations = DND.DROP_MOVE | DND.DROP_COPY;
 * 	
 * 	DragSource source = new DragSource(label, operations);
 * 	source.setTransfer(types);
 * 	source.addDragListener(new DragSourceListener() {
 * 		public void dragStart(DragSourceEvent e) {
 * 			// Only start the drag if there is actually text in the
 * 			// label - this text will be what is dropped on the target.
 * 			if (label.getText().length() == 0) {
 * 				event.doit = false;
 * 			}
 * 		};
 * 		public void dragSetData(DragSourceEvent event) {
 * 			// A drop has been performed, so provide the data of the 
 * 			// requested type.
 * 			// (Checking the type of the requested data is only 
 * 			// necessary if the drag source supports more than 
 * 			// one data type but is shown here as an example).
 * 			if (TextTransfer.getInstance().isSupportedType(event.dataType)){
 * 				event.data = label.getText();
 * 			}
 * 		}
 * 		public void dragFinished(DragSourceEvent event) {
 * 			// A Move operation has been performed so remove the data
 * 			// from the source
 * 			if (event.detail == DND.DROP_MOVE)
 * 				label.setText("");
 * 		}
 * 	});
 * </pre></code>
 * 
 * 
 * <dl>
 * <dt><b>Styles</b></dt>
 * <dd>DND.DROP_NONE, DND.DROP_COPY, DND.DROP_MOVE, DND.DROP_LINK</dd>
 * <dt><b>Events</b></dt>
 * <dd>DND.DragStart, DND.DragSetData, DND.DragEnd</dd>
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
public class DragSource extends Widget {

	// info for registering as a drag source
	Control control;
	Listener controlListener;
	Transfer[] transferAgents = new Transfer[0];
	DragSourceEffect dragEffect;

	long /* int */targetList;

	// workaround - remember action performed for DragEnd
	boolean moveData = false;

	static final String DEFAULT_DRAG_SOURCE_EFFECT = "DEFAULT_DRAG_SOURCE_EFFECT"; //$NON-NLS-1$

	static Callback DragGetData;
	static Callback DragEnd;
	static Callback DragDataDelete;
	static {
		DragGetData = new Callback(DragSource.class, "DragGetData", 5); //$NON-NLS-1$
		if (DragGetData.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
		DragEnd = new Callback(DragSource.class, "DragEnd", 2); //$NON-NLS-1$
		if (DragEnd.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
		DragDataDelete = new Callback(DragSource.class, "DragDataDelete", 2); //$NON-NLS-1$
		if (DragDataDelete.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
	}

	/**
	 * Creates a new <code>DragSource</code> to handle dragging from the
	 * specified <code>Control</code>. Creating an instance of a DragSource may
	 * cause system resources to be allocated depending on the platform. It is
	 * therefore mandatory that the DragSource instance be disposed when no
	 * longer required.
	 * 
	 * @param control
	 *            the <code>Control</code> that the user clicks on to initiate
	 *            the drag
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
	 *                <li>ERROR_CANNOT_INIT_DRAG - unable to initiate drag
	 *                source; this will occur if more than one drag source is
	 *                created for a control or if the operating system will not
	 *                allow the creation of the drag source</li>
	 *                </ul>
	 * 
	 *                <p>
	 *                NOTE: ERROR_CANNOT_INIT_DRAG should be an SWTException,
	 *                since it is a recoverable error, but can not be changed
	 *                due to backward compatibility.
	 *                </p>
	 * 
	 * @see Widget#dispose
	 * @see DragSource#checkSubclass
	 * @see DND#DROP_NONE
	 * @see DND#DROP_COPY
	 * @see DND#DROP_MOVE
	 * @see DND#DROP_LINK
	 */
	public DragSource(Control control, int style) {
		super(control, checkStyle(style));
	}

	static int checkStyle(int style) {
		// if (style == SWT.NONE)
		// return DND.DROP_MOVE;
		return style;
	}

	static long /* int */DragDataDelete(long /* int */widget,
			long /* int */context) {
		DragSource source = FindDragSource(widget);
		if (source == null)
			return 0;
		source.dragDataDelete(widget, context);
		return 0;
	}

	static long /* int */DragEnd(long /* int */widget, long /* int */context) {
		DragSource source = FindDragSource(widget);
		if (source == null)
			return 0;
		source.dragEnd(widget, context);
		return 0;
	}

	static long /* int */DragGetData(long /* int */widget, long /* int */context,
			long /* int */selection_data, long /* int */info, long /* int */time) {
		DragSource source = FindDragSource(widget);
		if (source == null)
			return 0;
		source.dragGetData(widget, context, selection_data, (int) /* 64 */info,
				(int) /* 64 */time);
		return 0;
	}

	static DragSource FindDragSource(long /* int */handle) {
		return null;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when a drag and drop operation is in progress, by sending it one of the
	 * messages defined in the <code>DragSourceListener</code> interface.
	 * 
	 * <p>
	 * <ul>
	 * <li><code>dragStart</code> is called when the user has begun the actions
	 * required to drag the widget. This event gives the application the chance
	 * to decide if a drag should be started.
	 * <li><code>dragSetData</code> is called when the data is required from the
	 * drag source.
	 * <li><code>dragFinished</code> is called when the drop has successfully
	 * completed (mouse up over a valid target) or has been terminated (such as
	 * hitting the ESC key). Perform cleanup such as removing data from the
	 * source side on a successful move operation.
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
	 * @see DragSourceListener
	 * @see #getDragListeners
	 * @see #removeDragListener
	 * @see DragSourceEvent
	 */
	public void addDragListener(DragSourceListener listener) {
		if (listener == null)
			DND.error(SWT.ERROR_NULL_ARGUMENT);
		DNDListener typedListener = new DNDListener(listener);
		typedListener.dndWidget = this;
		addListener(DND.DragStart, typedListener);
		addListener(DND.DragSetData, typedListener);
		addListener(DND.DragEnd, typedListener);
	}

	protected void checkSubclass() {
		String name = getClass().getName();
		String validName = DragSource.class.getName();
		if (!validName.equals(name)) {
			DND.error(SWT.ERROR_INVALID_SUBCLASS);
		}
	}

	void drag(Event dragEvent) {
	}

	void dragEnd(long /* int */widget, long /* int */context) {
	}

	void dragGetData(long /* int */widget, long /* int */context,
			long /* int */selection_data, int info, int time) {
	}

	void dragDataDelete(long /* int */widget, long /* int */context) {
		moveData = true;
	}

	/**
	 * Returns the Control which is registered for this DragSource. This is the
	 * control that the user clicks in to initiate dragging.
	 * 
	 * @return the Control which is registered for this DragSource
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Returns an array of listeners who will be notified when a drag and drop
	 * operation is in progress, by sending it one of the messages defined in
	 * the <code>DragSourceListener</code> interface.
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
	 * @see DragSourceListener
	 * @see #addDragListener
	 * @see #removeDragListener
	 * @see DragSourceEvent
	 * 
	 * @since 3.4
	 */
	public DragSourceListener[] getDragListeners() {
		Listener[] listeners = getListeners(DND.DragStart);
		int length = listeners.length;
		DragSourceListener[] dragListeners = new DragSourceListener[length];
		int count = 0;
		for (int i = 0; i < length; i++) {
			Listener listener = listeners[i];
			if (listener instanceof DNDListener) {
				dragListeners[count] = (DragSourceListener) ((DNDListener) listener)
						.getEventListener();
				count++;
			}
		}
		if (count == length)
			return dragListeners;
		DragSourceListener[] result = new DragSourceListener[count];
		System.arraycopy(dragListeners, 0, result, 0, count);
		return result;
	}

	/**
	 * Returns the drag effect that is registered for this DragSource. This drag
	 * effect will be used during a drag and drop operation.
	 * 
	 * @return the drag effect that is registered for this DragSource
	 * 
	 * @since 3.3
	 */
	public DragSourceEffect getDragSourceEffect() {
		return dragEffect;
	}

	/**
	 * Returns the list of data types that can be transferred by this
	 * DragSource.
	 * 
	 * @return the list of data types that can be transferred by this DragSource
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
	 * @see DragSourceListener
	 * @see #addDragListener
	 * @see #getDragListeners
	 */
	public void removeDragListener(DragSourceListener listener) {
		if (listener == null)
			DND.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(DND.DragStart, listener);
		removeListener(DND.DragSetData, listener);
		removeListener(DND.DragEnd, listener);
	}

	/**
	 * Specifies the drag effect for this DragSource. This drag effect will be
	 * used during a drag and drop operation.
	 * 
	 * @param effect
	 *            the drag effect that is registered for this DragSource
	 * 
	 * @since 3.3
	 */
	public void setDragSourceEffect(DragSourceEffect effect) {
		dragEffect = effect;
	}

	/**
	 * Specifies the list of data types that can be transferred by this
	 * DragSource. The application must be able to provide data to match each of
	 * these types when a successful drop has occurred.
	 * 
	 * @param transferAgents
	 *            a list of Transfer objects which define the types of data that
	 *            can be dragged from this source
	 */
	public void setTransfer(Transfer[] transferAgents) {
	}

	static long /* int */createPixbuf(Image image) {
		return 0;
	}
}
