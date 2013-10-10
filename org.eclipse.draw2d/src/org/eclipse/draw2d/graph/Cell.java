/*******************************************************************************
 * Copyright (c) 2005, 2010, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gerhardt Informatics Kft. - GEFGWT port
 *******************************************************************************/

package org.eclipse.draw2d.graph;

public class Cell {

	public int index;

	public int rank;

	public Cell(int rank, int index) {
		this.rank = rank;
		this.index = index;
	}

}
