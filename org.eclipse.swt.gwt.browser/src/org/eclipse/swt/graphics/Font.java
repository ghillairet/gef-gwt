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
package org.eclipse.swt.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;

/**
 * Instances of this class manage operating system resources that define how
 * text looks when it is displayed. Fonts may be constructed by providing a
 * device and either name, size and style information or a <code>FontData</code>
 * object which encapsulates this data.
 * <p>
 * Application code must explicitly invoke the <code>Font.dispose()</code>
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required.
 * </p>
 * 
 * @see FontData
 * @see <a href="http://www.eclipse.org/swt/snippets/#font">Font snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Examples:
 *      GraphicsExample, PaintExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 */
public final class Font extends Resource {
	/**
	 * the handle to the OS font resource (Warning: This field is platform
	 * dependent)
	 * <p>
	 * <b>IMPORTANT:</b> This field is <em>not</em> part of the SWT public API.
	 * It is marked public only so that it can be shared within the packages
	 * provided by SWT. It is not available on all platforms and should never be
	 * accessed from application code.
	 * </p>
	 * 
	 * @noreference This field is not intended to be referenced by clients.
	 */
	public long /* int */handle;
	private FontData fontData;

	Font(Device device) {
		super(device);
		fontData = new FontData();
	}

	/**
	 * Constructs a new font given a device and font data which describes the
	 * desired font's appearance.
	 * <p>
	 * You must dispose the font when it is no longer required.
	 * </p>
	 * 
	 * @param device
	 *            the device to create the font on
	 * @param fd
	 *            the FontData that describes the desired font (must not be
	 *            null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the fd argument is null</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES - if a font could not be created from
	 *                the given font data</li>
	 *                </ul>
	 */
	public Font(Device device, FontData fd) {
		super(device);
		if (fd == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.fontData = fd;
		init(fd.getName(), fd.getHeightF(), fd.getStyle(), fd.string);
		init();
	}

	/**
	 * Constructs a new font given a device and an array of font data which
	 * describes the desired font's appearance.
	 * <p>
	 * You must dispose the font when it is no longer required.
	 * </p>
	 * 
	 * @param device
	 *            the device to create the font on
	 * @param fds
	 *            the array of FontData that describes the desired font (must
	 *            not be null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the fds argument is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the length of fds is zero</li>
	 *                <li>ERROR_NULL_ARGUMENT - if any fd in the array is null</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES - if a font could not be created from
	 *                the given font data</li>
	 *                </ul>
	 * 
	 * @since 2.1
	 */
	public Font(Device device, FontData[] fds) {
		super(device);
		if (fds == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (fds.length == 0)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		for (int i = 0; i < fds.length; i++) {
			if (fds[i] == null)
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.fontData = fds[0];
		FontData fd = fds[0];
		init(fd.getName(), fd.getHeightF(), fd.getStyle(), fd.string);
		init();
	}

	/**
	 * Constructs a new font given a device, a font name, the height of the
	 * desired font in points, and a font style.
	 * <p>
	 * You must dispose the font when it is no longer required.
	 * </p>
	 * 
	 * @param device
	 *            the device to create the font on
	 * @param name
	 *            the name of the font (must not be null)
	 * @param height
	 *            the font height in points
	 * @param style
	 *            a bit or combination of NORMAL, BOLD, ITALIC
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the name argument is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the height is negative</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES - if a font could not be created from
	 *                the given arguments</li>
	 *                </ul>
	 */
	public Font(Device device, String name, int height, int style) {
		super(device);
		this.fontData = new FontData(name, height, style);
		init(name, height, style, null);
		init();
	}

	/* public */Font(Device device, String name, float height, int style) {
		super(device);
		this.fontData = new FontData(name, height, style);
		init(name, height, style, null);
		init();
	}

	void destroy() {
	}

	/**
	 * Compares the argument to the receiver, and returns true if they represent
	 * the <em>same</em> object using a class specific comparison.
	 * 
	 * @param object
	 *            the object to compare with this object
	 * @return <code>true</code> if the object is the same as this object and
	 *         <code>false</code> otherwise
	 * 
	 * @see #hashCode
	 */
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if ((object instanceof Font) == false)
			return false;
		// if (((Font) object).fontData != null) {
		// return ((Font) object).fontData.equals(this.fontData);
		// }
		return ((Font) object).fontData.equals(this.fontData);
	}

	/**
	 * Returns an array of <code>FontData</code>s representing the receiver. On
	 * Windows, only one FontData will be returned per font. On X however, a
	 * <code>Font</code> object <em>may</em> be composed of multiple X fonts. To
	 * support this case, we return an array of font data objects.
	 * 
	 * @return an array of font data objects describing the receiver
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public FontData[] getFontData() {
		return new FontData[] { new FontData(fontData) };
	}

	/**
	 * Invokes platform specific functionality to allocate a new font.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Font</code>. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms, and should never be called from application code.
	 * </p>
	 * 
	 * @param device
	 *            the device on which to allocate the color
	 * @param handle
	 *            the handle for the font
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public static Font gtk_new(Device device, long /* int */handle) {
		Font font = new Font(device);
		font.handle = handle;
		return font;
	}

	/**
	 * Returns an integer hash code for the receiver. Any two objects that
	 * return <code>true</code> when passed to <code>equals</code> must return
	 * the same value for this method.
	 * 
	 * @return the receiver's hash
	 * 
	 * @see #equals
	 */
	public int hashCode() {
		return (int) /* 64 */handle;
	}

	void init(String name, float height, int style, byte[] fontString) {
	}

	/**
	 * Returns <code>true</code> if the font has been disposed, and
	 * <code>false</code> otherwise.
	 * <p>
	 * This method gets the dispose state for the font. When a font has been
	 * disposed, it is an error to invoke any other method (except
	 * {@link #dispose()}) using the font.
	 * 
	 * @return <code>true</code> when the font is disposed and
	 *         <code>false</code> otherwise
	 */
	public boolean isDisposed() {
		// return handle == 0;
		return false;
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the receiver
	 */
	public String toString() {
		if (isDisposed())
			return "Font {*DISPOSED*}";
		return "Font {" + handle + "}";
	}

	FontData getFD() {
		return this.fontData;
	}

}
