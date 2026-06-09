# 花卉识别与移动端部署项目

**学号：121052023050**

**姓名：熊依婷**

本项目完整演示了从基于 TensorFlow 的深度学习模型训练，到模型轻量化转换，再到 Android 移动端应用集成的端到端全流程。

## 项目概述

1. **模型训练 (Jupyter Notebook):** 使用 TensorFlow/Keras 构建并训练了一个花卉图像分类模型，基于 `EfficientNet-Lite0` 架构。
2. **模型导出 (TFLite):** 将训练好的 `.keras` 模型转换为适用于移动端推理的 `.tflite` 格式（FLOAT32），并生成对应的标签文件 `labels.txt`。
3. **Android 部署 (Android Studio):** 将生成的模型文件集成到现有的 Android CameraX 应用中，实现调用手机摄像头进行实时的花卉识别。

## 仓库目录结构

建议在查看源码前了解本项目的目录结构：

```
├── Jupyter_Notebook/            # 模型训练代码目录
│   └── flower_training.ipynb    # 包含数据预处理、模型训练、TFLite导出的完整 Python 脚本
├── TFLClassify-main/start/      # Android Studio 项目目录
│   ├── start/src/main/assets/   # 存放自定义模型 (model.tflite) 和标签 (labels.txt)
│   ├── start/src/main/java/...  # 核心业务逻辑 (包含 MainActivity.kt)
│   └── start/build.gradle       # Gradle 依赖配置文件 (已解决 LiteRT 冲突)
└── README.md                    # 项目说明文档
```

## 技术栈与依赖版本

在项目推进过程中，为了解决现代 Android 系统与 TensorFlow 库的兼容性问题，本项目采用了 Google 最新的边缘端 AI 解决方案：

- **模型训练环境:** Python 3.x, TensorFlow 2.x
- **Android 开发语言:** Kotlin
- **相机框架:** CameraX
- **移动端推理引擎:** **LiteRT (原 TensorFlow Lite)**
  - `com.google.ai.edge.litert:litert:1.0.1`
  - `org.tensorflow:tensorflow-lite-support:0.4.4` (剔除旧版 tflite 核心以防冲突)
- **Android 兼容性:** `targetSdkVersion 34` (适配 Android 14 规范，显式声明 `android:exported`)

## 核心工程问题攻克记录

本项目不仅实现了基础功能，还重点解决了在实际工程落地时的几个bug供参考：

1. **"FULLY_CONNECTED version 12" 算子版本冲突:**
   - **问题:** 较新版本的 Python TF 导出的模型包含高版本算子，旧版 Android TFLite 引擎无法解析导致 Crash。
   - **解决:** 放弃旧版依赖，将 Android 项目核心推理库整体迁移至最新的 `LiteRT 1.0.1` 架构。
2. **`tensorflow-lite` 与 `litert` 产生的 Duplicate Class 冲突:**
   - **问题:** 引入 Support 库时，Gradle 底层依然会拉取旧版 `tensorflow-lite-api`，导致与新版 `litert` 的类名（如 `DataType`）冲突。
   - **解决:** 在 `build.gradle` 中通过 `exclude` 语法强制剔除 `support` 库自带的 `tensorflow-lite` 和 `tensorflow-lite-api` 模块。
3. **Tensor 数据维度冲突 (Buffer Size Mismatch):**
   - **问题:** 摄像头获取的 Bitmap 默认转为 150528 字节的 `UINT8` 格式，而模型需要的是 602112 字节的 `FLOAT32` 格式，导致数据拷贝失败。
   - **解决:** 在 Kotlin 代码的 Analyzer 中，显式声明 `TensorImage(DataType.FLOAT32)` 容器，强制进行数据类型升级转换。

## 运行指南

1. **Android 端:**
   - 使用 Android Studio 打开 `Android_App` 文件夹。
   - 点击右上角 `Sync Project with Gradle Files`。
   - 连接物理真机（强烈建议使用真机以保证摄像头性能），点击 `Run` 即可运行。
2. **模型训练端:**
   - 使用 Jupyter Notebook 或 Google Colab 打开 `Jupyter_Notebook/flower_training.ipynb` 即可复现训练过程。