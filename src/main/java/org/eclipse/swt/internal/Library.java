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
package org.eclipse.swt.internal;

public class Library {

	/* SWT Version - Mmmm (M=major, mmm=minor) */

	/**
	 * SWT Major version number (must be >= 0)
	 */
	static int MAJOR_VERSION = 3;

	/**
	 * SWT Minor version number (must be in the range 0..999)
	 */
	static int MINOR_VERSION = 738;

	/**
	 * SWT revision number (must be >= 0)
	 */
	static int REVISION = 0;

	/**
	 * The JAVA and SWT versions
	 */
	public static final int JAVA_VERSION, SWT_VERSION;

	static final String SEPARATOR;
	static final String DELIMITER;

	/* 64-bit support */
	static final boolean IS_64 = longConst() == (long /* int */) longConst();
	static final String SUFFIX_64 = "-64"; //$NON-NLS-1$
	static final String SWT_LIB_DIR;

	static {
		//		DELIMITER = System.getProperty("line.separator"); //$NON-NLS-1$
		//		SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
		DELIMITER = "\n";
		SEPARATOR = "/";
		SWT_LIB_DIR = ".swt" + SEPARATOR + "lib" + SEPARATOR + os() + SEPARATOR + arch(); //$NON-NLS-1$ $NON-NLS-2$
		//		JAVA_VERSION = parseVersion(System.getProperty("java.version")); //$NON-NLS-1$
		JAVA_VERSION = 6; //$NON-NLS-1$
		SWT_VERSION = SWT_VERSION(MAJOR_VERSION, MINOR_VERSION);
	}

	static String arch() {
		return "browser";
	}

	static String os() {
		return "browser";
	}

	static void chmod(String permision, String path) {
		if (Platform.PLATFORM.equals("win32"))return; //$NON-NLS-1$
		try {
			// Runtime.getRuntime()
			//					.exec(new String[] { "chmod", permision, path }).waitFor(); //$NON-NLS-1$
		} catch (Throwable e) {
		}
	}

	/* Use method instead of in-lined constants to avoid compiler warnings */
	static long longConst() {
		return 0x1FFFFFFFFL;
	}

	static int parseVersion(String version) {
		if (version == null)
			return 0;
		int major = 0, minor = 0, micro = 0;
		int length = version.length(), index = 0, start = 0;
		while (index < length && Character.isDigit(version.charAt(index)))
			index++;
		try {
			if (start < length)
				major = Integer.parseInt(version.substring(start, index));
		} catch (NumberFormatException e) {
		}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index)))
			index++;
		try {
			if (start < length)
				minor = Integer.parseInt(version.substring(start, index));
		} catch (NumberFormatException e) {
		}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index)))
			index++;
		try {
			if (start < length)
				micro = Integer.parseInt(version.substring(start, index));
		} catch (NumberFormatException e) {
		}
		return JAVA_VERSION(major, minor, micro);
	}

	/**
	 * Returns the Java version number as an integer.
	 * 
	 * @param major
	 * @param minor
	 * @param micro
	 * @return the version
	 */
	public static int JAVA_VERSION(int major, int minor, int micro) {
		return (major << 16) + (minor << 8) + micro;
	}

	/**
	 * Returns the SWT version number as an integer.
	 * 
	 * @param major
	 * @param minor
	 * @return the version
	 */
	public static int SWT_VERSION(int major, int minor) {
		return major * 1000 + minor;
	}

	static boolean extract(String fileName, String mappedName,
			StringBuffer message) {
		return false;
	}

	/**
	 * Loads the shared library that matches the version of the Java code which
	 * is currently running. SWT shared libraries follow an encoding scheme
	 * where the major, minor and revision numbers are embedded in the library
	 * name and this along with <code>name</code> is used to load the library.
	 * If this fails, <code>name</code> is used in another attempt to load the
	 * library, this time ignoring the SWT version encoding scheme.
	 * 
	 * @param name
	 *            the name of the library to load
	 */
	public static void loadLibrary(String name) {
		// loadLibrary(name, true);
	}

}
