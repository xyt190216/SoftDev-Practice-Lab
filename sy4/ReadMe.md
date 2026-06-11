## 实验四：实现智能图像分类 APP 实践报告

- **课程名称**：软件项目研发实践
- **实验项目**：基于 TensorFlow Lite 的智能图像分类 APP
- **学生姓名**：熊依婷
- **学号**：121052023050
- **专业班级**：软工2班
- **指导教师**：林立

## 一、 实验内容

本实验旨在 Android 设备上运用 TensorFlow Lite 运行预训练的机器学习模型。主要任务包括：

1. **获取基础工程**：拉取 `TFLClassify` 项目源码，并在物理真机上跑通搭载 CameraX 的初始版本。
2. **模型导入**：利用 Android Studio 的 ML 模型绑定功能，导入已训练好的花卉分类模型（`FlowerModel.tflite`）。
3. **图像数据预处理**：将 CameraX 捕获的 `ImageProxy` 实时转换为 TensorFlow 接受的 `TensorImage` 格式。
4. **模型推理与 UI 渲染**：调用模型处理图像，解析输出概率，提取置信度最高的类别并更新至 RecyclerView 界面。

## 二、 核心代码实现

本实验的主要工作集中在 `start` 模块的 `MainActivity.kt` 文件中。我将原有的占位虚拟数据（Fake label）删除，并依次完成了 4 个 TODO 核心逻辑：

### TODO 1：初始化 TensorFlow Lite 模型

在分析器内部实例化刚刚导入的花卉分类模型类。

```
private class ImageAnalyzer(ctx: Context, private val listener: RecognitionListener) :
        ImageAnalysis.Analyzer {
    // 实例化自动生成的模型包装类
    private val flowerModel = FlowerModel.newInstance(ctx)
    // ...
}
```

### TODO 2：转换图像格式 (ImageProxy -> TensorImage)

在 `analyze` 回调方法中，将相机流的图像帧转换为模型推理所需的格式。

```
override fun analyze(imageProxy: ImageProxy) {
    // 将 ImageProxy 转为 Bitmap，再封装为 TensorImage
    val tfImage = TensorImage.fromBitmap(toBitmap(imageProxy))
    // ...
}
```

### TODO 3：模型推理与结果排序

将转换后的图像传入模型，获取输出概率列表，并按置信度从高到低进行降序排序，截取前 `MAX_RESULT_DISPLAY`（预设数量）个结果。

```
// 处理图像，获取类别概率列表，按置信度降序排序，取出 Top 结果
val outputs = flowerModel.process(tfImage)
    .probabilityAsCategoryList.apply {
        sortByDescending { it.score } 
    }.take(MAX_RESULT_DISPLAY)
  ...
}
```

### TODO 4：封装识别结果以供 UI 显示

遍历模型的输出结果，将标签（label）和概率得分（score）封装成 `Recognition` 数据类，添加到列表中，供主线程更新界面。

```
// 将最高概率项转换为 Recognition 列表
for (output in outputs) {
    items.add(Recognition(output.label, output.score))
}
```

## 三、 实验过程与结果验证

### 1. 初始工程编译与真机运行验证

在未接入模型前，直接在物理真机上运行了 `start` 模块。应用成功获取了相机权限并开启了画面预览，此时界面下方的识别结果由代码中的 `Random.nextFloat()` 随机生成（Fake label）。

> <img src="images\fake.png" alt="fake" style="zoom:50%;" />

### 2. TFLite 模型导入配置

通过 Android Studio 的 `New > Other > TensorFlow Lite Model` 功能，成功导入了提供的 `FlowerModel.tflite`。系统自动在 `build.gradle` 中添加了依赖，并生成了包含输入输出张量（Tensor）信息摘要的自动包装类。

> <img src="D:\AndroidPrograms\sy4\images\import.png" alt="import" style="zoom:50%;" />

### 3. 完整业务逻辑运行验证（花卉识别）

完成所有 TODO 代码后重新编译运行。将手机摄像头对准向日葵（太阳花）图片进行测试，应用能够进行实时的图像捕获与模型推理，并准确地在屏幕下方输出了类别名称及对应的置信度百分比。

> <img src="D:\AndroidPrograms\sy4\images\recognize.png" alt="recognize" style="zoom:50%;" />

## 四、 实验总结

本次实验打通了移动端 AI 应用的完整闭环，让我对 Android 平台上的端侧推理有了直观深入的理解：

1. **工程化体验极佳**：以往认为在移动端部署机器学习模型非常繁琐，但 Android Studio 提供的 ML Binding 特性极大地简化了这一过程。导入 `.tflite` 文件后，IDE 自动生成了强类型的 Java/Kotlin 封装类，让调用底层模型就像调用普通函数一样简单。

2. **数据流转的理解**：通过实现 TODO 逻辑，我清晰地认识到了端侧 AI 的数据流水线：`CameraX (ImageProxy)` -> `格式转换 (Bitmap)` -> `模型输入 (TensorImage)` -> `模型推理` -> `结果排序封装` -> `UI 渲染`。

   