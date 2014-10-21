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

import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.Converter;

/**
 * Instances of this class are used to print to a printer. Applications create a
 * GC on a printer using <code>new GC(printer)</code> and then draw on the
 * printer GC using the usual graphics calls.
 * <p>
 * A <code>Printer</code> object may be constructed by providing a
 * <code>PrinterData</code> object which identifies the printer. A
 * <code>PrintDialog</code> presents a print dialog to the user and returns an
 * initialized instance of <code>PrinterData</code>. Alternatively, calling
 * <code>new Printer()</code> will construct a printer object for the user's
 * default printer.
 * </p>
 * <p>
 * Application code must explicitly invoke the <code>Printer.dispose()</code>
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required.
 * </p>
 * 
 * @see PrinterData
 * @see PrintDialog
 * @see <a href="http://www.eclipse.org/swt/snippets/#printing">Printing
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 */
public final class Printer extends Device {
	static PrinterData[] printerList;
	static long /* int */findPrinter;
	static PrinterData findData;

	PrinterData data;
	long /* int */printer;
	long /* int */printJob;
	long /* int */settings;
	long /* int */pageSetup;
	long /* int */surface;
	long /* int */cairo;

	/**
	 * whether or not a GC was created for this printer
	 */
	boolean isGCCreated = false;
	Font systemFont;

	static byte[] settingsData;
	static int start, end;

	static final String GTK_LPR_BACKEND = "GtkPrintBackendLpr"; //$NON-NLS-1$
	static final String GTK_FILE_BACKEND = "GtkPrintBackendFile"; //$NON-NLS-1$

	static boolean disabledPrinting = true;

	static void gtk_init() {
	}

	/**
	 * Returns an array of <code>PrinterData</code> objects representing all
	 * available printers. If there are no printers, the array will be empty.
	 * 
	 * @return an array of PrinterData objects representing the available
	 *         printers
	 */
	public static PrinterData[] getPrinterList() {
		return printerList;
	}

	static long /* int */GtkPrinterFunc_List(long /* int */printer,
			long /* int */user_data) {
		int length = printerList.length;
		PrinterData[] newList = new PrinterData[length + 1];
		System.arraycopy(printerList, 0, newList, 0, length);
		printerList = newList;
		printerList[length] = printerDataFromGtkPrinter(printer);
		/*
		 * Bug in GTK. While performing a gtk_enumerate_printers(), GTK finds
		 * all of the available printers from each backend and can hang. If a
		 * backend requires more time to gather printer info, GTK will start an
		 * event loop waiting for a done signal before continuing. For the Lpr
		 * backend, GTK does not send a done signal which means the event loop
		 * never ends. The fix is to check to see if the driver is of type Lpr,
		 * and stop the enumeration, which exits the event loop.
		 */
		if (printerList[length].driver.equals(GTK_LPR_BACKEND))
			return 1;
		return 0;
	}

	/**
	 * Returns a <code>PrinterData</code> object representing the default
	 * printer or <code>null</code> if there is no default printer.
	 * 
	 * @return the default printer data or null
	 * 
	 * @since 2.1
	 */
	public static PrinterData getDefaultPrinterData() {
		return findData;
	}

	static long /* int */GtkPrinterFunc_Default(long /* int */printer,
			long /* int */user_data) {
		return 0;
	}

	static long /* int */gtkPrinterFromPrinterData(PrinterData data) {
		return findPrinter;
	}

	static long /* int */GtkPrinterFunc_FindNamedPrinter(long /* int */printer,
			long /* int */user_data) {
		return 0;
	}

	static PrinterData printerDataFromGtkPrinter(long /* int */printer) {
		return null;
	}

	/*
	 * Restore printer settings and page_setup data from data.
	 */
	static void restore(byte[] data, long /* int */settings,
			long /* int */page_setup) {
	}

	static byte[] uriFromFilename(String filename) {
		return null;
	}

	static DeviceData checkNull(PrinterData data) {
		return data;
	}

