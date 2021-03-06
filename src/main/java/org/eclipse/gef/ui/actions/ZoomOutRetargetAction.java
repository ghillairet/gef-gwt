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
package org.eclipse.gef.ui.actions;

import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.internal.InternalImages;
import org.eclipse.ui.actions.RetargetAction;

/**
 * @author danlee
 */
public class ZoomOutRetargetAction extends RetargetAction {

	/**
	 * Constructor for ZoomInRetargetAction
	 */
	public ZoomOutRetargetAction() {
		super(null, null);
		setText(GEFMessages.ZoomOut_Label);
		setId(GEFActionConstants.ZOOM_OUT);
		setToolTipText(GEFMessages.ZoomOut_Tooltip);
		setImageDescriptor(InternalImages.DESC_ZOOM_OUT);
		setActionDefinitionId(GEFActionConstants.ZOOM_OUT);
	}

}
