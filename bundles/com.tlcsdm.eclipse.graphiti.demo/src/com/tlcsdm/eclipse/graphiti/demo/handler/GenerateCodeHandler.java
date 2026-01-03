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
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.tlcsdm.eclipse.graphiti.demo.Activator;
import com.tlcsdm.eclipse.graphiti.demo.editor.LvglDiagramEditor;
import com.tlcsdm.eclipse.graphiti.demo.editor.LvglMultiPageEditor;
import com.tlcsdm.eclipse.graphiti.demo.generator.LvglCodeGenerator;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.preferences.LvglPreferenceConstants;
import com.tlcsdm.eclipse.graphiti.demo.util.ConsoleUtil;

/**
 * Handler for the "Generate C Code" command.
 * Generates LVGL C code from the current diagram editor.
 * Output is shown in the Eclipse RCP console, not in a dialog.
 */
public class GenerateCodeHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);

		// Handle multi-page editor
		if (editor instanceof LvglMultiPageEditor) {
			LvglMultiPageEditor multiPageEditor = (LvglMultiPageEditor) editor;
			LvglScreen screen = multiPageEditor.getScreen();
			IFile diagramFile = multiPageEditor.getDiagramFile();

			if (screen == null) {
				ConsoleUtil.printError("Could not find LVGL screen data in the diagram.");
				return null;
			}

			generateCode(screen, diagramFile);
			return null;
		}

		// Handle direct diagram editor (for backward compatibility)
		if (editor instanceof DiagramEditor) {
			DiagramEditor diagramEditor = (DiagramEditor) editor;
			Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();

			if (diagram == null) {
				ConsoleUtil.printError("No diagram found in the editor.");
				return null;
			}

			// Get the LvglScreen from the diagram
			Object bo = diagramEditor.getDiagramTypeProvider().getFeatureProvider()
					.getBusinessObjectForPictogramElement(diagram);

			if (!(bo instanceof LvglScreen)) {
				ConsoleUtil.printError("Could not find LVGL screen data in the diagram.");
				return null;
			}

			LvglScreen screen = (LvglScreen) bo;
			IFile diagramFile = getDiagramFile(diagramEditor);

			generateCode(screen, diagramFile);
			return null;
		}

		ConsoleUtil.printError("Please open an LVGL UI diagram first.");
		return null;
	}

	private IFile getDiagramFile(DiagramEditor editor) {
		if (editor instanceof LvglDiagramEditor) {
			return ((LvglDiagramEditor) editor).getDiagramFile();
		}
		// Try adapter approach
		Object adapted = editor.getAdapter(IFile.class);
		if (adapted instanceof IFile) {
			return (IFile) adapted;
		}
		// Try getting from editor input
		if (editor.getEditorInput() != null) {
			Object file = editor.getEditorInput().getAdapter(IFile.class);
			if (file instanceof IFile) {
				return (IFile) file;
			}
		}
		return null;
	}

	private void generateCode(LvglScreen screen, IFile diagramFile) {
		try {
			if (diagramFile == null) {
				ConsoleUtil.printError("Could not determine the diagram file location.");
				return;
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
			IContainer parentFolder = diagramFile.getParent();

			// Write header file
			IFile headerFile = parentFolder.getFile(new Path(headerFileName));
			writeFile(headerFile, headerContent);

			// Write source file
			IFile sourceFile = parentFolder.getFile(new Path(sourceFileName));
			writeFile(sourceFile, sourceContent);

			// Refresh the parent folder
			parentFolder.refreshLocal(1, new NullProgressMonitor());

			// Log to console only (no dialog as per requirements)
			ConsoleUtil.println("Generated LVGL C code:");
			ConsoleUtil.println("  - " + headerFile.getFullPath().toString());
			ConsoleUtil.println("  - " + sourceFile.getFullPath().toString());

		} catch (Exception e) {
			ConsoleUtil.printError("Failed to generate code: " + e.getMessage());
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
