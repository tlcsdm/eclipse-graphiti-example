/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

import com.tlcsdm.eclipse.graphiti.demo.feature.AddLvglWidgetFeature;
import com.tlcsdm.eclipse.graphiti.demo.feature.CreateLvglWidgetFeature;
import com.tlcsdm.eclipse.graphiti.demo.feature.DeleteLvglWidgetFeature;
import com.tlcsdm.eclipse.graphiti.demo.feature.DirectEditLvglWidgetFeature;
import com.tlcsdm.eclipse.graphiti.demo.feature.LayoutLvglWidgetFeature;
import com.tlcsdm.eclipse.graphiti.demo.feature.MoveLvglWidgetFeature;
import com.tlcsdm.eclipse.graphiti.demo.feature.ResizeLvglWidgetFeature;
import com.tlcsdm.eclipse.graphiti.demo.feature.UpdateLvglWidgetFeature;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget.WidgetType;

/**
 * Feature provider for LVGL UI diagrams.
 * Manages all the features (create, add, delete, move, resize, etc.) for widgets.
 */
public class LvglFeatureProvider extends DefaultFeatureProvider {

	public LvglFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		// Create features for commonly used widgets
		return new ICreateFeature[] {
			new CreateLvglWidgetFeature(this, WidgetType.BUTTON),
			new CreateLvglWidgetFeature(this, WidgetType.LABEL),
			new CreateLvglWidgetFeature(this, WidgetType.SLIDER),
			new CreateLvglWidgetFeature(this, WidgetType.SWITCH),
			new CreateLvglWidgetFeature(this, WidgetType.CHECKBOX),
			new CreateLvglWidgetFeature(this, WidgetType.DROPDOWN),
			new CreateLvglWidgetFeature(this, WidgetType.TEXTAREA),
			new CreateLvglWidgetFeature(this, WidgetType.IMAGE),
			new CreateLvglWidgetFeature(this, WidgetType.ARC),
			new CreateLvglWidgetFeature(this, WidgetType.BAR),
			new CreateLvglWidgetFeature(this, WidgetType.CONTAINER),
			new CreateLvglWidgetFeature(this, WidgetType.CHART),
			new CreateLvglWidgetFeature(this, WidgetType.TABLE),
			new CreateLvglWidgetFeature(this, WidgetType.LIST),
			new CreateLvglWidgetFeature(this, WidgetType.TABVIEW),
			new CreateLvglWidgetFeature(this, WidgetType.SPINNER),
			new CreateLvglWidgetFeature(this, WidgetType.LED),
			new CreateLvglWidgetFeature(this, WidgetType.CALENDAR),
			new CreateLvglWidgetFeature(this, WidgetType.KEYBOARD),
			new CreateLvglWidgetFeature(this, WidgetType.ROLLER),
			new CreateLvglWidgetFeature(this, WidgetType.MSGBOX),
			new CreateLvglWidgetFeature(this, WidgetType.WIN)
		};
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		if (context.getNewObject() instanceof LvglWidget) {
			return new AddLvglWidgetFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (bo instanceof LvglWidget) {
				return new UpdateLvglWidgetFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (bo instanceof LvglWidget) {
				return new LayoutLvglWidgetFeature(this);
			}
		}
		return super.getLayoutFeature(context);
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		if (bo instanceof LvglWidget) {
			return new MoveLvglWidgetFeature(this);
		}
		return super.getMoveShapeFeature(context);
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		if (bo instanceof LvglWidget) {
			return new ResizeLvglWidgetFeature(this);
		}
		return super.getResizeShapeFeature(context);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof LvglWidget) {
			return new DeleteLvglWidgetFeature(this);
		}
		return super.getDeleteFeature(context);
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof LvglWidget) {
			return new DirectEditLvglWidgetFeature(this);
		}
		return super.getDirectEditingFeature(context);
	}
}
