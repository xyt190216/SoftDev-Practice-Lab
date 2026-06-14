## 实验 5-2：TensorFlow 石头剪刀布手势模型生成报告

- **课程名称**：软件项目研发实践
- **实验项目**：实验 5-2 TensorFlow 石头剪刀布手势模型生成
- **学生姓名**：熊依婷
- **学号**：121052023050
- **专业班级**：软工2班
- **指导教师**：林立

## 一、 实验内容

1. 进一步掌握TensorFlow模型训练和生成的基本流程
2. 下载石头剪刀布图片数据集
3. 学习石头剪刀布图片识别模型的生成，参考教程。
4. 绘制图像验证模型的性能

## 二、 核心实验步骤与代码实现

### 第一步：下载、解压与检验数据集

通过 `urllib.request` 自动从云端下载 `rps.zip` (训练集) 和 `rps-test-set.zip` (测试集) 并解压至本地目录。解压后，通过遍历目录统计各类别的图片数量，并使用 `matplotlib.image` 打印部分样本图片以验证数据集的完整性。

### 第二步：数据预处理 (Data Augmentation)

使用 Keras 的 `ImageDataGenerator` 类对训练图像进行数据增强预处理，这能有效扩充数据多样性，使得后续的训练模型更具泛化能力，不易过拟合。

```
training_datagen = ImageDataGenerator(
      rescale = 1./255,
      rotation_range=40,
      width_shift_range=0.2,
      height_shift_range=0.2,
      shear_range=0.2,
      zoom_range=0.2,
      horizontal_flip=True,
      fill_mode='nearest')
```

### 第三步：构建卷积神经网络 (CNN)

使用 `tf.keras.models.Sequential` 堆叠多层神经网络结构：

```
model = tf.keras.models.Sequential([
    tf.keras.layers.Conv2D(64, (3,3), activation='relu', input_shape=(150, 150, 3)),
    tf.keras.layers.MaxPooling2D(2, 2),
    tf.keras.layers.Conv2D(64, (3,3), activation='relu'),
    tf.keras.layers.MaxPooling2D(2,2),
    tf.keras.layers.Conv2D(128, (3,3), activation='relu'),
    tf.keras.layers.MaxPooling2D(2,2),
    tf.keras.layers.Conv2D(128, (3,3), activation='relu'),
    tf.keras.layers.MaxPooling2D(2,2),
    tf.keras.layers.Flatten(),
    tf.keras.layers.Dropout(0.5),
    tf.keras.layers.Dense(512, activation='relu'),
    tf.keras.layers.Dense(3, activation='softmax')
])
```

### 第四步：编译、训练与绘制验证曲线

使用 `categorical_crossentropy` 作为损失函数，`rmsprop` 作为优化器。经过 25 个 Epoch 的迭代训练，调用 `matplotlib.pyplot` 绘制训练过程的曲线，并将模型保存为 `rps.h5`。

### 第五步：输出model.tflite和label.txt

```
import tensorflow as tf
# 1. 加载你刚才训练好的模型
model = tf.keras.models.load_model("rps.h5")
# 2. 实例化转换器并转换模型
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()
# 3. 将转换后的模型保存到本地
with open("model.tflite", "wb") as f:
    f.write(tflite_model)
print("🎉 成功！model.tflite 已经保存在你当前 Notebook 的同一目录下了。")
# 获取生成器自动推断的类别索引字典，例如 {'paper': 0, 'rock': 1, 'scissors': 2}
labels_dict = train_generator.class_indices
# 根据索引按顺序提取文件夹名称
class_names = [name for name, index in sorted(labels_dict.items(), key=lambda x: x[1])]
# 写入到 labels.txt 文件中
with open("labels.txt", "w", encoding="utf-8") as f:
    f.write("\n".join(class_names) + "\n")
print("📄 成功！labels.txt 已经生成，标签顺序为:", class_names)
```

## 三、 实验结果与截图佐证

**实验佐证 1：数据集检验与样本展示** 

<img src="images\1.png" alt="1" style="zoom:50%;" />

**实验佐证 2：模型训练过程日志** 

<img src="images\training.png" alt="training" style="zoom:50%;" />

**实验佐证 3：训练与验证指标曲线** 

<img src="images\acc.png" alt="acc" style="zoom:50%;" />

## 四、 实验总结

在将训练生成的自定义模型（转换为 `.tflite` 后）部署到 Android 设备进行实际测试时，应用出现了严重的闪退问题。

- **原因：模型输入参数硬编码**
  - logcat抛出了 `IllegalArgumentException: Cannot copy to a TensorFlowLite tensor with X bytes from a Java Buffer with Y bytes`。是因为 Android 原代码中**硬编码**了输入尺寸（224×224）、输入类型（FLOAT32）和输入层名称。当我换上本次实验新训练的模型（输入尺寸变成了 150×150）时，Java Buffer 和底层 Tensor 要求的字节长度不匹配，导致内存拷贝失败。
  - **最终解决方案**：为了让移动端代码一劳永逸地适配未来任何尺寸和精度的 TFLite 模型，我修改了 `MainActivity.kt` 中的逻辑，**取消硬编码，改为在运行时动态从模型中读取参数配置**：

```
// 获取模型输入信息（自动动态适配）
private val inputTensor = interpreter.getInputTensor(0)  // 使用索引获取，不依赖具体的 Layer Name
private val inputShape = inputTensor.shape()
// 动态获取输入图片尺寸
private val inputSize = if (inputShape.size >= 3) inputShape[1] else 150
private val inputType = inputTensor.dataType()

// 根据当前加载模型的实际类型，动态创建正确的 TensorImage
val tfImage = if (inputType == DataType.FLOAT32) {
    TensorImage(DataType.FLOAT32)
} else {
    TensorImage(DataType.UINT8)
}
```

经过这一重构，代码获得了极强的解耦性，未来更换不同输入尺度的 AI 模型时，无需再修改任何一行底层业务逻辑，完美实现了移动端推理引擎的鲁棒性。