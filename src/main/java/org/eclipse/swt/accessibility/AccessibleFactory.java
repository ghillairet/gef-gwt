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
package org.eclipse.swt.accessibility;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.Callback;

class AccessibleFactory {
	// static final Hashtable Accessibles = new Hashtable(9);
	// static final Hashtable Factories = new Hashtable(9);
	static final String SWT_TYPE_PREFIX = "SWTAccessible"; //$NON-NLS-1$
	static final String CHILD_TYPENAME = "Child"; //$NON-NLS-1$
	static final String FACTORY_TYPENAME = "SWTFactory"; //$NON-NLS-1$
	static final int[] actionRoles = { ACC.ROLE_CHECKBUTTON, ACC.ROLE_COMBOBOX,
			ACC.ROLE_LINK, ACC.ROLE_MENUITEM, ACC.ROLE_PUSHBUTTON,
			ACC.ROLE_RADIOBUTTON, ACC.ROLE_SPLITBUTTON, ACC.ROLE_SPINBUTTON,
			ACC.ROLE_CHECKMENUITEM, ACC.ROLE_RADIOMENUITEM, };
	static final int[] editableTextRoles = { ACC.ROLE_TEXT, ACC.ROLE_COMBOBOX,
			ACC.ROLE_PARAGRAPH, ACC.ROLE_DOCUMENT, };
	static final int[] hypertextRoles = { ACC.ROLE_TEXT, ACC.ROLE_LINK,
			ACC.ROLE_PARAGRAPH, };
	static final int[] selectionRoles = { ACC.ROLE_LIST, ACC.ROLE_TABFOLDER,
			ACC.ROLE_TABLE, ACC.ROLE_TREE, };
	static final int[] textRoles = { ACC.ROLE_COMBOBOX, ACC.ROLE_LINK,
			ACC.ROLE_LABEL, ACC.ROLE_TEXT, ACC.ROLE_STATUSBAR,
			ACC.ROLE_PARAGRAPH, ACC.ROLE_DOCUMENT, };
	static final int[] tableRoles = { ACC.ROLE_TABLE, ACC.ROLE_TREE, };
	static final int[] valueRoles = { ACC.ROLE_SCROLLBAR, ACC.ROLE_SPINBUTTON,
			ACC.ROLE_PROGRESSBAR, };

	static private Callback newCallback(Object object, String method,
			int argCount) {
		Callback callback = new Callback(object, method, argCount);
		if (callback.getAddress() == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
		return callback;
	}

	static String getTypeName(long /* int */type) {
		return null;
	}

	static long /* int */getParentType(long /* int */widgetType) {
		return 0;
	}

	static long /* int */atkObjectFactory_create_accessible(long /*
																 * int
																 */widget) {
		return 0;
	}

	static AccessibleObject createChildAccessible(Accessible accessible,
			int childId) {
		return null;
	}

	static void createAccessible(Accessible accessible) {
	}

	static long /* int */getType(String widgetTypeName, Accessible accessible,
			long /* int */parentType, int childId) {
		return 0;
	}

	static long /* int */gTypeInfo_base_init_factory(long /* int */klass) {
		return 0;
	}

	static long /* int */gTypeInfo_base_init_type(long /* int */klass) {
		return 0;
	}

	static long /* int */initActionIfaceCB(long /* int */iface) {
		return 0;
	}

	static long /* int */initComponentIfaceCB(long /* int */iface) {
		return 0;
	}

	static long /* int */initEditableTextIfaceCB(long /* int */iface) {
		return 0;
	}

	static long /* int */initHypertextIfaceCB(long /* int */iface) {
		return 0;
	}

	static long /* int */initSelectionIfaceCB(long /* int */iface) {
		return 0;
	}

	static long /* int */initTableIfaceCB(long /* int */iface) {
		return 0;
	}

	static long /* int */initTextIfaceCB(long /* int */iface) {
		return 0;
	}

	static long /* int */initValueIfaceCB(long /* int */iface) {
		return 0;
	}

	static void registerAccessible(Accessible accessible) {
	}

	static void unregisterAccessible(Accessible accessible) {
	}
}
