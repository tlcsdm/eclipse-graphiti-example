/*******************************************************************************
 * Copyright (c) 2025 Tlcsdm. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 ******************************************************************************/
package com.tlcsdm.eclipse.graphiti.demo.generator;

import java.util.ArrayList;
import java.util.List;

import com.tlcsdm.eclipse.graphiti.demo.model.LvglScreen;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget.LayoutType;
import com.tlcsdm.eclipse.graphiti.demo.model.LvglWidget.WidgetType;

/**
 * Generates LVGL C code from screen and widget models.
 * Produces both header (.h) and source (.c) files.
 */
public class LvglCodeGenerator {

	private final LvglScreen screen;
	private final String licenseHeader;

	public LvglCodeGenerator(LvglScreen screen) {
		this(screen, null);
	}

	public LvglCodeGenerator(LvglScreen screen, String licenseHeader) {
		this.screen = screen;
		this.licenseHeader = licenseHeader;
	}

	/**
	 * Generates the header file content (.h).
	 *
	 * @return the header file content
	 */
	public String generateHeader() {
		StringBuilder sb = new StringBuilder();
		String screenName = screen.getVariableName();
		String guardName = screenName.toUpperCase() + "_H";

		// License header
		if (licenseHeader != null && !licenseHeader.isEmpty()) {
			sb.append(licenseHeader);
			if (!licenseHeader.endsWith("\n")) {
				sb.append("\n");
			}
			sb.append("\n");
		}

		// Header guard
		sb.append("#ifndef ").append(guardName).append("\n");
		sb.append("#define ").append(guardName).append("\n\n");

		// Includes
		sb.append("#include \"lvgl.h\"\n\n");

		// C++ extern
		sb.append("#ifdef __cplusplus\n");
		sb.append("extern \"C\" {\n");
		sb.append("#endif\n\n");

		// Variable declarations
		sb.append("/* Screen object */\n");
		sb.append("extern lv_obj_t *").append(screenName).append(";\n\n");

		// Widget declarations
		List<LvglWidget> allWidgets = getAllWidgets();
		if (!allWidgets.isEmpty()) {
			sb.append("/* Widget objects */\n");
			for (LvglWidget widget : allWidgets) {
				sb.append("extern lv_obj_t *").append(widget.getVariableName()).append(";\n");
			}
			sb.append("\n");
		}

		// Function declarations
		sb.append("/* Function declarations */\n");
		sb.append("void ").append(screenName).append("_create(void);\n");
		sb.append("void ").append(screenName).append("_delete(void);\n\n");

		// End C++ extern
		sb.append("#ifdef __cplusplus\n");
		sb.append("}\n");
		sb.append("#endif\n\n");

		// End header guard
		sb.append("#endif /* ").append(guardName).append(" */\n");

		return sb.toString();
	}

