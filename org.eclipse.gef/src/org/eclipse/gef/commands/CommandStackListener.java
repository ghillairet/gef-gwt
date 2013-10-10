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
package org.eclipse.gef.commands;

import java.util.EventObject;

/**
 * A CommandStackListener is notified whenever the {@link CommandStack}'s state
 * has changed.
 */
public interface CommandStackListener {

	/**
	 * Called when the {@link CommandStack}'s state has changed.
	 * 
	 * @param event
	 *            the event
	 */
	void commandStackChanged(EventObject event);

}
