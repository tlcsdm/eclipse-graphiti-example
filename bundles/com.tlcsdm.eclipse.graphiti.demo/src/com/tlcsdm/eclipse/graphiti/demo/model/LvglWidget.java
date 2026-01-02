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
 * Represents an LVGL widget with various properties.
 * Supports all major LVGL widget types and layout configurations.
 */
public class LvglWidget extends ModelElement {

	private static final long serialVersionUID = 1L;

	/**
	 * Enum of supported LVGL widget types.
	 */
	public enum WidgetType {
		BUTTON("lv_btn", "Button"),
		LABEL("lv_label", "Label"),
		SLIDER("lv_slider", "Slider"),
		SWITCH("lv_switch", "Switch"),
		CHECKBOX("lv_checkbox", "Checkbox"),
		DROPDOWN("lv_dropdown", "Dropdown"),
		TEXTAREA("lv_textarea", "Textarea"),
		IMAGE("lv_img", "Image"),
		ARC("lv_arc", "Arc"),
		BAR("lv_bar", "Bar"),
		CONTAINER("lv_obj", "Container"),
		// Additional LVGL widgets from official documentation
		ANIMIMG("lv_animimg", "Animation Image"),
		ARCLABEL("lv_arclabel", "Arc Label"),
		BUTTONMATRIX("lv_btnmatrix", "Button Matrix"),
		CALENDAR("lv_calendar", "Calendar"),
		CANVAS("lv_canvas", "Canvas"),
		CHART("lv_chart", "Chart"),
		IMAGEBUTTON("lv_imgbtn", "Image Button"),
		KEYBOARD("lv_keyboard", "Keyboard"),
		LED("lv_led", "LED"),
		LINE("lv_line", "Line"),
		LIST("lv_list", "List"),
		MENU("lv_menu", "Menu"),
		MSGBOX("lv_msgbox", "Message Box"),
		ROLLER("lv_roller", "Roller"),
		SCALE("lv_scale", "Scale"),
		SPANGROUP("lv_spangroup", "Spangroup"),
		SPINBOX("lv_spinbox", "Spinbox"),
		SPINNER("lv_spinner", "Spinner"),
		TABLE("lv_table", "Table"),
		TABVIEW("lv_tabview", "Tab View"),
		TILEVIEW("lv_tileview", "Tile View"),
		WIN("lv_win", "Window");

		private final String lvglType;
		private final String displayName;

		WidgetType(String lvglType, String displayName) {
			this.lvglType = lvglType;
			this.displayName = displayName;
		}

