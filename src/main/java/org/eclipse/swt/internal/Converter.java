/*******************************************************************************
 * Copyright (c) 2000, 2005, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gerhardt Informatics Kft. - GEFGWT port
 *******************************************************************************/
package org.eclipse.swt.internal;

/**
 * This class implements the conversions between unicode characters and the
 * <em>platform supported</em> representation for characters.
 * <p>
 * Note that, unicode characters which can not be found in the platform encoding
 * will be converted to an arbitrary platform specific character.
 * </p>
 */
public final class Converter {
	public static final byte[] NullByteArray = new byte[1];
	public static final byte[] EmptyByteArray = new byte[0];
	public static final char[] EmptyCharArray = new char[0];

	/**
	 * Returns the default code page for the platform where the application is
	 * currently running.
	 * 
	 * @return the default code page
	 */
	public static String defaultCodePage() {
		return "UTF8";
	}

	public static char[] mbcsToWcs(String codePage, byte[] buffer) {
		return null;
	}

	public static byte[] wcsToMbcs(String codePage, String string,
			boolean terminate) {
		int length = string.length();
		char[] buffer = new char[length];
		string.getChars(0, length, buffer, 0);
		return wcsToMbcs(codePage, buffer, terminate);
	}

	public static byte[] wcsToMbcs(String codePage, char[] buffer,
			boolean terminate) {
		return null;
	}

}
