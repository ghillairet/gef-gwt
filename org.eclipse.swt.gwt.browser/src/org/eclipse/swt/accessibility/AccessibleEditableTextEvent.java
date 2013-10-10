/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gerhardt Informatics Kft. - GEFGWT port
 *******************************************************************************/
package org.eclipse.swt.accessibility;

import org.eclipse.swt.internal.SWTEventObject;

/**
 * Instances of this class are sent as a result of accessibility clients sending
 * AccessibleEditableText messages to an accessible object.
 * 
 * @see AccessibleEditableTextListener
 * @see AccessibleEditableTextAdapter
 * 
 * @since 3.7
 */
public class AccessibleEditableTextEvent extends SWTEventObject {

	/**
	 * [in] 0-based start offset of the character range to perform the operation
	 * on
	 * 
	 * @see AccessibleEditableTextListener#copyText
	 * @see AccessibleEditableTextListener#cutText
	 * @see AccessibleEditableTextListener#pasteText
	 * @see AccessibleEditableTextListener#replaceText
	 */
	public int start;

	/**
	 * [in] 0-based ending offset of the character range to perform the
	 * operation on
	 * 
	 * @see AccessibleEditableTextListener#copyText
	 * @see AccessibleEditableTextListener#cutText
	 * @see AccessibleEditableTextListener#replaceText
	 */
	public int end;

	/**
	 * [in] a string that will replace the specified character range
	 * 
	 * @see AccessibleEditableTextListener#replaceText
	 */
	public String string;

	/**
	 * [out] Set this field to {@link ACC#OK} if the operation was completed
	 * successfully, and <code>null</code> otherwise.
	 * 
	 * @see AccessibleEditableTextListener#copyText
	 * @see AccessibleEditableTextListener#cutText
	 * @see AccessibleEditableTextListener#pasteText
	 * @see AccessibleEditableTextListener#replaceText
	 */
	public String result;

	static final long serialVersionUID = -5045447704486894646L;

	/**
	 * Constructs a new instance of this class.
	 * 
	 * @param source
	 *            the object that fired the event
	 */
	public AccessibleEditableTextEvent(Object source) {
		super(source);
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the event
	 */
	public String toString() {
		return "AccessibleEditableTextEvent {" //$NON-NLS-1$
				+ "start=" + start //$NON-NLS-1$
				+ " end=" + end //$NON-NLS-1$
				+ " string=" + string //$NON-NLS-1$
				+ " result=" + result //$NON-NLS-1$
				+ "}"; //$NON-NLS-1$
	}
}
