/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an LVGL screen containing UI widgets.
 * This is the root container for all widgets in the diagram.
 */
public class LvglScreen extends ModelElement {

	private static final long serialVersionUID = 1L;

	private String name = "screen";
	private int width = 480;
	private int height = 320;
	private int bgColor = 0xFFFFFF;
	private final List<LvglWidget> widgets = new ArrayList<>();

	public LvglScreen() {
		// Default constructor
	}

	public LvglScreen(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		firePropertyChange(PROPERTY_NAME, oldValue, name);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		int oldValue = this.width;
		this.width = width;
		firePropertyChange("width", oldValue, width);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		int oldValue = this.height;
		this.height = height;
		firePropertyChange("height", oldValue, height);
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		int oldValue = this.bgColor;
		this.bgColor = bgColor;
		firePropertyChange("bgColor", oldValue, bgColor);
	}

	public List<LvglWidget> getWidgets() {
		return widgets;
	}

	public void addWidget(LvglWidget widget) {
		widgets.add(widget);
		firePropertyChange(PROPERTY_ADD, null, widget);
	}

	public void insertWidget(int index, LvglWidget widget) {
		widgets.add(index, widget);
		firePropertyChange(PROPERTY_ADD, null, widget);
	}

	public void removeWidget(LvglWidget widget) {
		widgets.remove(widget);
		firePropertyChange(PROPERTY_REMOVE, widget, null);
	}

	/**
	 * Finds a widget by name.
	 *
	 * @param name the widget name
	 * @return the widget, or null if not found
	 */
	public LvglWidget findWidgetByName(String name) {
		for (LvglWidget widget : widgets) {
			if (widget.getName().equals(name)) {
				return widget;
			}
			// Check nested children
			LvglWidget found = findWidgetByNameRecursive(widget, name);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private LvglWidget findWidgetByNameRecursive(LvglWidget parent, String name) {
		for (LvglWidget child : parent.getChildren()) {
			if (child.getName().equals(name)) {
				return child;
			}
			LvglWidget found = findWidgetByNameRecursive(child, name);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	/**
	 * Generates a variable name for this screen.
	 *
	 * @return the variable name
	 */
	public String getVariableName() {
		String varName = name.replaceAll("[^a-zA-Z0-9_]", "_");
		if (!varName.isEmpty() && Character.isDigit(varName.charAt(0))) {
			varName = "_" + varName;
		}
		if (varName.isEmpty()) {
			varName = "_screen";
		}
		return varName;
	}
}