		public String getLvglType() {
			return lvglType;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	/**
	 * Layout types supported by LVGL containers.
	 */
	public enum LayoutType {
		NONE("None", "LV_LAYOUT_NONE"),
		FLEX("Flex", "LV_LAYOUT_FLEX"),
		GRID("Grid", "LV_LAYOUT_GRID");

		private final String displayName;
		private final String lvglConstant;

		LayoutType(String displayName, String lvglConstant) {
			this.displayName = displayName;
			this.lvglConstant = lvglConstant;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getLvglConstant() {
			return lvglConstant;
		}
	}

	/**
	 * Flex flow direction for flex layout.
	 */
	public enum FlexFlow {
		ROW("Row", "LV_FLEX_FLOW_ROW"),
		COLUMN("Column", "LV_FLEX_FLOW_COLUMN"),
		ROW_WRAP("Row Wrap", "LV_FLEX_FLOW_ROW_WRAP"),
		COLUMN_WRAP("Column Wrap", "LV_FLEX_FLOW_COLUMN_WRAP"),
		ROW_REVERSE("Row Reverse", "LV_FLEX_FLOW_ROW_REVERSE"),
		COLUMN_REVERSE("Column Reverse", "LV_FLEX_FLOW_COLUMN_REVERSE");

		private final String displayName;
		private final String lvglConstant;

		FlexFlow(String displayName, String lvglConstant) {
			this.displayName = displayName;
			this.lvglConstant = lvglConstant;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getLvglConstant() {
			return lvglConstant;
		}
	}

	/**
	 * Flex alignment options.
	 */
	public enum FlexAlign {
		START("Start", "LV_FLEX_ALIGN_START"),
		END("End", "LV_FLEX_ALIGN_END"),
		CENTER("Center", "LV_FLEX_ALIGN_CENTER"),
		SPACE_EVENLY("Space Evenly", "LV_FLEX_ALIGN_SPACE_EVENLY"),
		SPACE_AROUND("Space Around", "LV_FLEX_ALIGN_SPACE_AROUND"),
		SPACE_BETWEEN("Space Between", "LV_FLEX_ALIGN_SPACE_BETWEEN");

		private final String displayName;
		private final String lvglConstant;

		FlexAlign(String displayName, String lvglConstant) {
			this.displayName = displayName;
			this.lvglConstant = lvglConstant;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getLvglConstant() {
			return lvglConstant;
		}
	}

	// Basic properties
	private String name = "widget";
	private WidgetType widgetType = WidgetType.BUTTON;
	private int x = 0;
	private int y = 0;
	private int width = 100;
	private int height = 40;
	private String text = "";
	private final List<LvglWidget> children = new ArrayList<>();
	private LvglWidget parent;

	// Style properties
	private int bgColor = 0xFFFFFF;
	private int textColor = 0x000000;
	private int borderWidth = 0;
	private int borderColor = 0x000000;
	private int radius = 0;

	// Image-specific properties
	private String imageSource = "";

	// Checkbox/Switch state
	private boolean checked = false;

	// Value-based properties (for Slider, Arc, Bar)
	private int value = 0;
	private int minValue = 0;
	private int maxValue = 100;

	// Table properties
	private int rowCount = 3;
	private int columnCount = 3;
	private String tableData = "";

	// Layout properties (for containers)
	private LayoutType layoutType = LayoutType.NONE;
	private FlexFlow flexFlow = FlexFlow.ROW;
	private FlexAlign flexMainAlign = FlexAlign.START;
	private FlexAlign flexCrossAlign = FlexAlign.START;
	private FlexAlign flexTrackAlign = FlexAlign.START;
	private int padRow = 0;
	private int padColumn = 0;

	public LvglWidget() {
		// Default constructor
	}

	public LvglWidget(String name, WidgetType widgetType) {
		this.name = name;
		this.widgetType = widgetType;
	}

	// Basic properties getters and setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		firePropertyChange(PROPERTY_NAME, oldValue, name);
	}

	public WidgetType getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(WidgetType widgetType) {
		WidgetType oldValue = this.widgetType;
		this.widgetType = widgetType;
		firePropertyChange("widgetType", oldValue, widgetType);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		int oldValue = this.x;
		this.x = x;
		firePropertyChange("x", oldValue, x);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		int oldValue = this.y;
		this.y = y;
		firePropertyChange("y", oldValue, y);
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		String oldValue = this.text;
		this.text = text;
		firePropertyChange("text", oldValue, text);
	}

	// Children management

	public List<LvglWidget> getChildren() {
		return children;
	}

	public void addChild(LvglWidget child) {
		children.add(child);
		child.setParent(this);
		firePropertyChange(PROPERTY_ADD, null, child);
	}

	public void removeChild(LvglWidget child) {
		children.remove(child);
		child.setParent(null);
		firePropertyChange(PROPERTY_REMOVE, child, null);
	}

	public LvglWidget getParent() {
		return parent;
	}

	public void setParent(LvglWidget parent) {
		this.parent = parent;
	}

	// Style properties getters and setters

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		int oldValue = this.bgColor;
		this.bgColor = bgColor;
		firePropertyChange("bgColor", oldValue, bgColor);
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		int oldValue = this.textColor;
		this.textColor = textColor;
		firePropertyChange("textColor", oldValue, textColor);
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		int oldValue = this.borderWidth;
		this.borderWidth = borderWidth;
		firePropertyChange("borderWidth", oldValue, borderWidth);
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		int oldValue = this.borderColor;
		this.borderColor = borderColor;
		firePropertyChange("borderColor", oldValue, borderColor);
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		int oldValue = this.radius;
		this.radius = radius;
		firePropertyChange("radius", oldValue, radius);
	}

	// Image properties

	public String getImageSource() {
		return imageSource;
	}

	public void setImageSource(String imageSource) {
		String oldValue = this.imageSource;
		this.imageSource = imageSource != null ? imageSource : "";
		firePropertyChange("imageSource", oldValue, this.imageSource);
	}

	// Checkbox/Switch state

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		boolean oldValue = this.checked;
		this.checked = checked;
		firePropertyChange("checked", oldValue, checked);
	}

	// Value-based properties

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		int oldValue = this.value;
		this.value = value;
		firePropertyChange("value", oldValue, value);
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		int oldValue = this.minValue;
		this.minValue = minValue;
		firePropertyChange("minValue", oldValue, minValue);
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		int oldValue = this.maxValue;
		this.maxValue = maxValue;
		firePropertyChange("maxValue", oldValue, maxValue);
	}

