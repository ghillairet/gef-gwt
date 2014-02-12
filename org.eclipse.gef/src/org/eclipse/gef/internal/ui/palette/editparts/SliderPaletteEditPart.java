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
package org.eclipse.gef.internal.ui.palette.editparts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.editparts.PaletteAnimator;
import org.eclipse.gef.ui.palette.editparts.PaletteEditPart;
import org.eclipse.gef.ui.palette.editparts.PaletteToolbarLayout;

public class SliderPaletteEditPart extends PaletteEditPart {

	private PaletteAnimator controller;

	public SliderPaletteEditPart(PaletteRoot paletteRoot) {
		super(paletteRoot);
	}

	public IFigure createFigure() {
		Figure figure = new Figure();
		figure.setOpaque(false);
//		figure.setForegroundColor(ColorConstants.listForeground);
//		figure.setBackgroundColor(ColorConstants.black);
		return figure;
	}

	/**
	 * This method overrides super's functionality to do nothing.
	 * 
	 * @see PaletteEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#registerVisuals()
	 */
	@SuppressWarnings("unchecked")
	protected void registerVisuals() {
		super.registerVisuals();
		controller = new PaletteAnimator(
				((PaletteViewer) getViewer()).getPaletteViewerPreferences());
		getViewer().getEditPartRegistry().put(PaletteAnimator.class, controller);
		ToolbarLayout layout = new PaletteToolbarLayout();
		getFigure().setLayoutManager(layout);
		getFigure().addLayoutListener(controller);
	}

}
