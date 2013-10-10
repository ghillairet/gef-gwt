/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Julian Chen - fix for bug #92572, jclRM
 *     Benjamin Cabe <benjamin.cabe@anyware-tech.com> - fix for bug 265532
 *******************************************************************************/
package org.eclipse.core.internal.runtime;

import org.eclipse.core.runtime.IAdapterManager;

/**
 * Bootstrap class for the platform. It is responsible for setting up the
 * platform class loader and passing control to the actual application class
 */
public final class InternalPlatform {

	private static final InternalPlatform singleton = new InternalPlatform();

	public static InternalPlatform getDefault() {
		return singleton;
	}

	public IAdapterManager getAdapterManager() {
		return AdapterManager.getDefault();
	}
}
