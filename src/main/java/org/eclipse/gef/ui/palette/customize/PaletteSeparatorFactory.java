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
package org.eclipse.gef.ui.palette.customize;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.ui.palette.PaletteMessages;
import org.eclipse.swt.widgets.Shell;

/**
 * Factory to create {@link org.eclipse.gef.palette.PaletteSeparator
 * PaletteSeparators}.
 * 
 * @author Pratik Shah
 */
public class PaletteSeparatorFactory extends PaletteEntryFactory {

	/**
	 * Constructor
	 */
	public PaletteSeparatorFactory() {
		setLabel(PaletteMessages.MODEL_TYPE_SEPARATOR);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.customize.PaletteEntryFactory#createNewEntry(Shell)
	 */
	public PaletteEntry createNewEntry(Shell shell) {
		PaletteSeparator separator = new PaletteSeparator();
		return separator;
	}

	/**
	 * @see org.eclipse.gef.ui.palette.customize.PaletteEntryFactory#determineTypeForNewEntry(org.eclipse.gef.palette.PaletteEntry)
	 */
	protected Object determineTypeForNewEntry(PaletteEntry selected) {
		return PaletteSeparator.PALETTE_TYPE_SEPARATOR;
	}

}
