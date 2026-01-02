/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.feature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;

/**
 * Feature to resize an LVGL widget.
 */
public class ResizeLvglWidgetFeature extends DefaultResizeShapeFeature {

	public ResizeLvglWidgetFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		return bo instanceof LvglWidget;
	}

	@Override
	public void resizeShape(IResizeShapeContext context) {
		super.resizeShape(context);

		// Update the model with the new size
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);

		if (bo instanceof LvglWidget widget) {
			widget.setX(context.getX());
			widget.setY(context.getY());
			widget.setWidth(context.getWidth());
			widget.setHeight(context.getHeight());
		}
	}
}