	/**
	 * Constructs a new printer representing the default printer.
	 * <p>
	 * Note: You must dispose the printer when it is no longer required.
	 * </p>
	 * 
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES - if there is no valid default
	 *                printer
	 *                </ul>
	 * 
	 * @see Device#dispose
	 */
	public Printer() {
		this(null);
	}

	/**
	 * Constructs a new printer given a <code>PrinterData</code> object
	 * representing the desired printer. If the argument is null, then the
	 * default printer will be used.
	 * <p>
	 * Note: You must dispose the printer when it is no longer required.
	 * </p>
	 * 
	 * @param data
	 *            the printer data for the specified printer, or null to use the
	 *            default printer
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the specified printer data
	 *                does not represent a valid printer
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES - if there are no valid printers
	 *                </ul>
	 * 
	 * @see Device#dispose
	 */
	public Printer(PrinterData data) {
		super(checkNull(data));
	}

	static int restoreInt(String key) {
		byte[] value = restoreBytes(key, false);
		return Integer.parseInt(new String(value));
	}

	static double restoreDouble(String key) {
		byte[] value = restoreBytes(key, false);
		return Double.parseDouble(new String(value));
	}

	static boolean restoreBoolean(String key) {
		byte[] value = restoreBytes(key, false);
		return Boolean.valueOf(new String(value)).booleanValue();
	}

	static byte[] restoreBytes(String key, boolean nullTerminate) {
		// get key
		start = end;
		while (end < settingsData.length && settingsData[end] != 0)
			end++;
		end++;
		byte[] keyBuffer = new byte[end - start];
		System.arraycopy(settingsData, start, keyBuffer, 0, keyBuffer.length);

		// get value
		start = end;
		while (end < settingsData.length && settingsData[end] != 0)
			end++;
		int length = end - start;
		end++;
		if (nullTerminate)
			length++;
		byte[] valueBuffer = new byte[length];
		System.arraycopy(settingsData, start, valueBuffer, 0, length);

		if (DEBUG)
			System.out
					.println(new String(Converter.mbcsToWcs(null, keyBuffer))
							+ ": "
							+ new String(Converter.mbcsToWcs(null, valueBuffer)));

		return valueBuffer;
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
		return null;
	}

	/**
	 * Invokes platform specific functionality to allocate a new GC handle.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Printer</code>. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms, and should never be called from application code.
	 * </p>
	 * 
	 * @param data
	 *            the platform specific GC data
	 * @return the platform specific GC handle
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public long /* int */internal_new_GC(GCData data) {
		return 0;
	}

	/**
	 * Invokes platform specific functionality to dispose a GC handle.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Printer</code>. It is marked public only so that it can be shared
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
	 * Releases any internal state prior to destroying this printer. This method
	 * is called internally by the dispose mechanism of the <code>Device</code>
	 * class.
	 */
	protected void release() {
		super.release();

		/* Dispose the default font */
		if (systemFont != null)
			systemFont.dispose();
		systemFont = null;
	}

	/**
	 * Starts a print job and returns true if the job started successfully and
	 * false otherwise.
	 * <p>
	 * This must be the first method called to initiate a print job, followed by
	 * any number of startPage/endPage calls, followed by endJob. Calling
	 * startPage, endPage, or endJob before startJob will result in undefined
	 * behavior.
	 * </p>
	 * 
	 * @param jobName
	 *            the name of the print job to start
	 * @return true if the job started successfully and false otherwise.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #startPage
	 * @see #endPage
	 * @see #endJob
	 */
	public boolean startJob(String jobName) {
		return true;
	}

	/**
	 * Destroys the printer handle. This method is called internally by the
	 * dispose mechanism of the <code>Device</code> class.
	 */
	protected void destroy() {
	}

	/**
	 * Ends the current print job.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #startJob
	 * @see #startPage
	 * @see #endPage
	 */
	public void endJob() {
	}

