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
 * A Listener interface for receiving {@link ActionEvent ActionEvents}.
 */
public interface ActionListener {

	/**
	 * Called when the action occurs.
	 * 
	 * @param event
	 *            The event
	 */
	void actionPerformed(ActionEvent event);

}
