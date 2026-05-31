# Android TFLite Flower Classifier

本项目是一个基于 Android 平台的实时花卉识别应用。项目通过集成 CameraX 库获取实时摄像头数据流，并利用 TensorFlow Lite 部署自定义的轻量级图像分类模型 (`FlowerModel.tflite`)，结合 GPU 加速实现了在移动端的高效、准确推理。

## 🌟 核心功能特性

* **实时图像处理**：利用 Android CameraX 库高效处理摄像头帧数据。
* **端侧 AI 推理**：集成了 TensorFlow Lite 框架，在离线环境下完成图像分类计算。
* **数据结构转换**：实现了从底层的 `ImageProxy` 到 `Bitmap`，再到 TFLite 标准输入 `TensorImage` 的无缝数据流转。
* **结果智能过滤**：对推理输出按置信度 (Score) 降序排列，并在 UI 动态渲染最可能的 Top-K 识别结果。

---

## 📸 运行效果截图

### 1. 初始占位状态 (Fake Labels)
展示了模型接入前，UI 层面利用随机数进行数据占位的初始状态。
<img src="D:\AndroidPrograms\sy4\fake.png" style="zoom:50%;" />

### 2. 模型导入与摘要 (Model Import Summary)
利用 Android Studio ML Binding 功能成功导入 `FlowerModel.tflite` 模型后的输入输出张量结构摘要。
<img src="D:\AndroidPrograms\sy4\import.png" style="zoom:35%;" />

### 3. 最终实时识别 (Final Recognition)
完成全部逻辑链路后，摄像头对准目标花卉并进行实时、准确分类的结果展示。
<img src="D:\AndroidPrograms\sy4\recognize.png" style="zoom:50%;" />

---

## 🛠️ 开发环境与依赖

* **IDE**: Android Studio 4.1 或更高版本
* **硬件要求**: 需使用开启了“USB 调试”的真实 Android 物理设备进行测试（不建议使用模拟器验证摄像头与 GPU 加速）。
* **主要依赖库**:
    * `androidx.camera:camera-core` (CameraX)
    * `org.tensorflow:tensorflow-lite`
    * `org.tensorflow:tensorflow-lite-support`

---

## 💻 核心代码实现路径

本项目的核心逻辑集中在 `MainActivity.kt` 中的 `ImageAnalyzer` 类，主要攻克了以下关键技术节点：

1.  **模型初始化配置**
    实例化 TFLite 自动生成的 `FlowerModel` 类，加载模型至内存。
```kotlin
    private val flowerModel = FlowerModel.newInstance(ctx)
```

2.  **图像预处理与张量转换**
    接管 CameraX 传回的 `ImageProxy`，将其转换为 `Bitmap` 后，进一步封装为模型所需的 `TensorImage` 格式。
```kotlin
    val tfImage = TensorImage.fromBitmap(toBitmap(imageProxy))
```

3.  **模型前向推理与结果排序**
    将张量送入模型进行推理，获取分类概率列表。利用 Kotlin 的集合操作，按照置信度 `score` 降序排列，并截取前 `MAX_RESULT_DISPLAY` 个最高概率结果。
```kotlin
    val outputs = flowerModel.process(tfImage)
        .probabilityAsCategoryList.apply {
            sortByDescending { it.score } 
        }.take(MAX_RESULT_DISPLAY)
```

4.  **UI 数据映射**
    遍历输出结果，将底层分类对象的 `label` 和 `score` 封装为视图层所需的数据类 `Recognition`，以驱动 RecyclerView 刷新。
```kotlin
    for (output in outputs) {
        items.add(Recognition(output.label, output.score))
    }
```

---

## 👤 作者信息

121052023050 熊依婷

本项目作为端侧深度学习模型部署的实践基石，为后续探索更复杂的计算机视觉任务（如对象检测、分割）在移动端落地积累了工程经验。

---
*License: CC 4.0 BY-SA (Derived from Android TFLite codelab instructions)*