	/**
	 * Generates the source file content (.c).
	 *
	 * @return the source file content
	 */
	public String generateSource() {
		StringBuilder sb = new StringBuilder();
		String screenName = screen.getVariableName();

		// License header
		if (licenseHeader != null && !licenseHeader.isEmpty()) {
			sb.append(licenseHeader);
			if (!licenseHeader.endsWith("\n")) {
				sb.append("\n");
			}
			sb.append("\n");
		}

		// Includes
		sb.append("#include \"").append(screenName).append(".h\"\n\n");

		// Variable definitions
		sb.append("/* Screen object */\n");
		sb.append("lv_obj_t *").append(screenName).append(" = NULL;\n\n");

		// Widget definitions
		List<LvglWidget> allWidgets = getAllWidgets();
		if (!allWidgets.isEmpty()) {
			sb.append("/* Widget objects */\n");
			for (LvglWidget widget : allWidgets) {
				sb.append("lv_obj_t *").append(widget.getVariableName()).append(" = NULL;\n");
			}
			sb.append("\n");
		}

		// Create function
		sb.append("/**\n");
		sb.append(" * Create the ").append(screenName).append(" screen and all its widgets.\n");
		sb.append(" */\n");
		sb.append("void ").append(screenName).append("_create(void) {\n");
		
		// Create screen
		sb.append("    /* Create the screen */\n");
		sb.append("    ").append(screenName).append(" = lv_obj_create(NULL);\n");
		sb.append("    lv_obj_set_size(").append(screenName).append(", ");
		sb.append(screen.getWidth()).append(", ").append(screen.getHeight()).append(");\n");
		
		// Set screen background color
		sb.append("    lv_obj_set_style_bg_color(").append(screenName).append(", ");
		sb.append("lv_color_hex(0x").append(String.format("%06X", screen.getBgColor())).append("), ");
		sb.append("LV_PART_MAIN);\n\n");

		// Create widgets
		for (LvglWidget widget : screen.getWidgets()) {
			generateWidgetCode(sb, widget, screenName, "    ");
		}

		sb.append("}\n\n");

		// Delete function
		sb.append("/**\n");
		sb.append(" * Delete the ").append(screenName).append(" screen and all its widgets.\n");
		sb.append(" */\n");
		sb.append("void ").append(screenName).append("_delete(void) {\n");
		sb.append("    if (").append(screenName).append(" != NULL) {\n");
		sb.append("        lv_obj_del(").append(screenName).append(");\n");
		sb.append("        ").append(screenName).append(" = NULL;\n");
		
		// Reset all widget pointers
		for (LvglWidget widget : allWidgets) {
			sb.append("        ").append(widget.getVariableName()).append(" = NULL;\n");
		}
		
		sb.append("    }\n");
		sb.append("}\n");

		return sb.toString();
	}

	private void generateWidgetCode(StringBuilder sb, LvglWidget widget, String parentVar, String indent) {
		String varName = widget.getVariableName();
		WidgetType type = widget.getWidgetType();

		sb.append(indent).append("/* Create ").append(type.getDisplayName()).append(": ");
		sb.append(widget.getName()).append(" */\n");

		// Create the widget
		sb.append(indent).append(varName).append(" = ");
		sb.append(getCreateFunction(type)).append("(").append(parentVar).append(");\n");

		// Set position
		sb.append(indent).append("lv_obj_set_pos(").append(varName).append(", ");
		sb.append(widget.getX()).append(", ").append(widget.getY()).append(");\n");

		// Set size
		sb.append(indent).append("lv_obj_set_size(").append(varName).append(", ");
		sb.append(widget.getWidth()).append(", ").append(widget.getHeight()).append(");\n");

		// Set type-specific properties
		generateTypeSpecificCode(sb, widget, varName, indent);

		// Set common style properties
		generateStyleCode(sb, widget, varName, indent);

		// Set layout properties (for containers)
		if (widget.isContainer() && widget.getLayoutType() != LayoutType.NONE) {
			generateLayoutCode(sb, widget, varName, indent);
		}

		sb.append("\n");

		// Generate child widgets
		for (LvglWidget child : widget.getChildren()) {
			generateWidgetCode(sb, child, varName, indent);
		}
	}

	private String getCreateFunction(WidgetType type) {
		switch (type) {
			case BUTTON:
				return "lv_btn_create";
			case LABEL:
				return "lv_label_create";
			case SLIDER:
				return "lv_slider_create";
			case SWITCH:
				return "lv_switch_create";
			case CHECKBOX:
				return "lv_checkbox_create";
			case DROPDOWN:
				return "lv_dropdown_create";
			case TEXTAREA:
				return "lv_textarea_create";
			case IMAGE:
				return "lv_img_create";
			case ARC:
				return "lv_arc_create";
			case BAR:
				return "lv_bar_create";
			case CONTAINER:
				return "lv_obj_create";
			case ANIMIMG:
				return "lv_animimg_create";
			case ARCLABEL:
				return "lv_arclabel_create";
			case BUTTONMATRIX:
				return "lv_btnmatrix_create";
			case CALENDAR:
				return "lv_calendar_create";
			case CANVAS:
				return "lv_canvas_create";
			case CHART:
				return "lv_chart_create";
			case IMAGEBUTTON:
				return "lv_imgbtn_create";
			case KEYBOARD:
				return "lv_keyboard_create";
			case LED:
				return "lv_led_create";
			case LINE:
				return "lv_line_create";
			case LIST:
				return "lv_list_create";
			case MENU:
				return "lv_menu_create";
			case MSGBOX:
				return "lv_msgbox_create";
			case ROLLER:
				return "lv_roller_create";
			case SCALE:
				return "lv_scale_create";
			case SPANGROUP:
				return "lv_spangroup_create";
			case SPINBOX:
				return "lv_spinbox_create";
			case SPINNER:
				return "lv_spinner_create";
			case TABLE:
				return "lv_table_create";
			case TABVIEW:
				return "lv_tabview_create";
			case TILEVIEW:
				return "lv_tileview_create";
			case WIN:
				return "lv_win_create";
			default:
				return "lv_obj_create";
		}
	}

