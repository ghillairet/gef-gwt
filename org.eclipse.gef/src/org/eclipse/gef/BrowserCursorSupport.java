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

import com.google.gwt.core.client.GWT;

public interface BrowserCursorSupport {
	BrowserCursorSupport INSTANCE = GWT.create(BrowserCursorSupport.class);

	Cursor createTreeAdd();

	Cursor createPlugNot();

	Cursor createPlug();
}
