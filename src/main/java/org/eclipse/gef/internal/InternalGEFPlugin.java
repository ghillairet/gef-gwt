/*******************************************************************************
 * Copyright (c) 2006, 2010, 2012 IBM Corporation, Gerhardt Informatics Ktf. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gerhardt Informatics Ktf.
 *******************************************************************************/

package org.eclipse.gef.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class InternalGEFPlugin extends AbstractUIPlugin {

	private static BundleContext context;
	private static AbstractUIPlugin singleton = new InternalGEFPlugin();

	public InternalGEFPlugin() {
		singleton = this;
	}

	public void start(BundleContext bc) throws Exception {
		super.start(bc);
		context = bc;
	}

	public static BundleContext getContext() {
		return context;
	}

	public static AbstractUIPlugin getDefault() {
		return singleton;
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		savePluginPreferences();
		super.stop(context);
	}

}
