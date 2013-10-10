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

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;

import com.google.gwt.core.client.GWT;

public class BrowserCursorSupportIEImpl implements BrowserCursorSupport {

	@Override
	public Cursor createTreeAdd() {
		String url = GWT.getModuleBaseURL()
				+ "geficons/Tree_Add_Transparent.cur";
		return new Cursor(new Image(null, (Object) url));
	}

	@Override
	public Cursor createPlugNot() {
		String url = GWT.getModuleBaseURL() + "geficons/plugnot.cur";
		return new Cursor(new Image(null, (Object) url));
	}

	@Override
	public Cursor createPlug() {
		String url = GWT.getModuleBaseURL() + "geficons/plug.cur";
		return new Cursor(new Image(null, (Object) url));
	}

}
