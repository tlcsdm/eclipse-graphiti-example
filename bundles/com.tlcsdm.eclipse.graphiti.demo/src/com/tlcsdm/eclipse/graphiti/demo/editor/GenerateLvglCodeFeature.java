/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.tlcsdm.eclipse.graphiti.demo.Activator;
import com.tlcsdm.eclipse.graphiti.demo.generator.LvglCodeGenerator;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.preferences.LvglPreferenceConstants;
import com.tlcsdm.eclipse.graphiti.demo.util.ConsoleUtil;

/**
 * Custom feature for generating LVGL C code from the diagram.
 * Can be invoked from the context menu.
 */
public class GenerateLvglCodeFeature extends AbstractCustomFeature {

	/** Feature ID for generate LVGL code action */
	public static final String FEATURE_ID = "generateLvglCode";

	public GenerateLvglCodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Generate LVGL Code";
	}

	@Override
	public String getDescription() {
		return "Generate LVGL C Code from Diagram";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	@Override
	public void execute(ICustomContext context) {
		// Get the screen and file from the active editor
		LvglScreen screen = null;
		IFile graphxmlFile = null;

		// Try to get from multi-page editor first
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		
		if (activeEditor instanceof LvglMultiPageEditor) {
			LvglMultiPageEditor multiPageEditor = (LvglMultiPageEditor) activeEditor;
			screen = multiPageEditor.getScreen();
			graphxmlFile = multiPageEditor.getDiagramFile();
		} else if (activeEditor instanceof DiagramEditor) {
			// Try to get from LvglDiagramEditor
			DiagramEditor diagramEditor = (DiagramEditor) activeEditor;
			if (diagramEditor instanceof LvglDiagramEditor) {
				LvglDiagramEditor lvglEditor = (LvglDiagramEditor) diagramEditor;
				screen = lvglEditor.getScreen();
				graphxmlFile = lvglEditor.getDiagramFile();
			}
		}

		if (screen == null) {
			ConsoleUtil.printError("Could not find LVGL screen data in the editor.");
			return;
		}

		if (screen.getWidgets().isEmpty()) {
			ConsoleUtil.println("Warning: The screen is empty. Please add some widgets first.");
			return;
		}

		try {
			if (graphxmlFile == null) {
				ConsoleUtil.printError("Could not determine the .graphxml file location.");
				return;
			}

			IContainer parentFolder = graphxmlFile.getParent();
			String baseName = graphxmlFile.getName();
			if (baseName.endsWith(".graphxml")) {
				baseName = baseName.substring(0, baseName.length() - 9);
			}

			// Get license header from preferences
			String licenseHeader = Activator.getDefault().getPreferenceStore()
					.getString(LvglPreferenceConstants.PREF_LICENSE_HEADER);

			// Generate code
			LvglCodeGenerator generator = new LvglCodeGenerator(screen, licenseHeader);
			String headerCode = generator.generateHeader();
			String sourceCode = generator.generateSource();

			// Write header file
			IFile headerFile = parentFolder.getFile(new Path(baseName + ".h"));
			writeFile(headerFile, headerCode);

			// Write source file
			IFile sourceFile = parentFolder.getFile(new Path(baseName + ".c"));
			writeFile(sourceFile, sourceCode);

			// Refresh parent folder
			parentFolder.refreshLocal(1, new NullProgressMonitor());

			// Log to console (no dialog as per requirements)
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

	@Override
	public boolean hasDoneChanges() {
		return false;
	}
}
