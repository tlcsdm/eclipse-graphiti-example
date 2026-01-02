/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.tlcsdm.eclipse.graphiti.demo.Activator;

/**
 * Preference page for LVGL UI Designer settings.
 */
public class LvglPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public LvglPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("LVGL UI Designer Preferences");
	}

	@Override
	public void init(IWorkbench workbench) {
		// Nothing to initialize
	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor licenseEditor = new StringFieldEditor(
				LvglPreferenceConstants.PREF_LICENSE_HEADER,
				"License Header:",
				getFieldEditorParent());
		licenseEditor.setEmptyStringAllowed(true);
		addFieldEditor(licenseEditor);
	}
}
