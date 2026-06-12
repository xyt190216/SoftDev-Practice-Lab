## 实验5-1：TensorFlow 模型生成与端到端部署报告

- **课程名称**：软件项目研发实践
- **实验项目**：TensorFlow 模型训练
- **学生姓名**：熊依婷
- **学号**：121052023050
- **专业班级**：软工2班
- **指导教师**：林立

## 📌 实验内容

- **模型训练 (Jupyter Notebook):** 使用 TensorFlow/Keras 构建并训练一个花卉图像分类模型。
- **模型导出 (TFLite):** 将训练好的 `.keras` 模型转换为适用于移动端推理的 `.tflite` 格式（FLOAT32），并生成对应的标签文件 `labels.txt`。
- **Android 部署 (Android Studio):** 将生成的模型文件集成到现有的 Android CameraX 应用中，实现调用手机摄像头进行实时的花卉识别。

## 🚀 实验过程

在成功 import tflite_model_maker后，按照以下四个步骤完成了模型构建：

- **Step 1: 获取并划分数据集** 下载花卉数据集，并将 90% 用于训练，10% 用于测试。

  ```
  image_path = tf.keras.utils.get_file(
        'flower_photos.tgz',
        '[https://storage.googleapis.com/download.tensorflow.org/example_images/flower_photos.tgz](https://storage.googleapis.com/download.tensorflow.org/example_images/flower_photos.tgz)',
        extract=True)
  image_path = os.path.join(os.path.dirname(image_path), 'flower_photos')
  data = DataLoader.from_folder(image_path)
  train_data, test_data = data.split(0.9)
  ```

- **Step 2: 训练模型** 基于预训练架构进行迁移学习，执行 5 个 Epoch。

  ```
  model = image_classifier.create(train_data)
  ```

- **Step 3: 测试评估**

  ```
  loss, accuracy = model.evaluate(test_data)
  ```

- **Step 4: 导出 TFLite 模型与标签** 将模型导出为 `.tflite` 格式，此文件将直接用于移动端部署。

  ```
  model.export(export_dir='.')
  ```

### 3. 实验结果与截图佐证

#### 实验佐证 1：模型训练过程

<img src="images\ModelTraining.png" alt="ModelTraining" style="zoom:50%;" />

#### 实验佐证2：使用实验四的应用验证生成的模型

<img src="images\识别.png" alt="识别" style="zoom:50%;" />

## 🛠 核心工程问题攻克记录 (移动端部署期)

除了实验 5-1 中模型训练环节的环境坑之外，在将生成的 `.tflite` 模型实际部署进 Android 工程时，我还排查并解决了以下几个核心底层 Bug：

### 1. "FULLY_CONNECTED version 12" 算子版本冲突

- **问题还原**：较新版本的 Python TensorFlow 导出的 `.tflite` 模型包含了高版本的算子配置，而 Android 项目原依赖的旧版 TFLite 引擎无法解析该算子，导致应用直接 Crash。
- **解决方案**：果断放弃过时的旧版依赖库，将 Android 项目核心推理库整体升级并迁移至最新的 `LiteRT 1.0.1` 架构，完美兼容新算子。

### 2. `tensorflow-lite` 与 `litert` 产生的 Duplicate Class 冲突

- **问题还原**：在引入 Support 库时，Gradle 依赖解析树依然会隐式拉取旧版 `tensorflow-lite-api`，这导致它与新引入的 `litert` 出现严重的类名重复（如 `DataType` 类冲突），阻断了编译。
- **解决方案**：在 `build.gradle` 中利用 `exclude` 语法结构，强制从依赖树中剔除了 `support` 库自带的 `tensorflow-lite` 和 `tensorflow-lite-api` 模块，实现了编译通行。

### 3. Tensor 数据维度冲突 

- **问题还原**：摄像头实时获取并转换的 Bitmap 默认被压缩为了 150,528 字节的 `UINT8` 格式；然而模型定义的输入张量需要的是 602,112 字节的 `FLOAT32` 格式，导致底层内存拷贝报错失败。
- **解决方案**：在 Kotlin 代码的 `Analyzer` 中，放弃默认的隐式转换，通过 `TensorImage(DataType.FLOAT32)` 显式声明数据精度格式，配合 `ImageProcessor` 管线将其强制转换为 `FLOAT32`，顺利喂入模型。