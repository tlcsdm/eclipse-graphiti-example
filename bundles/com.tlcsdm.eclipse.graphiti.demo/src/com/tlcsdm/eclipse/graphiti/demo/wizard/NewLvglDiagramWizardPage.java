/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * Wizard page for creating a new LVGL UI diagram file.
 */
public class NewLvglDiagramWizardPage extends WizardNewFileCreationPage {

	private static final String DEFAULT_FILENAME = "new_screen";

	public NewLvglDiagramWizardPage(IStructuredSelection selection) {
		super("newLvglDiagramPage", selection);
		setTitle("New LVGL UI Diagram");
		setDescription("Create a new LVGL UI diagram file (.graphxml)");
		setFileName(DEFAULT_FILENAME + "." + NewLvglDiagramWizard.FILE_EXTENSION);
	}

	@Override
	protected boolean validatePage() {
		if (super.validatePage()) {
			String fileName = getFileName();
			if (!fileName.endsWith("." + NewLvglDiagramWizard.FILE_EXTENSION)) {
				setFileName(fileName + "." + NewLvglDiagramWizard.FILE_EXTENSION);
			}
			return true;
		}
		return false;
	}
}
