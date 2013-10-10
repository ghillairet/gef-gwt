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
package org.eclipse.gef.ui.properties;

import java.util.EventObject;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetEntry;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.ForwardUndoCompoundCommand;

/**
 * <p>
 * UndoablePropertySheetEntry provides undo support for changes made to
 * IPropertySources by the PropertySheetViewer. Clients can construct a
 * {@link org.eclipse.ui.views.properties.PropertySheetPage} and use this class
 * as the root entry. All changes made to property sources displayed on that
 * page will be done using the provided command stack.
 * <p>
 * <b>NOTE:</b> If you intend to use an IPropertySourceProvider for a
 * PropertySheetPage whose root entry is an instance of of
 * UndoablePropertySheetEntry, you should set the IPropertySourceProvider on
 * that root entry, rather than the PropertySheetPage.
 */
public class UndoablePropertySheetEntry extends PropertySheetEntry {

	private CommandStackListener commandStackListener;

	private CommandStack commandStack;

	/**
	 * Constructs a non-root, i.e. child entry, which may obtain the command
	 * stack from its parent.
	 * 
	 * @since 3.1
	 */
	private UndoablePropertySheetEntry() {
	}

	/**
	 * Constructs the root entry using the given command stack.
	 * 
	 * @param commandStack
	 *            the command stack to use
	 * @since 3.1
	 */
	public UndoablePropertySheetEntry(CommandStack commandStack) {
		this.commandStack = commandStack;
		this.commandStackListener = new CommandStackListener() {
			public void commandStackChanged(EventObject e) {
				refreshFromRoot();
			}
		};
		this.commandStack.addCommandStackListener(commandStackListener);
	}

	/**
	 * @see org.eclipse.ui.views.properties.PropertySheetEntry#createChildEntry()
	 */
	protected PropertySheetEntry createChildEntry() {
		return new UndoablePropertySheetEntry();
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySheetEntry#dispose()
	 */
	public void dispose() {
		if (commandStack != null)
			commandStack.removeCommandStackListener(commandStackListener);
		super.dispose();
	}

	/**
	 * Returns the {@link CommandStack} that is used by this entry. It is
	 * obtained from the parent in case the entry is not a root entry.
	 * 
	 * @return the {@link CommandStack} to be used.
	 * @since 3.7
	 */
	protected CommandStack getCommandStack() {
		// only the root has, and is listening too, the command stack
		if (getParent() != null)
			return ((UndoablePropertySheetEntry) getParent()).getCommandStack();
		return commandStack;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySheetEntry#resetPropertyValue()
	 */
	public void resetPropertyValue() {
		CompoundCommand cc = new CompoundCommand();
		if (getParent() == null)
			// root does not have a default value
			return;

		// Use our parent's values to reset our values.
		boolean change = false;
		Object[] objects = getParent().getValues();
		for (int i = 0; i < objects.length; i++) {
			IPropertySource source = getPropertySource(objects[i]);
			if (source.isPropertySet(getDescriptor().getId())) {
				SetPropertyValueCommand restoreCmd = new SetPropertyValueCommand(
						getDescriptor().getDisplayName(), source,
						getDescriptor().getId(),
						SetPropertyValueCommand.DEFAULT_VALUE);
				cc.add(restoreCmd);
				change = true;
			}
		}
		if (change) {
			getCommandStack().execute(cc);
			refreshFromRoot();
		}
	}

	/**
	 * @see PropertySheetEntry#valueChanged(PropertySheetEntry)
	 */
	protected void valueChanged(PropertySheetEntry child) {
		valueChanged((UndoablePropertySheetEntry) child,
				new ForwardUndoCompoundCommand());
	}

	private void valueChanged(UndoablePropertySheetEntry child,
			CompoundCommand command) {
		CompoundCommand cc = new CompoundCommand();
		command.add(cc);

		SetPropertyValueCommand setCommand;
		for (int i = 0; i < getValues().length; i++) {
			setCommand = new SetPropertyValueCommand(child.getDisplayName(),
					getPropertySource(getValues()[i]), child.getDescriptor()
							.getId(), child.getValues()[i]);
			cc.add(setCommand);
		}

		// inform our parent
		if (getParent() != null)
			((UndoablePropertySheetEntry) getParent()).valueChanged(this,
					command);
		else {
			// I am the root entry
			commandStack.execute(command);
		}
	}
}
