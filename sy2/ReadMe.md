# 实验二：基于 Kotlin、Compose 与 CameraX 的 Android 应用开发

## 1. 项目简介

本项目为《软件项目研发实践(3)》课程的实验二代码合集。本次实验旨在引导学生掌握使用 Kotlin 语言开发 Android 应用的基本流程 ，并深入理解声明式 UI 框架（Jetpack Compose）与传统 XML 布局的混合使用。此外，项目集成了 Android CameraX 核心库，实现了底层的硬件设备调用功能。  

## 2. 实验任务与完成情况

本次实验包含三个核心子模块：

### 子实验 2.1：Kotlin 基本语法及实验

**核心内容**：学习 Kotlin 语言的变量与类型、空安全特性、控制流、Lambda 表达式、面向对象编程（类与 data class）及集合的常见操作 。  

**完成情况**：已完成 Kotlin 官方 Playground 中的 Koans 在线编程练习，完成比例100% 。  

### 子实验 2.2：构建 Kotlin 应用并使用 Compose 布局

**任务一**：搭建了首个基于 Kotlin 的 Android 基础工程（包含 Greeting Preview 面板显示） 。  

**任务二**：实践了 Jetpack Compose 布局的基本用法 。  

**任务三**：完成了一个面向 AI 应用的原型界面（LiteRT AI Demo） 。代码中采用了自上而下的模块化设计思路，利用 `Column` 组织整体页面，使用 `Box` 为相机预览区占位，通过 `Card` 呈现模型推理结果，并利用 `Row` 和 `Column` 实现了底部功能按钮的网格布局 。  

### 子实验 2.3：构建 Android CameraX 应用

**核心内容**：使用传统的 XML 布局文件搭建相机交互界面 ，并在 `MainActivity.kt` 中编写逻辑代码。  

**完成情况**：

- 在 `build.gradle.kts` 中成功引入了版本为 `1.5.0-alpha06` 的 CameraX 相关依赖 。  
- 实现了 Android 硬件权限（相机、麦克风等）的动态请求获取 。  
- 成功实现了 CameraX 的三大核心用例：`Preview`（实时画面预览） 、`ImageCapture`（高质量静态图片捕捉） 和 `VideoCapture`（视频录制拍摄） 。  

## 3. 技术栈

- **开发语言**：Kotlin
- **UI 构建方案**：Jetpack Compose (实验 2.2) / XML View Layouts (实验 2.3)
- **多媒体框架**：Android CameraX
- **开发环境**：Android Studio

## 4. 实验过程截图

> 本实验各个阶段的详细运行截图与效果展示，请见每个子实验文件夹下的 `screenshot` 目录。

## 5. 开发遇到的问题及解决思路

在本次实验的推进过程中，主要遇到了以下两个典型的工程环境问题，均已排查并解决：

### 问题一：Android Studio 模板选择与旧版教程的冲突

- **问题描述**：在进行子实验 2.3（构建 CameraX 应用）时，参考教程要求创建一个 "Empty Activity"。但由于使用的是最新版的 Android Studio，"Empty Activity" 默认生成的是纯 Compose 架构的项目，不会自动生成 `res/layout` 文件夹及 XML 布局文件，导致代码无法按照教程继续编写。
- **解决方案**：经过排查 Android Studio 的版本差异，在新建 CameraX 工程时，主动放弃选择 "Empty Activity"，改为选择 **"Empty Views Activity"** 模板。这样系统正确生成了 `activity_main.xml` 和基于 `setContentView` 渲染的 `MainActivity`，顺利接入教程的后续步骤。

### 问题二：模拟器运行 CameraX 持续卡死与通道断裂 (DEAD_OBJECT)

- **问题描述**：在 CameraX 实验中，代码编译成功并部署到模拟器后，应用频繁停留在主界面不断刷新，或者在请求相机权限时直接崩溃。Logcat 中抛出 `Channel is unrecoverably broken` 和 `Surface failed to disconnect (Error -32 DEAD_OBJECT)` 等底层渲染错误。

- **解决方案**：此问题并非代码逻辑错误，而是模拟器的显卡驱动在处理复杂的视频流和相机硬件映射时资源崩溃导致。通过打开 Android Studio 的 Device Manager，对 Pixel 模拟器进行了以下两步优化：

  1. 将 Graphics 渲染模式从 `Automatic` 强制修改为 **`Software - GLES 2.0`**（软件渲染模式）。

  2. 对模拟器执行了 **Cold Boot Now（冷启动）** 清除了之前卡死的硬件状态和残留锁文件。

     重启后，模拟器成功渲染出了 CameraX 的预览画面，拍照与视频录制功能均恢复正常。