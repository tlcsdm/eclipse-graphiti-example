/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.feature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;

/**
 * Feature to enable direct editing of widget text.
 */
public class DirectEditLvglWidgetFeature extends AbstractDirectEditingFeature {

	public DirectEditLvglWidgetFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		// Allow direct editing only for text graphics algorithms
		if (bo instanceof LvglWidget && ga instanceof Text) {
			return true;
		}

		return false;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo instanceof LvglWidget widget) {
			return widget.getText();
		}

		return "";
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		// Accept any string value
		return null;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo instanceof LvglWidget widget) {
			widget.setText(value);
		}

		// Update the graphics algorithm text
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		if (ga instanceof Text text) {
			text.setValue(value);
		}

		// Trigger an update to refresh the shape
		updatePictogramElement(((Shape) pe).getContainer());
	}
}
