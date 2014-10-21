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

import org.eclipse.gef.internal.Internal;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Cursor;

public class BrowserCursorSupportDefaultImpl implements BrowserCursorSupport {

	@Override
	public Cursor createTreeAdd() {
		String sourceName = "icons/Tree_Add_Transparent.gif";
		ImageDescriptor id = ImageDescriptor.createFromFile(Internal.class,
				sourceName);
		return new Cursor(id.createImage());
	}

	@Override
	public Cursor createPlugNot() {
		String sourceName = "icons/plugmasknot.gif";
		ImageDescriptor id = ImageDescriptor.createFromFile(Internal.class,
				sourceName);
		return new Cursor(id.createImage());
	}

	@Override
	public Cursor createPlug() {
		String sourceName = "icons/plugmask.gif";
		ImageDescriptor id = ImageDescriptor.createFromFile(Internal.class,
				sourceName);
		return new Cursor(id.createImage());
	}

}