	private void generateTypeSpecificCode(StringBuilder sb, LvglWidget widget, String varName, String indent) {
		WidgetType type = widget.getWidgetType();
		String text = widget.getText();

		switch (type) {
			case BUTTON:
				// Button with label
				if (text != null && !text.isEmpty()) {
					sb.append(indent).append("{\n");
					sb.append(indent).append("    lv_obj_t *label = lv_label_create(").append(varName).append(");\n");
					sb.append(indent).append("    lv_label_set_text(label, \"").append(escapeString(text)).append("\");\n");
					sb.append(indent).append("    lv_obj_center(label);\n");
					sb.append(indent).append("}\n");
				}
				break;

			case LABEL:
				if (text != null && !text.isEmpty()) {
					sb.append(indent).append("lv_label_set_text(").append(varName).append(", \"");
					sb.append(escapeString(text)).append("\");\n");
				}
				break;

			case CHECKBOX:
				if (text != null && !text.isEmpty()) {
					sb.append(indent).append("lv_checkbox_set_text(").append(varName).append(", \"");
					sb.append(escapeString(text)).append("\");\n");
				}
				if (widget.isChecked()) {
					sb.append(indent).append("lv_obj_add_state(").append(varName).append(", LV_STATE_CHECKED);\n");
				}
				break;

			case SWITCH:
				if (widget.isChecked()) {
					sb.append(indent).append("lv_obj_add_state(").append(varName).append(", LV_STATE_CHECKED);\n");
				}
				break;

			case SLIDER:
			case BAR:
			case ARC:
				sb.append(indent).append("lv_").append(type == WidgetType.ARC ? "arc" : type.getLvglType().replace("lv_", ""));
				sb.append("_set_range(").append(varName).append(", ");
				sb.append(widget.getMinValue()).append(", ").append(widget.getMaxValue()).append(");\n");
				sb.append(indent).append("lv_").append(type == WidgetType.ARC ? "arc" : type.getLvglType().replace("lv_", ""));
				sb.append("_set_value(").append(varName).append(", ").append(widget.getValue()).append(");\n");
				break;

			case DROPDOWN:
				if (text != null && !text.isEmpty()) {
					sb.append(indent).append("lv_dropdown_set_options(").append(varName).append(", \"");
					sb.append(escapeString(text)).append("\");\n");
				}
				break;

			case TEXTAREA:
				if (text != null && !text.isEmpty()) {
					sb.append(indent).append("lv_textarea_set_text(").append(varName).append(", \"");
					sb.append(escapeString(text)).append("\");\n");
				}
				break;

			case IMAGE:
				if (widget.getImageSource() != null && !widget.getImageSource().isEmpty()) {
					sb.append(indent).append("lv_img_set_src(").append(varName).append(", &");
					sb.append(widget.getImageSource()).append(");\n");
				}
				break;

			case TABLE:
				sb.append(indent).append("lv_table_set_row_cnt(").append(varName).append(", ");
				sb.append(widget.getRowCount()).append(");\n");
				sb.append(indent).append("lv_table_set_col_cnt(").append(varName).append(", ");
				sb.append(widget.getColumnCount()).append(");\n");
				break;

			case LED:
				sb.append(indent).append("lv_led_on(").append(varName).append(");\n");
				break;

			case MSGBOX:
				if (text != null && !text.isEmpty()) {
					sb.append(indent).append("/* Note: lv_msgbox requires different API for text/title */\n");
				}
				break;

			case WIN:
				if (text != null && !text.isEmpty()) {
					sb.append(indent).append("/* Note: lv_win_add_title can be used to set window title */\n");
				}
				break;

			default:
				break;
		}
	}

