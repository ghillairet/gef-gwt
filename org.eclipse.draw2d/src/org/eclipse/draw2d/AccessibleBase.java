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
package org.eclipse.draw2d;

/**
 * The base class for accessible objects which provides accesibilty clients with
 * a unique ID.
 */
public class AccessibleBase {

	/**
	 * Returns the id of this accessible object using {@link Object#hashCode()}.
	 * 
	 * @return the id
	 */
	public final int getAccessibleID() {
		/*
		 * This assumes that the native implementation of hashCode in Object is
		 * to return the pointer to the Object, which should be U-unique.
		 */
		int value = super.hashCode();
		/*
		 * Values -3, -2, and -1 are reserved by SWT's ACC class to have special
		 * meaning. Therefore, a child cannot have this value.
		 */
		if (value < 0)
			value -= 4;
		return value;
	}

}
