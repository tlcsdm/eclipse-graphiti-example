/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.feature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;

/**
 * Feature to delete an LVGL widget from the diagram.
 */
public class DeleteLvglWidgetFeature extends DefaultDeleteFeature {

	public DeleteLvglWidgetFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		return bo instanceof LvglWidget;
	}

	@Override
	public void preDelete(IDeleteContext context) {
		super.preDelete(context);

		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo instanceof LvglWidget widget) {
			// Remove from parent widget or screen
			LvglWidget parent = widget.getParent();
			if (parent != null) {
				parent.removeChild(widget);
			}

			// Also try to remove from screen
			PictogramElement diagram = pe.eContainer() instanceof PictogramElement ? (PictogramElement) pe.eContainer() : null;
			if (diagram != null) {
				Object screenBo = getBusinessObjectForPictogramElement(diagram);
				if (screenBo instanceof LvglScreen screen) {
					screen.removeWidget(widget);
				}
			}
		}
	}
}