	/**
	 * Cancels a print job in progress.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void cancelJob() {
	}

	/**
	 * Starts a page and returns true if the page started successfully and false
	 * otherwise.
	 * <p>
	 * After calling startJob, this method may be called any number of times
	 * along with a matching endPage.
	 * </p>
	 * 
	 * @return true if the page started successfully and false otherwise.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #endPage
	 * @see #startJob
	 * @see #endJob
	 */
	public boolean startPage() {
		return true;
	}

	/**
	 * Ends the current page.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #startPage
	 * @see #startJob
	 * @see #endJob
	 */
	public void endPage() {
	}

	/**
	 * Returns a point whose x coordinate is the horizontal dots per inch of the
	 * printer, and whose y coordinate is the vertical dots per inch of the
	 * printer.
	 * 
	 * @return the horizontal and vertical DPI
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Point getDPI() {
		return null;
	}

	/**
	 * Returns a rectangle describing the receiver's size and location.
	 * <p>
	 * For a printer, this is the size of the physical page, in pixels.
	 * </p>
	 * 
	 * @return the bounding rectangle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #getClientArea
	 * @see #computeTrim
	 */
	public Rectangle getBounds() {
		return null;
	}

	/**
	 * Returns a rectangle which describes the area of the receiver which is
	 * capable of displaying data.
	 * <p>
	 * For a printer, this is the size of the printable area of the page, in
	 * pixels.
	 * </p>
	 * 
	 * @return the client area
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #getBounds
	 * @see #computeTrim
	 */
	public Rectangle getClientArea() {
		return null;
	}

	Point getIndependentDPI() {
		return new Point(72, 72);
	}

	/**
	 * Given a <em>client area</em> (as described by the arguments), returns a
	 * rectangle, relative to the client area's coordinates, that is the client
	 * area expanded by the printer's trim (or minimum margins).
	 * <p>
	 * Most printers have a minimum margin on each edge of the paper where the
	 * printer device is unable to print. This margin is known as the "trim."
	 * This method can be used to calculate the printer's minimum margins by
	 * passing in a client area of 0, 0, 0, 0 and then using the resulting x and
	 * y coordinates (which will be <= 0) to determine the minimum margins for
	 * the top and left edges of the paper, and the resulting width and height
	 * (offset by the resulting x and y) to determine the minimum margins for
	 * the bottom and right edges of the paper, as follows:
	 * <ul>
	 * <li>The left trim width is -x pixels</li>
	 * <li>The top trim height is -y pixels</li>
	 * <li>The right trim width is (x + width) pixels</li>
	 * <li>The bottom trim height is (y + height) pixels</li>
	 * </ul>
	 * </p>
	 * 
	 * @param x
	 *            the x coordinate of the client area
	 * @param y
	 *            the y coordinate of the client area
	 * @param width
	 *            the width of the client area
	 * @param height
	 *            the height of the client area
	 * @return a rectangle, relative to the client area's coordinates, that is
	 *         the client area expanded by the printer's trim (or minimum
	 *         margins)
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #getBounds
	 * @see #getClientArea
	 */
	public Rectangle computeTrim(int x, int y, int width, int height) {
		return null;
	}

	/**
	 * Creates the printer handle. This method is called internally by the
	 * instance creation mechanism of the <code>Device</code> class.
	 * 
	 * @param deviceData
	 *            the device data
	 */
	protected void create(DeviceData deviceData) {
	}

	/**
	 * Initializes any internal resources needed by the device.
	 * <p>
	 * This method is called after <code>create</code>.
	 * </p>
	 * <p>
	 * If subclasses reimplement this method, they must call the
	 * <code>super</code> implementation.
	 * </p>
	 * 
	 * @see #create
	 */
	protected void init() {
	}

	/**
	 * Returns a <code>PrinterData</code> object representing the target printer
	 * for this print job.
	 * 
	 * @return a PrinterData object describing the receiver
	 */
	public PrinterData getPrinterData() {
		checkDevice();
		return data;
	}

}
