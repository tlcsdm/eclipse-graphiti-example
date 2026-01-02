# Graphiti + LVGL 架构说明 / Architecture Documentation

本文档详细说明项目中 Graphiti (Graphical Tooling Infrastructure) 以及代码生成的实现架构。

---

## 目录

1. [整体架构图](#整体架构图)
2. [Graphiti 架构](#graphiti-架构)
3. [数据模型](#数据模型)
4. [代码生成流程](#代码生成流程)
5. [核心类关系图](#核心类关系图)
6. [数据流说明](#数据流说明)

---

## 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Eclipse Workbench                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐ │
│  │   Palette       │  │   Editor Area   │  │   Properties View           │ │
│  │   (调色板)       │  │   (编辑区域)     │  │   (属性视图)                │ │
│  │                 │  │                 │  │                             │ │
│  │  ┌───────────┐  │  │  ┌───────────┐  │  │  Widget Name: btn_ok        │ │
│  │  │ Selection │  │  │  │           │  │  │  Type: Button               │ │
│  │  │ Button    │  │  │  │  Canvas   │  │  │  Text: OK                   │ │
│  │  │ Label     │  │  │  │  (画布)   │  │  │  X: 100  Y: 100             │ │
│  │  │ Slider    │  │  │  │           │  │  │  Width: 120  Height: 50     │ │
│  │  │ ...       │  │  │  └───────────┘  │  │  Background: #FFFFFF        │ │
│  │  └───────────┘  │  │                 │  │                             │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────────────┤
│                           Graphiti Framework                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    Diagram Type Provider                              │  │
│  │  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐            │  │
│  │  │   Model     │◄───►│  Feature    │◄───►│   Tool      │            │  │
│  │  │   (模型)    │     │  Provider   │     │  Behavior   │            │  │
│  │  │             │     │  (特性提供) │     │  Provider   │            │  │
│  │  │ LvglScreen  │     │             │     │             │            │  │
│  │  │ LvglWidget  │     │ CreateFeature│     │ Context    │            │  │
│  │  └─────────────┘     │ AddFeature  │     │ Buttons    │            │  │
│  │         │            │ DeleteFeature│     │ Tooltip    │            │  │
│  │         │            │ MoveFeature │     │             │            │  │
│  │         │PropertyChg │ ResizeFeature│     └─────────────┘            │  │
│  │         └────────────┴─────────────┘                                 │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────────────────┤
│                            Persistence                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │  LvglXmlSerializer                                                    │  │
│  │  ├── save(LvglScreen, OutputStream)  // 保存模型到 XML               │  │
│  │  └── load(InputStream) : LvglScreen  // 从 XML 加载模型              │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────────────────┤
│                          Code Generation                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │  LvglCodeGenerator                                                    │  │
│  │  ├── generateHeader() : String   // 生成 .h 头文件                   │  │
│  │  └── generateSource() : String   // 生成 .c 源文件                   │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Graphiti 架构

Graphiti 是 Eclipse 提供的图形编辑框架，本项目的实现如下：

### Diagram Type Provider

```
┌─────────────────────────────────────────────────────────────────┐
│                    LvglDiagramTypeProvider                       │
│  (图形类型提供者 - Graphiti 入口点)                               │
│  ├── getFeatureProvider()        // 获取特性提供者              │
│  └── getAvailableToolBehaviorProviders()  // 工具行为提供者     │
└─────────────────────────────────────────────────────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │               │               │
                ▼               ▼               ▼
┌───────────────────┐ ┌───────────────┐ ┌───────────────────┐
│ LvglFeatureProvider│ │LvglToolBehavior│ │ LvglImageProvider │
│  (特性提供者)       │ │ Provider      │ │  (图像提供者)      │
│                   │ │  (工具行为)   │ │                   │
│ - CreateFeature   │ │ - Tooltips    │ │ - Widget Icons   │
│ - AddFeature      │ │ - Context Menu│ │                   │
│ - DeleteFeature   │ │ - Context Btns│ │                   │
│ - MoveFeature     │ │               │ │                   │
│ - ResizeFeature   │ │               │ │                   │
│ - UpdateFeature   │ │               │ │                   │
│ - LayoutFeature   │ │               │ │                   │
│ - DirectEditFeature│ │               │ │                   │
└───────────────────┘ └───────────────┘ └───────────────────┘
```

### Feature (特性) 层

```
┌─────────────────────────────────────────────────────────────────┐
│                        Feature 类型                              │
│                                                                 │
│  CreateLvglWidgetFeature                                        │
│  ├── canCreate()     // 检查是否可以创建                        │
│  └── create()        // 创建模型对象和图形                      │
│                                                                 │
│  AddLvglWidgetFeature                                           │
│  ├── canAdd()        // 检查是否可以添加图形                    │
│  └── add()           // 添加图形表示                            │
│                                                                 │
│  DeleteLvglWidgetFeature                                        │
│  ├── canDelete()     // 检查是否可以删除                        │
│  └── delete()        // 删除模型和图形                          │
│                                                                 │
│  MoveLvglWidgetFeature                                          │
│  ├── canMoveShape()  // 检查是否可以移动                        │
│  └── postMoveShape() // 移动后更新模型                          │
│                                                                 │
│  ResizeLvglWidgetFeature                                        │
│  ├── canResizeShape()// 检查是否可以调整大小                    │
│  └── resizeShape()   // 调整大小并更新模型                      │
│                                                                 │
│  UpdateLvglWidgetFeature                                        │
│  ├── updateNeeded()  // 检查是否需要更新                        │
│  └── update()        // 更新图形表示                            │
│                                                                 │
│  LayoutLvglWidgetFeature                                        │
│  └── layout()        // 布局子元素                              │
│                                                                 │
│  DirectEditLvglWidgetFeature                                    │
│  └── setValue()      // 直接编辑文本                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 数据模型

### Model (模型层) - `model` 包

```
┌─────────────────────────────────────────────────────────────────┐
│                        ModelElement                              │
│  (所有模型元素的抽象基类)                                         │
│  ├── PropertyChangeSupport  // 属性变更通知支持                  │
│  ├── addPropertyChangeListener()                                │
│  ├── removePropertyChangeListener()                             │
│  └── firePropertyChange()                                       │
└─────────────────────────────────────────────────────────────────┘
                                │
                ┌───────────────┴───────────────┐
                │                               │
                ▼                               ▼
┌───────────────────┐               ┌───────────────────┐
│    LvglScreen     │               │    LvglWidget     │
│   (屏幕/画布)      │               │    (控件)          │
│                   │               │                   │
│ - name: String    │               │ - name            │
│ - width: int      │               │ - widgetType      │
│ - height: int     │               │ - x, y            │
│ - bgColor: int    │               │ - width, height   │
│ - widgets: List   │               │ - text            │
│                   │               │ - bgColor         │
│                   │               │ - textColor       │
│                   │               │ - children        │
│                   │               │ - layoutType      │
│                   │               │ - flexFlow        │
│                   │               │ - ...             │
└───────────────────┘               └───────────────────┘
```

### Widget Types (控件类型)

```
┌─────────────────────────────────────────────────────────────────┐
│                     LvglWidget.WidgetType                        │
│                                                                 │
│  基础控件:                                                       │
│  ├── BUTTON       (lv_btn)      - 按钮                          │
│  ├── LABEL        (lv_label)    - 标签                          │
│  ├── SLIDER       (lv_slider)   - 滑块                          │
│  ├── SWITCH       (lv_switch)   - 开关                          │
│  ├── CHECKBOX     (lv_checkbox) - 复选框                        │
│  ├── DROPDOWN     (lv_dropdown) - 下拉框                        │
│  ├── TEXTAREA     (lv_textarea) - 文本区域                      │
│  └── IMAGE        (lv_img)      - 图像                          │
│                                                                 │
│  高级控件:                                                       │
│  ├── ARC          (lv_arc)      - 弧形进度                      │
│  ├── BAR          (lv_bar)      - 进度条                        │
│  ├── CHART        (lv_chart)    - 图表                          │
│  ├── TABLE        (lv_table)    - 表格                          │
│  ├── CALENDAR     (lv_calendar) - 日历                          │
│  └── KEYBOARD     (lv_keyboard) - 键盘                          │
│                                                                 │
│  容器控件:                                                       │
│  ├── CONTAINER    (lv_obj)      - 容器                          │
│  ├── TABVIEW      (lv_tabview)  - 标签页视图                    │
│  ├── LIST         (lv_list)     - 列表                          │
│  ├── MENU         (lv_menu)     - 菜单                          │
│  └── WIN          (lv_win)      - 窗口                          │
│                                                                 │
│  指示器控件:                                                     │
│  ├── LED          (lv_led)      - LED指示灯                     │
│  ├── SPINNER      (lv_spinner)  - 旋转加载                      │
│  └── ROLLER       (lv_roller)   - 滚轮选择器                    │
└─────────────────────────────────────────────────────────────────┘
```

---

## 代码生成流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      代码生成流程                                │
│                                                                 │
│   用户操作                                                       │
│      │                                                          │
│      ▼                                                          │
│  ┌─────────────────┐                                            │
│  │ 点击"生成代码"   │  (工具栏按钮或右键菜单)                     │
│  │ 按钮            │                                            │
│  └────────┬────────┘                                            │
│           │                                                     │
│           ▼                                                     │
│  ┌─────────────────┐                                            │
│  │ GenerateCode    │  handler 包                                │
│  │ Handler         │  处理命令，获取当前编辑器的 LvglScreen      │
│  └────────┬────────┘                                            │
│           │                                                     │
│           ▼                                                     │
│  ┌─────────────────┐                                            │
│  │ LvglCode        │  generator 包                              │
│  │ Generator       │  遍历屏幕中的所有控件，生成 C 代码          │
│  │                 │                                            │
│  │ ┌─────────────┐ │                                            │
│  │ │ generate    │ │  生成头文件声明                             │
│  │ │ Header()    │ │  - 控件变量声明 (extern lv_obj_t*)         │
│  │ │             │ │  - 函数声明 (create/delete)                │
│  │ └─────────────┘ │                                            │
│  │                 │                                            │
│  │ ┌─────────────┐ │                                            │
│  │ │ generate    │ │  生成源文件实现                             │
│  │ │ Source()    │ │  - 控件变量定义                            │
│  │ │             │ │  - create 函数实现                         │
│  │ │             │ │    - 创建控件 (lv_xxx_create)              │
│  │ │             │ │    - 设置位置大小                          │
│  │ │             │ │    - 设置属性 (文本、颜色等)                │
│  │ │             │ │  - delete 函数实现                         │
│  │ └─────────────┘ │                                            │
│  └────────┬────────┘                                            │
│           │                                                     │
│           ▼                                                     │
│  ┌─────────────────┐                                            │
│  │ 输出文件        │                                            │
│  │ - xxx.h        │  头文件                                     │
│  │ - xxx.c        │  源文件                                     │
│  └─────────────────┘                                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 核心类关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             核心类关系                                       │
│                                                                             │
│  diagram 包                                                                 │
│  ┌─────────────────────────────────────┐                                   │
│  │      LvglDiagramTypeProvider        │                                   │
│  │  (Graphiti 图形类型提供者)           │                                   │
│  │  ┌────────────────────────────────┐ │                                   │
│  │  │      LvglFeatureProvider       │ │                                   │
│  │  │  (特性提供者)                   │ │                                   │
│  │  │  - CreateFeature               │ │                                   │
│  │  │  - AddFeature                  │ │                                   │
│  │  │  - DeleteFeature               │ │                                   │
│  │  │  - MoveFeature                 │ │                                   │
│  │  │  - ResizeFeature               │ │                                   │
│  │  └────────────────────────────────┘ │                                   │
│  └─────────────────────────────────────┘                                   │
│                    │                                                        │
│                    │ uses                                                   │
│                    ▼                                                        │
│  ┌─────────────────────────────────────┐                                   │
│  │         LvglWidget                  │  model 包                         │
│  │  (控件模型)                         │                                   │
│  │  - name, widgetType, x, y          │                                   │
│  │  - width, height, text             │                                   │
│  │  - PropertyChangeSupport           │                                   │
│  └─────────────────────────────────────┘                                   │
│                    │                                                        │
│                    │ serialized by                                          │
│                    ▼                                                        │
│  ┌─────────────────────────────────────┐                                   │
│  │       LvglXmlSerializer             │  model 包                         │
│  │  (XML 序列化器)                     │                                   │
│  │  - save(LvglScreen, OutputStream)  │                                   │
│  │  - load(InputStream)               │                                   │
│  └─────────────────────────────────────┘                                   │
│                                                                             │
│  ┌─────────────────────────────────────┐                                   │
│  │       LvglCodeGenerator             │  generator 包                     │
│  │  (代码生成器)                       │                                   │
│  │  - generateHeader()                │                                   │
│  │  - generateSource()                │                                   │
│  └─────────────────────────────────────┘                                   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 数据流说明

### 1. 创建控件流程

```
用户从调色板拖拽控件 → LvglFeatureProvider.getCreateFeature()
                                    ↓
                        CreateLvglWidgetFeature.create()
                                    ↓
                        创建 LvglWidget 实例
                                    ↓
                        screen.addWidget(widget)
                                    ↓
                        firePropertyChange(PROPERTY_ADD)
                                    ↓
                        AddLvglWidgetFeature.add()
                                    ↓
                        创建 Graphiti 图形元素
                                    ↓
                        界面显示新控件
```

### 2. 属性编辑流程

```
用户在属性视图修改属性 → widget.setXxx(value)
                                    ↓
                        firePropertyChange("xxx", oldValue, newValue)
                                    ↓
                        UpdateLvglWidgetFeature.updateNeeded()
                                    ↓
                        UpdateLvglWidgetFeature.update()
                                    ↓
                        更新 Graphiti 图形元素
                                    ↓
                        界面更新控件外观
```

### 3. 保存流程

```
用户按 Ctrl+S → Graphiti Editor.doSave()
                        ↓
                LvglXmlSerializer.save(screen, outputStream)
                        ↓
                遍历 screen.getWidgets()
                        ↓
                生成 XML 结构
                        ↓
                写入 .graphxml 文件
```

### 4. 代码生成流程

```
用户点击生成按钮 → GenerateCodeHandler.execute()
                        ↓
                获取当前编辑器的 LvglScreen
                        ↓
                new LvglCodeGenerator(screen)
                        ↓
                generateHeader() / generateSource()
                        ↓
                遍历控件，生成 C 代码
                        ↓
                写入 .h 和 .c 文件
```

---

## 扩展指南

### 添加新控件类型

1. **model 包**: 在 `LvglWidget.WidgetType` 枚举中添加新类型
2. **feature 包**: 在 `CreateLvglWidgetFeature` 中添加默认尺寸
3. **feature 包**: 在 `AddLvglWidgetFeature` 中添加图形绘制逻辑
4. **diagram 包**: 在 `LvglFeatureProvider.getCreateFeatures()` 中添加新类型
5. **generator 包**: 在 `LvglCodeGenerator` 中添加代码生成逻辑

### 添加新属性

1. **model 包**: 在 `LvglWidget` 中添加属性字段和 getter/setter
2. **model 包**: 在 `LvglXmlSerializer` 中添加序列化/反序列化逻辑
3. **generator 包**: 在 `LvglCodeGenerator` 中添加代码生成逻辑

---

## 参考资料

- [Eclipse Graphiti](https://www.eclipse.org/graphiti/)
- [Graphiti Tutorial](https://www.eclipse.org/graphiti/documentation/tutorial.php)
- [LVGL (Light and Versatile Graphics Library)](https://lvgl.io/)
- [LVGL Documentation](https://docs.lvgl.io/)
