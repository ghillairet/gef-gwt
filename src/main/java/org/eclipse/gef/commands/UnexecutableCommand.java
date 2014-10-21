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

/**
 * A Command which can never be executed
 */
public final class UnexecutableCommand extends Command {

	/**
	 * The singleton instance
	 */
	public static final UnexecutableCommand INSTANCE = new UnexecutableCommand();

	private UnexecutableCommand() {
	}

	/**
	 * @return <code>false</code>
	 */
	public boolean canExecute() {
		return false;
	}

	/**
	 * @return <code>false</code>
	 */
	public boolean canUndo() {
		return false;
	}

}
