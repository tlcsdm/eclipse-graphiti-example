/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.feature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;

/**
 * Feature to move an LVGL widget within the diagram.
 */
public class MoveLvglWidgetFeature extends DefaultMoveShapeFeature {

	public MoveLvglWidgetFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		return bo instanceof LvglWidget;
	}

	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		super.postMoveShape(context);

		// Update the model with the new position
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);

		if (bo instanceof LvglWidget widget) {
			widget.setX(context.getX());
			widget.setY(context.getY());
		}
	}
}
