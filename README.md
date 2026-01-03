# Eclipse Graphiti LVGL Example

基于 Eclipse Graphiti 实现的 LVGL UI 设计器示例项目。

## 功能

- 拖拽式的图形化编辑器
- 支持多种 LVGL 控件类型
- 属性视图编辑控件属性
- 布局支持（Flex/Grid）
- 生成 LVGL C 代码 (.h 和 .c 文件)

## 项目结构

```
eclipse-graphiti-example/
├── bundles/                          # 插件模块
│   └── com.tlcsdm.eclipse.graphiti.demo/   # 主演示插件
├── features/                         # Feature模块
│   └── com.tlcsdm.eclipse.graphiti.demo.feature/
├── sites/                            # 更新站点
│   └── com.tlcsdm.eclipse.graphiti.demo.site/
└── target-platform.target            # 目标平台配置
```

## 模块功能说明

主插件 `com.tlcsdm.eclipse.graphiti.demo` 的包结构及功能：

| 包名 | 功能说明 |
|------|----------|
| `model` | **数据模型层** - 定义图形编辑器中的所有模型对象，如 LvglScreen（屏幕）、LvglWidget（控件）等 |
| `diagram` | **Graphiti 图层** - Diagram Type Provider、Feature Provider 和 Tool Behavior Provider |
| `feature` | **特性层** - 定义创建、添加、删除、移动、调整大小等 Graphiti 特性 |
| `generator` | **代码生成器** - 将图形模型转换为 LVGL C 代码，生成头文件和源文件 |
| `handler` | **命令处理器** - 处理 Eclipse 工作台命令，如工具栏按钮点击事件 |
| `preferences` | **首选项** - 管理插件的用户偏好设置，如代码生成的许可证头 |
| `wizard` | **向导** - 实现新建文件向导，用于创建新的图形设计文件 (.graphxml) |
| `util` | **工具类** - 通用工具类，如控制台日志输出 |

## 支持的 LVGL 控件

- Button (按钮)
- Label (标签)
- Slider (滑块)
- Switch (开关)
- Checkbox (复选框)
- Dropdown (下拉框)
- Textarea (文本区域)
- Image (图像)
- Arc (弧形)
- Bar (进度条)
- Container (容器)
- Chart (图表)
- Table (表格)
- List (列表)
- Tab View (标签页视图)
- Spinner (旋转器)
- LED (指示灯)
- Calendar (日历)
- Keyboard (键盘)
- Roller (滚轮选择器)
- Message Box (消息框)
- Window (窗口)

## 架构文档

详细的 Graphiti 架构说明请参考 [ARCHITECTURE.md](./ARCHITECTURE.md)。

## 构建

```bash
mvn clean verify
```

## 开发环境

- Java 21
- Eclipse 2025-03 or later
- Maven 3.9.0+

## 使用方法

1. 安装插件到 Eclipse
2. 新建 LVGL UI 图形文件 (File -> New -> Other -> LVGL UI Designer -> New LVGL UI Diagram)
3. 从调色板拖拽控件到画布
4. 在属性视图中编辑控件属性
5. 点击工具栏的 "Generate C Code" 按钮生成代码

**注意事项:**
- 每个 `.graphxml` 文件都会有一个对应的 `.diagram` 文件自动生成，用于存储 Graphiti 图形编辑器的元数据
- `.diagram` 文件已添加到 `.gitignore`，不需要提交到版本控制系统
- 如果打开现有的 `.graphxml` 文件时出现 "No Diagram found for URI" 错误，请删除对应的 `.diagram` 文件，编辑器会自动重新创建

## 生成的代码示例

**头文件 (screen.h):**
```c
#ifndef SCREEN_H
#define SCREEN_H

#include "lvgl.h"

#ifdef __cplusplus
extern "C" {
#endif

extern lv_obj_t *screen;
extern lv_obj_t *btn_ok;
extern lv_obj_t *lbl_title;

void screen_create(void);
void screen_delete(void);

#ifdef __cplusplus
}
#endif

#endif /* SCREEN_H */
```

**源文件 (screen.c):**
```c
#include "screen.h"

lv_obj_t *screen = NULL;
lv_obj_t *btn_ok = NULL;
lv_obj_t *lbl_title = NULL;

void screen_create(void) {
    screen = lv_obj_create(NULL);
    lv_obj_set_size(screen, 480, 320);
    lv_obj_set_style_bg_color(screen, lv_color_hex(0xFFFFFF), LV_PART_MAIN);

    btn_ok = lv_btn_create(screen);
    lv_obj_set_pos(btn_ok, 100, 100);
    lv_obj_set_size(btn_ok, 120, 50);
    {
        lv_obj_t *label = lv_label_create(btn_ok);
        lv_label_set_text(label, "OK");
        lv_obj_center(label);
    }

    lbl_title = lv_label_create(screen);
    lv_obj_set_pos(lbl_title, 100, 30);
    lv_obj_set_size(lbl_title, 200, 40);
    lv_label_set_text(lbl_title, "LVGL UI Designer");
}

void screen_delete(void) {
    if (screen != NULL) {
        lv_obj_del(screen);
        screen = NULL;
    }
}
```

## 许可证

Eclipse Public License - v2.0
