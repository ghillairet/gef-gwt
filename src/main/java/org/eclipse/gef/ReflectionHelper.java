/*******************************************************************************
 * Copyright (c) 2012 Gerhardt Informatics Kft.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gerhardt Informatics Kft. - GEFGWT port
 *******************************************************************************/
package org.eclipse.gef;

import java.util.HashMap;
import java.util.Map;

public class ReflectionHelper {

	private static Map<Class, Instantiator> helpers = new HashMap<Class, Instantiator>();

	public interface Instantiator {
		Object newInstance(Class c);
	}

	public static Object newInstance(Class type) {
		Instantiator instantiator = helpers.get(type);
		if (instantiator == null) {
			System.err.println("No instantiator for: " + type);
		}
		Object newInstance = instantiator.newInstance(type);
		if (newInstance == null) {
			System.err.println("No instance for: " + type);
		}
		return newInstance;
	}

	public static void registerHelper(Class c, Instantiator instantiator) {
		helpers.put(c, instantiator);
	}

}