	// Table properties

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		int oldValue = this.rowCount;
		this.rowCount = rowCount;
		firePropertyChange("rowCount", oldValue, rowCount);
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		int oldValue = this.columnCount;
		this.columnCount = columnCount;
		firePropertyChange("columnCount", oldValue, columnCount);
	}

	public String getTableData() {
		return tableData;
	}

	public void setTableData(String tableData) {
		String oldValue = this.tableData;
		this.tableData = tableData != null ? tableData : "";
		firePropertyChange("tableData", oldValue, this.tableData);
	}

	// Layout properties

	public LayoutType getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(LayoutType layoutType) {
		LayoutType oldValue = this.layoutType;
		this.layoutType = layoutType != null ? layoutType : LayoutType.NONE;
		firePropertyChange("layoutType", oldValue, this.layoutType);
	}

	public FlexFlow getFlexFlow() {
		return flexFlow;
	}

	public void setFlexFlow(FlexFlow flexFlow) {
		FlexFlow oldValue = this.flexFlow;
		this.flexFlow = flexFlow != null ? flexFlow : FlexFlow.ROW;
		firePropertyChange("flexFlow", oldValue, this.flexFlow);
	}

	public FlexAlign getFlexMainAlign() {
		return flexMainAlign;
	}

	public void setFlexMainAlign(FlexAlign flexMainAlign) {
		FlexAlign oldValue = this.flexMainAlign;
		this.flexMainAlign = flexMainAlign != null ? flexMainAlign : FlexAlign.START;
		firePropertyChange("flexMainAlign", oldValue, this.flexMainAlign);
	}

	public FlexAlign getFlexCrossAlign() {
		return flexCrossAlign;
	}

	public void setFlexCrossAlign(FlexAlign flexCrossAlign) {
		FlexAlign oldValue = this.flexCrossAlign;
		this.flexCrossAlign = flexCrossAlign != null ? flexCrossAlign : FlexAlign.START;
		firePropertyChange("flexCrossAlign", oldValue, this.flexCrossAlign);
	}

	public FlexAlign getFlexTrackAlign() {
		return flexTrackAlign;
	}

	public void setFlexTrackAlign(FlexAlign flexTrackAlign) {
		FlexAlign oldValue = this.flexTrackAlign;
		this.flexTrackAlign = flexTrackAlign != null ? flexTrackAlign : FlexAlign.START;
		firePropertyChange("flexTrackAlign", oldValue, this.flexTrackAlign);
	}

	public int getPadRow() {
		return padRow;
	}

	public void setPadRow(int padRow) {
		int oldValue = this.padRow;
		this.padRow = padRow;
		firePropertyChange("padRow", oldValue, padRow);
	}

	public int getPadColumn() {
		return padColumn;
	}

	public void setPadColumn(int padColumn) {
		int oldValue = this.padColumn;
		this.padColumn = padColumn;
		firePropertyChange("padColumn", oldValue, padColumn);
	}

	/**
	 * Generate variable name for this widget in C code.
	 * Ensures the name is a valid C identifier.
	 *
	 * @return the C variable name
	 */
	public String getVariableName() {
		String varName = name.replaceAll("[^a-zA-Z0-9_]", "_");
		// C identifiers cannot start with a digit
		if (!varName.isEmpty() && Character.isDigit(varName.charAt(0))) {
			varName = "_" + varName;
		}
		// Handle empty name
		if (varName.isEmpty()) {
			varName = "_widget";
		}
		return varName;
	}

	/**
	 * Checks if this widget type can contain children.
	 *
	 * @return true if the widget can contain children
	 */
	public boolean isContainer() {
		return widgetType == WidgetType.CONTAINER
				|| widgetType == WidgetType.TABVIEW
				|| widgetType == WidgetType.TILEVIEW
				|| widgetType == WidgetType.LIST
				|| widgetType == WidgetType.MENU
				|| widgetType == WidgetType.WIN;
	}
}
