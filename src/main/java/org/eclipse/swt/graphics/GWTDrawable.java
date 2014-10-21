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
package org.eclipse.swt.graphics;

import com.google.gwt.canvas.dom.client.Context2d;

public interface GWTDrawable extends Drawable {
	public Context2d internal_getContext();
}
