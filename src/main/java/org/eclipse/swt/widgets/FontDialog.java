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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**
 * Instances of this class allow the user to select a font from all available
 * fonts in the system.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample, Dialog tab</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class FontDialog extends Dialog {
	FontData fontData;
	RGB rgb;

	/**
	 * Constructs a new instance of this class given only its parent.
	 * 
	 * @param parent
	 *            a shell which will be the parent of the new instance
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
	 */
	public FontDialog(Shell parent) {
		this(parent, SWT.APPLICATION_MODAL);
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
	 *            a shell which will be the parent of the new instance
	 * @param style
	 *            the style of dialog to construct
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
	 */
	public FontDialog(Shell parent, int style) {
		super(parent, checkStyle(parent, style));
		checkSubclass();
	}

	/**
	 * Returns a FontData object describing the font that was selected in the
	 * dialog, or null if none is available.
	 * 
	 * @return the FontData for the selected font, or null
	 * @deprecated use #getFontList ()
	 */
	public FontData getFontData() {
		return fontData;
	}

	/**
	 * Returns a FontData set describing the font that was selected in the
	 * dialog, or null if none is available.
	 * 
	 * @return the FontData for the selected font, or null
	 * @since 2.1.1
	 */
	public FontData[] getFontList() {
		if (fontData == null)
			return null;
		FontData[] result = new FontData[1];
		result[0] = fontData;
		return result;
	}

	/**
	 * Returns an RGB describing the color that was selected in the dialog, or
	 * null if none is available.
	 * 
	 * @return the RGB value for the selected color, or null
	 * 
	 * @see PaletteData#getRGBs
	 * 
	 * @since 2.1
	 */
	public RGB getRGB() {
		return rgb;
	}

	/**
	 * Makes the dialog visible and brings it to the front of the display.
	 * 
	 * @return a FontData object describing the font that was selected, or null
	 *         if the dialog was cancelled or an error occurred
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the dialog has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the dialog</li>
	 *                </ul>
	 */
	public FontData open() {
		return fontData;
	}

	/**
	 * Sets a FontData object describing the font to be selected by default in
	 * the dialog, or null to let the platform choose one.
	 * 
	 * @param fontData
	 *            the FontData to use initially, or null
	 * @deprecated use #setFontList (FontData [])
	 */
	public void setFontData(FontData fontData) {
		this.fontData = fontData;
	}

	/**
	 * Sets the set of FontData objects describing the font to be selected by
	 * default in the dialog, or null to let the platform choose one.
	 * 
	 * @param fontData
	 *            the set of FontData objects to use initially, or null to let
	 *            the platform select a default when open() is called
	 * 
	 * @see Font#getFontData
	 * 
	 * @since 2.1.1
	 */
	public void setFontList(FontData[] fontData) {
		if (fontData != null && fontData.length > 0) {
			this.fontData = fontData[0];
		} else {
			this.fontData = null;
		}
	}

	/**
	 * Sets the RGB describing the color to be selected by default in the
	 * dialog, or null to let the platform choose one.
	 * 
	 * @param rgb
	 *            the RGB value to use initially, or null to let the platform
	 *            select a default when open() is called
	 * 
	 * @see PaletteData#getRGBs
	 * 
	 * @since 2.1
	 */
	public void setRGB(RGB rgb) {
		this.rgb = rgb;
	}
}
