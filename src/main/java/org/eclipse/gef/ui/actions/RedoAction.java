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

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import com.ibm.icu.text.MessageFormat;

/**
 * An action to redo the last command.
 */
public class RedoAction extends StackAction {

	/**
	 * Creates a <code>RedoAction</code> and associates it with the given
	 * workbech part.
	 * 
	 * @param part
	 *            The workbench part this action is associated with.
	 */
	public RedoAction(IWorkbenchPart part) {
		super(part);
	}

	/**
	 * Creates a <code>RedoAction</code> and associates it with the given
	 * editor.
	 * 
	 * @param editor
	 *            The editor this action is associated with.
	 */
	public RedoAction(IEditorPart editor) {
		super(editor);
	}

	/**
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return getCommandStack().canRedo();
	}

	/**
	 * Initializes this actions text and images.
	 */
	protected void init() {
		super.init();
		setToolTipText(MessageFormat.format(GEFMessages.RedoAction_Tooltip,
				new Object[] { "" }).trim()); //$NON-NLS-1$
		setText(MessageFormat.format(GEFMessages.RedoAction_Label,
				new Object[] { "" }).trim() //$NON-NLS-1$
		);
		setId(ActionFactory.REDO.getId());

		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO_DISABLED));
	}

	/**
	 * Refreshes this action's text to use the last undone command's label.
	 */
	protected void refresh() {
		Command redoCmd = getCommandStack().getRedoCommand();
		setToolTipText(MessageFormat.format(GEFMessages.RedoAction_Tooltip,
				new Object[] { getLabelForCommand(redoCmd) }).trim());
		setText(MessageFormat.format(GEFMessages.RedoAction_Label,
				new Object[] { getLabelForCommand(redoCmd) }).trim());
		super.refresh();
	}

	/**
	 * Redoes the last command.
	 */
	public void run() {
		getCommandStack().redo();
	}

}
