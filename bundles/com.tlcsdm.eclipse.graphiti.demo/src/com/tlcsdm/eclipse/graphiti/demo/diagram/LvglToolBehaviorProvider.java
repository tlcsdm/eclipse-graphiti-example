/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;

import com.tlcsdm.eclipse.graphiti.demo.editor.GenerateLvglCodeFeature;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;

/**
 * Tool behavior provider for LVGL UI diagrams.
 * Customizes the palette, context buttons, and other UI behaviors.
 */
public class LvglToolBehaviorProvider extends DefaultToolBehaviorProvider {

	public LvglToolBehaviorProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);
		PictogramElement pe = context.getPictogramElement();
		setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE | CONTEXT_BUTTON_UPDATE);
		return data;
	}

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		// Create "Generate LVGL Code" menu entry
		ContextMenuEntry generateCodeEntry = new ContextMenuEntry(
				new GenerateLvglCodeFeature(getFeatureProvider()), context);
		generateCodeEntry.setText("Generate LVGL Code");
		generateCodeEntry.setDescription("Generate LVGL C Code from Diagram");

		// Return the context menu entries
		return new IContextMenuEntry[] { generateCodeEntry };
	}

	@Override
	public Object getToolTip(org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm ga) {
		PictogramElement pe = ga.getPictogramElement();
		IFeatureProvider fp = getFeatureProvider();
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof LvglWidget widget) {
			StringBuilder sb = new StringBuilder();
			sb.append("Name: ").append(widget.getName()).append("\n");
			sb.append("Type: ").append(widget.getWidgetType().getDisplayName()).append("\n");
			sb.append("Position: (").append(widget.getX()).append(", ").append(widget.getY()).append(")\n");
			sb.append("Size: ").append(widget.getWidth()).append(" x ").append(widget.getHeight());
			if (widget.getText() != null && !widget.getText().isEmpty()) {
				sb.append("\nText: ").append(widget.getText());
			}
			return sb.toString();
		}
		return super.getToolTip(ga);
	}

	@Override
	public boolean equalsBusinessObjects(Object o1, Object o2) {
		if (o1 instanceof LvglWidget && o2 instanceof LvglWidget) {
			return ((LvglWidget) o1).getName().equals(((LvglWidget) o2).getName());
		}
		return super.equalsBusinessObjects(o1, o2);
	}
}