	private void generateStyleCode(StringBuilder sb, LvglWidget widget, String varName, String indent) {
		// Background color
		if (widget.getBgColor() != 0xFFFFFF) {
			sb.append(indent).append("lv_obj_set_style_bg_color(").append(varName).append(", ");
			sb.append("lv_color_hex(0x").append(String.format("%06X", widget.getBgColor())).append("), ");
			sb.append("LV_PART_MAIN);\n");
		}

		// Text color
		if (widget.getTextColor() != 0x000000) {
			sb.append(indent).append("lv_obj_set_style_text_color(").append(varName).append(", ");
			sb.append("lv_color_hex(0x").append(String.format("%06X", widget.getTextColor())).append("), ");
			sb.append("LV_PART_MAIN);\n");
		}

		// Border
		if (widget.getBorderWidth() > 0) {
			sb.append(indent).append("lv_obj_set_style_border_width(").append(varName).append(", ");
			sb.append(widget.getBorderWidth()).append(", LV_PART_MAIN);\n");
			sb.append(indent).append("lv_obj_set_style_border_color(").append(varName).append(", ");
			sb.append("lv_color_hex(0x").append(String.format("%06X", widget.getBorderColor())).append("), ");
			sb.append("LV_PART_MAIN);\n");
		}

		// Radius
		if (widget.getRadius() > 0) {
			sb.append(indent).append("lv_obj_set_style_radius(").append(varName).append(", ");
			sb.append(widget.getRadius()).append(", LV_PART_MAIN);\n");
		}
	}

	private void generateLayoutCode(StringBuilder sb, LvglWidget widget, String varName, String indent) {
		LayoutType layout = widget.getLayoutType();

		if (layout == LayoutType.FLEX) {
			sb.append(indent).append("lv_obj_set_layout(").append(varName).append(", LV_LAYOUT_FLEX);\n");
			sb.append(indent).append("lv_obj_set_flex_flow(").append(varName).append(", ");
			sb.append(widget.getFlexFlow().getLvglConstant()).append(");\n");
			sb.append(indent).append("lv_obj_set_flex_align(").append(varName).append(", ");
			sb.append(widget.getFlexMainAlign().getLvglConstant()).append(", ");
			sb.append(widget.getFlexCrossAlign().getLvglConstant()).append(", ");
			sb.append(widget.getFlexTrackAlign().getLvglConstant()).append(");\n");
		} else if (layout == LayoutType.GRID) {
			sb.append(indent).append("lv_obj_set_layout(").append(varName).append(", LV_LAYOUT_GRID);\n");
			sb.append(indent).append("/* Note: Grid layout requires column and row descriptors */\n");
		}

		// Padding
		if (widget.getPadRow() > 0 || widget.getPadColumn() > 0) {
			sb.append(indent).append("lv_obj_set_style_pad_row(").append(varName).append(", ");
			sb.append(widget.getPadRow()).append(", LV_PART_MAIN);\n");
			sb.append(indent).append("lv_obj_set_style_pad_column(").append(varName).append(", ");
			sb.append(widget.getPadColumn()).append(", LV_PART_MAIN);\n");
		}
	}

	private List<LvglWidget> getAllWidgets() {
		List<LvglWidget> result = new ArrayList<>();
		for (LvglWidget widget : screen.getWidgets()) {
			collectWidgets(widget, result);
		}
		return result;
	}

	private void collectWidgets(LvglWidget widget, List<LvglWidget> result) {
		result.add(widget);
		for (LvglWidget child : widget.getChildren()) {
			collectWidgets(child, result);
		}
	}

	private String escapeString(String str) {
		if (str == null) {
			return "";
		}
		return str.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}
}
