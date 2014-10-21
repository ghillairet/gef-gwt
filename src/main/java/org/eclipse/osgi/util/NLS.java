package org.eclipse.osgi.util;

/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/

/**
 * Common superclass for all message bundle classes. Provides convenience
 * methods for manipulating messages.
 * <p>
 * The <code>#bind</code> methods perform string substitution and should be
 * considered a convenience and <em>not</em> a full substitute replacement for
 * <code>MessageFormat#format</code> method calls.
 * </p>
 * <p>
 * Text appearing within curly braces in the given message, will be interpreted
 * as a numeric index to the corresponding substitution object in the given
 * array. Calling the <code>#bind</code> methods with text that does not map to
 * an integer will result in an {@link IllegalArgumentException}.
 * </p>
 * <p>
 * Text appearing within single quotes is treated as a literal. A single quote
 * is escaped by a preceeding single quote.
 * </p>
 * <p>
 * Clients who wish to use the full substitution power of the
 * <code>MessageFormat</code> class should call that class directly and not use
 * these <code>#bind</code> methods.
 * </p>
 * <p>
 * Clients may subclass this type.
 * </p>
 * 
 * @since 3.1
 */
public abstract class NLS {

	/**
	 * Bind the given message's substitution locations with the given string
	 * value.
	 * 
	 * @param message
	 *            the message to be manipulated
	 * @param binding
	 *            the object to be inserted into the message
	 * @return the manipulated String
	 * @throws IllegalArgumentException
	 *             if the text appearing within curly braces in the given
	 *             message does not map to an integer
	 */
	public static String bind(String message, Object binding) {
		return internalBind(message, null, String.valueOf(binding), null);
	}

	/**
	 * Bind the given message's substitution locations with the given string
	 * values.
	 * 
	 * @param message
	 *            the message to be manipulated
	 * @param binding1
	 *            An object to be inserted into the message
	 * @param binding2
	 *            A second object to be inserted into the message
	 * @return the manipulated String
	 * @throws IllegalArgumentException
	 *             if the text appearing within curly braces in the given
	 *             message does not map to an integer
	 */
	public static String bind(String message, Object binding1, Object binding2) {
		return internalBind(message, null, String.valueOf(binding1),
				String.valueOf(binding2));
	}

	/**
	 * Bind the given message's substitution locations with the given string
	 * values.
	 * 
	 * @param message
	 *            the message to be manipulated
	 * @param bindings
	 *            An array of objects to be inserted into the message
	 * @return the manipulated String
	 * @throws IllegalArgumentException
	 *             if the text appearing within curly braces in the given
	 *             message does not map to an integer
	 */
	public static String bind(String message, Object[] bindings) {
		return internalBind(message, bindings, null, null);
	}

	/**
	 * Initialize the given class with the values from the message properties
	 * specified by the base name. The base name specifies a fully qualified
	 * base name to a message properties file, including the package where the
	 * message properties file is located. The class loader of the specified
	 * class will be used to load the message properties resources.
	 * <p>
	 * For example, if the locale is set to en_US and
	 * <code>org.eclipse.example.nls.messages</code> is used as the base name
	 * then the following resources will be searched using the class loader of
	 * the specified class:
	 * 
	 * <pre>
	 *   org/eclipse/example/nls/messages_en_US.properties
	 *   org/eclipse/example/nls/messages_en.properties
	 *   org/eclipse/example/nls/messages.properties
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param baseName
	 *            the base name of a fully qualified message properties file.
	 * @param clazz
	 *            the class where the constants will exist
	 */
	public static void initializeMessages(final String baseName,
			final Class<?> clazz) {
		// if (System.getSecurityManager() == null) {
		// load(baseName, clazz);
		// return;
		// }
		// AccessController.doPrivileged(new PrivilegedAction<Object>() {
		// public Object run() {
		// load(baseName, clazz);
		// return null;
		// }
		// });
	}

	/*
	 * Perform the string substitution on the given message with the specified
	 * args. See the class comment for exact details.
	 */
	private static String internalBind(String message, Object[] args,
			String argZero, String argOne) {
		return message;
	}

	/*
	 * Load the given resource bundle using the specified class loader.
	 */
	static void load(final String bundleName, Class<?> clazz) {
	}

	/*
	 * The method adds a log entry based on the error message and exception. The
	 * output is written to the System.err.
	 * 
	 * This method is only expected to be called if there is a problem in the
	 * NLS mechanism. As a result, translation facility is not available here
	 * and messages coming out of this log are generally not translated.
	 * 
	 * @param severity - severity of the message (SEVERITY_ERROR or
	 * SEVERITY_WARNING)
	 * 
	 * @param message - message to log
	 * 
	 * @param e - exception to log
	 */
	static void log(int severity, String message, Exception e) {
	}

}