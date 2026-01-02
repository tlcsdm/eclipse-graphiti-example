/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.tlcsdm.eclipse.graphiti.demo.Activator;
import com.tlcsdm.eclipse.graphiti.demo.generator.LvglCodeGenerator;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglXmlSerializer;
import com.tlcsdm.eclipse.graphiti.demo.preferences.LvglPreferenceConstants;
import com.tlcsdm.eclipse.graphiti.demo.util.ConsoleUtil;

/**
 * Handler for generating code from a .graphxml file in the Project Explorer.
 */
public class GenerateCodeFromFileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object firstElement = structuredSelection.getFirstElement();

		if (!(firstElement instanceof IFile)) {
			MessageDialog.openError(
					HandlerUtil.getActiveShell(event),
					"Error",
					"Please select a .graphxml file.");
			return null;
		}

		IFile file = (IFile) firstElement;
		if (!file.getName().endsWith(".graphxml")) {
			MessageDialog.openError(
					HandlerUtil.getActiveShell(event),
					"Error",
					"Please select a .graphxml file.");
			return null;
		}

		generateCode(file, event);

		return null;
	}

	private void generateCode(IFile diagramFile, ExecutionEvent event) {
		try {
			// Load the screen from the file
			LvglXmlSerializer serializer = new LvglXmlSerializer();
			LvglScreen screen;
			try (InputStream inputStream = diagramFile.getContents()) {
				screen = serializer.load(inputStream);
			}

			// Get license header from preferences
			String licenseHeader = Activator.getDefault().getPreferenceStore()
					.getString(LvglPreferenceConstants.PREF_LICENSE_HEADER);

			// Generate code
			LvglCodeGenerator generator = new LvglCodeGenerator(screen, licenseHeader);
			String headerContent = generator.generateHeader();
			String sourceContent = generator.generateSource();

			// Determine output file names
			String baseName = diagramFile.getName();
			if (baseName.contains(".")) {
				baseName = baseName.substring(0, baseName.lastIndexOf('.'));
			}
			String headerFileName = baseName + ".h";
			String sourceFileName = baseName + ".c";

			// Get parent folder
			IFolder parentFolder = (IFolder) diagramFile.getParent();

			// Write header file
			IFile headerFile = parentFolder.getFile(headerFileName);
			writeFile(headerFile, headerContent);

			// Write source file
			IFile sourceFile = parentFolder.getFile(sourceFileName);
			writeFile(sourceFile, sourceContent);

			// Refresh the parent folder
			parentFolder.refreshLocal(1, new NullProgressMonitor());

			// Log to console
			ConsoleUtil.println("Generated LVGL C code:");
			ConsoleUtil.println("  - " + headerFile.getFullPath().toString());
			ConsoleUtil.println("  - " + sourceFile.getFullPath().toString());

			MessageDialog.openInformation(
					HandlerUtil.getActiveShell(event),
					"Code Generated",
					"Successfully generated LVGL C code:\n\n" +
							"- " + headerFileName + "\n" +
							"- " + sourceFileName);

		} catch (Exception e) {
			MessageDialog.openError(
					HandlerUtil.getActiveShell(event),
					"Error",
					"Failed to generate code: " + e.getMessage());
		}
	}

	private void writeFile(IFile file, String content) throws Exception {
		InputStream source = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		if (file.exists()) {
			file.setContents(source, true, true, new NullProgressMonitor());
		} else {
			file.create(source, true, new NullProgressMonitor());
		}
	}
}
