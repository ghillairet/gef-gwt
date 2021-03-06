/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when a thread is waiting, sleeping, or otherwise occupied, and the
 * thread is interrupted, either before or during the activity. Occasionally a
 * method may wish to test whether the current thread has been interrupted, and
 * if so, to immediately throw this exception. The following code can be used to
 * achieve this effect:
 * 
 * <pre>
 * if (Thread.interrupted()) // Clears interrupted status!
 * 	throw new InterruptedException();
 * </pre>
 * 
 * @author Frank Yellin
 * @version %I%, %G%
 * @see java.lang.Object#wait()
 * @see java.lang.Object#wait(long)
 * @see java.lang.Object#wait(long, int)
 * @since JDK1.0
 */
public class InterruptedException extends Exception {
	/**
	 * Constructs an <code>InterruptedException</code> with no detail message.
	 */
	public InterruptedException() {
		super();
	}

	/**
	 * Constructs an <code>InterruptedException</code> with the specified detail
	 * message.
	 * 
	 * @param s
	 *            the detail message.
	 */
	public InterruptedException(String s) {
		super(s);
	}
}
