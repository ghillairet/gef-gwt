/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gunnar Wagenknecht <gunnar@wagenknecht.org> - Fix for bug 265445
 *     Benjamin Cabe <benjamin.cabe@anyware-tech.com> - Fix for bug 265532     
 *******************************************************************************/
package org.eclipse.core.runtime;

import org.eclipse.core.internal.runtime.InternalPlatform;

/**
 * The central class of the Eclipse Platform Runtime. This class cannot be
 * instantiated or subclassed by clients; all functionality is provided by
 * static methods. Features include:
 * <ul>
 * <li>the platform registry of installed plug-ins</li>
 * <li>the platform adapter manager</li>
 * <li>the platform log</li>
 * <li>the authorization info management</li>
 * </ul>
 * <p>
 * Most users don't have to worry about Platform's lifecycle. However, if your
 * code can call methods of this class when Platform is not running, it becomes
 * necessary to check {@link #isRunning()} before making the call. A runtime
 * exception might be thrown or incorrect result might be returned if a method
 * from this class is called while Platform is not running.
 * </p>
 */
public final class Platform {

	public static final String OS_MACOSX = "macosx";

	/**
	 * Returns the adapter manager used for extending <code>IAdaptable</code>
	 * objects.
	 * 
	 * @return the adapter manager for this platform
	 * @see IAdapterManager
	 */
	public static IAdapterManager getAdapterManager() {
		return InternalPlatform.getDefault().getAdapterManager();
	}

	public static Object getOS() {
		return "browser";
	}

	public static void run(ISafeRunnable safeRunnable) {
		// TODO Auto-generated method stub

	}

	// public static IPreferencesService getPreferencesService() {
	// return InternalPlatform.getDefault().getPreferencesService();
	// }

}
