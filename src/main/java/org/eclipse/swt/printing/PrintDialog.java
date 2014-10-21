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
package org.eclipse.swt.printing;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class allow the user to select a printer and various
 * print-related parameters prior to starting a print job.
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/snippets/#printing">Printing
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample, Dialog tab</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class PrintDialog extends Dialog {
	PrinterData printerData = new PrinterData();

	long /* int */handle;
	int index;
	byte[] settingsData;

	static final String GET_MODAL_DIALOG = "org.eclipse.swt.internal.gtk.getModalDialog";
	static final String SET_MODAL_DIALOG = "org.eclipse.swt.internal.gtk.setModalDialog";
	static final String ADD_IDLE_PROC_KEY = "org.eclipse.swt.internal.gtk.addIdleProc";
	static final String REMOVE_IDLE_PROC_KEY = "org.eclipse.swt.internal.gtk.removeIdleProc";
	static final String GET_EMISSION_PROC_KEY = "org.eclipse.swt.internal.gtk.getEmissionProc";

	/**
	 * Constructs a new instance of this class given only its parent.
	 * 
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
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
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public PrintDialog(Shell parent) {
		this(parent, SWT.PRIMARY_MODAL);
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
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public PrintDialog(Shell parent, int style) {
		super(parent, checkStyleBit(parent, style));
		checkSubclass();
	}

	/**
	 * Sets the printer data that will be used when the dialog is opened.
	 * <p>
	 * Setting the printer data to null is equivalent to resetting all data
	 * fields to their default values.
	 * </p>
	 * 
	 * @param data
	 *            the data that will be used when the dialog is opened or null
	 *            to use default data
	 * 
	 * @since 3.4
	 */
	public void setPrinterData(PrinterData data) {
		if (data == null)
			data = new PrinterData();
		this.printerData = data;
	}

	/**
	 * Returns the printer data that will be used when the dialog is opened.
	 * 
	 * @return the data that will be used when the dialog is opened
	 * 
	 * @since 3.4
	 */
	public PrinterData getPrinterData() {
		return printerData;
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

	static int checkStyleBit(Shell parent, int style) {
		int mask = SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;
		if ((style & SWT.SHEET) != 0) {
			style &= ~SWT.SHEET;
			if ((style & mask) == 0) {
				style |= parent == null ? SWT.APPLICATION_MODAL
						: SWT.PRIMARY_MODAL;
			}
		}
		if ((style & mask) == 0) {
			style |= SWT.APPLICATION_MODAL;
		}
		style &= ~SWT.MIRRORED;
		if ((style & (SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT)) == 0) {
			if (parent != null) {
				if ((parent.getStyle() & SWT.LEFT_TO_RIGHT) != 0)
					style |= SWT.LEFT_TO_RIGHT;
				if ((parent.getStyle() & SWT.RIGHT_TO_LEFT) != 0)
					style |= SWT.RIGHT_TO_LEFT;
			}
		}
		return checkBits(style, SWT.LEFT_TO_RIGHT, SWT.RIGHT_TO_LEFT, 0, 0, 0,
				0);
	}

	protected void checkSubclass() {
	}

	/**
	 * Returns the print job scope that the user selected before pressing OK in
	 * the dialog. This will be one of the following values:
	 * <dl>
	 * <dt><code>PrinterData.ALL_PAGES</code></dt>
	 * <dd>Print all pages in the current document</dd>
	 * <dt><code>PrinterData.PAGE_RANGE</code></dt>
	 * <dd>Print the range of pages specified by startPage and endPage</dd>
	 * <dt><code>PrinterData.SELECTION</code></dt>
	 * <dd>Print the current selection</dd>
	 * </dl>
	 * 
	 * @return the scope setting that the user selected
	 */
	public int getScope() {
		return printerData.scope;
	}

	/**
	 * Sets the scope of the print job. The user will see this setting when the
	 * dialog is opened. This can have one of the following values:
	 * <dl>
	 * <dt><code>PrinterData.ALL_PAGES</code></dt>
	 * <dd>Print all pages in the current document</dd>
	 * <dt><code>PrinterData.PAGE_RANGE</code></dt>
	 * <dd>Print the range of pages specified by startPage and endPage</dd>
	 * <dt><code>PrinterData.SELECTION</code></dt>
	 * <dd>Print the current selection</dd>
	 * </dl>
	 * 
	 * @param scope
	 *            the scope setting when the dialog is opened
	 */
	public void setScope(int scope) {
		printerData.scope = scope;
	}

	/**
	 * Returns the start page setting that the user selected before pressing OK
	 * in the dialog.
	 * <p>
	 * This value can be from 1 to the maximum number of pages for the platform.
	 * Note that it is only valid if the scope is
	 * <code>PrinterData.PAGE_RANGE</code>.
	 * </p>
	 * 
	 * @return the start page setting that the user selected
	 */
	public int getStartPage() {
		return printerData.startPage;
	}

	/**
	 * Sets the start page that the user will see when the dialog is opened.
	 * <p>
	 * This value can be from 1 to the maximum number of pages for the platform.
	 * Note that it is only valid if the scope is
	 * <code>PrinterData.PAGE_RANGE</code>.
	 * </p>
	 * 
	 * @param startPage
	 *            the startPage setting when the dialog is opened
	 */
	public void setStartPage(int startPage) {
		printerData.startPage = startPage;
	}

	/**
	 * Returns the end page setting that the user selected before pressing OK in
	 * the dialog.
	 * <p>
	 * This value can be from 1 to the maximum number of pages for the platform.
	 * Note that it is only valid if the scope is
	 * <code>PrinterData.PAGE_RANGE</code>.
	 * </p>
	 * 
	 * @return the end page setting that the user selected
	 */
	public int getEndPage() {
		return printerData.endPage;
	}

	/**
	 * Sets the end page that the user will see when the dialog is opened.
	 * <p>
	 * This value can be from 1 to the maximum number of pages for the platform.
	 * Note that it is only valid if the scope is
	 * <code>PrinterData.PAGE_RANGE</code>.
	 * </p>
	 * 
	 * @param endPage
	 *            the end page setting when the dialog is opened
	 */
	public void setEndPage(int endPage) {
		printerData.endPage = endPage;
	}

	/**
	 * Returns the 'Print to file' setting that the user selected before
	 * pressing OK in the dialog.
	 * 
	 * @return the 'Print to file' setting that the user selected
	 */
	public boolean getPrintToFile() {
		return printerData.printToFile;
	}

	/**
	 * Sets the 'Print to file' setting that the user will see when the dialog
	 * is opened.
	 * 
	 * @param printToFile
	 *            the 'Print to file' setting when the dialog is opened
	 */
	public void setPrintToFile(boolean printToFile) {
		printerData.printToFile = printToFile;
	}

	/**
	 * Makes the receiver visible and brings it to the front of the display.
	 * 
	 * @return a printer data object describing the desired print job
	 *         parameters, or null if the dialog was canceled, no printers were
	 *         found, or an error occurred
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public PrinterData open() {
		return null;
	}

	long /* int */GtkPrintSettingsFunc(long /* int */key, long /* int */value,
			long /* int */data) {
		return 0;
	}

	void store(String key, int value) {
		store(key, String.valueOf(value));
	}

	void store(String key, double value) {
		store(key, String.valueOf(value));
	}

	void store(String key, boolean value) {
		store(key, String.valueOf(value));
	}

	void storeBytes(String key, long /* int */value) {
	}

	void store(String key, String value) {
		store(key.getBytes(), value.getBytes());
	}

	void store(byte[] key, byte[] value) {
		int length = key.length + 1 + value.length + 1;
		if (index + length + 1 > settingsData.length) {
			byte[] newData = new byte[settingsData.length
					+ Math.max(length + 1, 1024)];
			System.arraycopy(settingsData, 0, newData, 0, settingsData.length);
			settingsData = newData;
		}
		System.arraycopy(key, 0, settingsData, index, key.length);
		index += key.length + 1; // null terminated
		System.arraycopy(value, 0, settingsData, index, value.length);
		index += value.length + 1; // null terminated
	}
}
