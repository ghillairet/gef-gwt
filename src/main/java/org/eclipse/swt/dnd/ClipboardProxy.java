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
package org.eclipse.swt.dnd;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

class ClipboardProxy {
	/*
	 * Data is not flushed to the clipboard immediately. This class will
	 * remember the data and provide it when requested.
	 */
	Object[] clipboardData;
	Transfer[] clipboardDataTypes;
	Object[] primaryClipboardData;
	Transfer[] primaryClipboardDataTypes;

	// long /* int */clipboardOwner = OS.gtk_window_new(0);
	Display display;
	Clipboard activeClipboard = null;
	Clipboard activePrimaryClipboard = null;
	Callback getFunc;
	Callback clearFunc;

	static String ID = "CLIPBOARD PROXY OBJECT"; //$NON-NLS-1$

	static ClipboardProxy _getInstance(final Display display) {
		ClipboardProxy proxy = (ClipboardProxy) display.getData(ID);
		if (proxy != null)
			return proxy;
		proxy = new ClipboardProxy(display);
		display.setData(ID, proxy);
		display.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				ClipboardProxy clipbordProxy = (ClipboardProxy) display
						.getData(ID);
				if (clipbordProxy == null)
					return;
				display.setData(ID, null);
				clipbordProxy.dispose();
			}
		});
		return proxy;
	}

	ClipboardProxy(Display display) {
		this.display = display;
		getFunc = new Callback(this, "getFunc", 4); //$NON-NLS-1$
		if (getFunc.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
		clearFunc = new Callback(this, "clearFunc", 2); //$NON-NLS-1$
		if (clearFunc.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
	}

	void clear(Clipboard owner, int clipboards) {
	}

	long /* int */clearFunc(long /* int */clipboard,
			long /* int */user_data_or_owner) {
		if (clipboard == Clipboard.GTKCLIPBOARD) {
			activeClipboard = null;
			clipboardData = null;
			clipboardDataTypes = null;
		}
		if (clipboard == Clipboard.GTKPRIMARYCLIPBOARD) {
			activePrimaryClipboard = null;
			primaryClipboardData = null;
			primaryClipboardDataTypes = null;
		}
		return 1;
	}

	void dispose() {
	}

	/**
	 * This function provides the data to the clipboard on request. When this
	 * clipboard is disposed, the data will no longer be available.
	 */
	long /* int */getFunc(long /* int */clipboard, long /* int */selection_data,
			long /* int */info, long /* int */user_data_or_owner) {
		return 1;
	}

	boolean setData(Clipboard owner, Object[] data, Transfer[] dataTypes,
			int clipboards) {
		return false;
	}
}
