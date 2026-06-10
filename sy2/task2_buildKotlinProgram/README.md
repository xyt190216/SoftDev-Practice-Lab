## 实验 2-2：构建 Kotlin 应用并使用 Compose 布局报告

- **课程名称**：软件项目研发实践
- **实验名称**：构建 Kotlin 应用并使用 Compose 布局
- **学生姓名**：熊依婷
- **学号**：121052023050
- **专业班级**：软工2班
- **指导教师**：林立

## 一、 实验内容

根据实验指导说明，本次实验主要完成基于 Kotlin 语言和 Jetpack Compose 的 Android 界面布局开发，包含以下三个核心任务：

- **任务一**：按照教程完成首个 Kotlin APP 的构建。
- **任务二**：按照教程完成 Compose 布局的实践。
- **任务三**：完成面向 AI 应用的 Compose 布局。

## 二、 任务具体实现与验证

### 任务一：创建首个 Kotlin 应用

- **实现过程**： 在 Android Studio 中新建项目，模板选择 **Empty Activity**，开发语言选用 **Kotlin**。项目创建后，等待 Gradle 依赖项同步完成，并在模拟器上成功运行了默认生成的 "Hello Android" 界面。

- **实验截图**

  <img src="images\helloAndroid.png" style="zoom:50%;" />

### 任务二：实践 Compose 布局

- **实现过程**： 按照 CSDN 教程指导，学习了 Jetpack Compose 中基础的声明式组件和状态管理。实现了包含列表项展开与折叠效果的测试界面。掌握了 Column、Row、Spacer 以及 Composable 函数的状态重组基本概念。

- **实验截图**

  **步骤 1：微调界面**

  使用Surface包装Text组件，并调用了 Material 3 主题中定义的primary颜色作为背景。

<img src="images\1.png" alt="1" style="zoom:50%;" />

**步骤 2：使用修饰符**

在Text追加了内边距修饰符modifier.padding(24.dp

<img src="images\2.png" alt="2" style="zoom:50%;" />

**步骤 3：提取可重复使用的 Composable 与基础骨架**

为了提高代码的可读性，将主布局逻辑封装在MyApp函数中。

<img src="\images\3.png" alt="3" style="zoom:50%;" />

**步骤 4：引入 Column 垂直排列组件**

为了解决多个视图组件在屏幕上的纵向堆叠问题，将Text放入Column布局中。

<img src="images\4.png" alt="4" style="zoom:50%;" />

**步骤 5：使用 Kotlin 循环渲染列表与指定 Preview 尺寸**

借助 Kotlin 极其高效的语法特性，使用for循环批量将名字列表渲染进Column中。

<img src="images\5.png" alt="5" style="zoom:50%;" />

**步骤 6&7：利用 Row 排列及 ElevatedButton、状态管理与动态重组**

使用Row组件实现横向排列，将文本列与新增的ElevatedButton按钮水平排开。给Column添加Modifier.weight(1f)权重属性，使其能够弹性占满除按钮外的所有剩余空间，从而将按钮自然推向右侧对齐。

为了赋予界面动态交互能力，引入remember和mutableStateOf来创建并在重组过程中保留一个 Boolean 类型的可观察状态变量 `expanded`。点击按钮时会触发该值的切换，随之动态改变文字下方的边距extraPadding，并且让按钮上的文字在 "Show more" 与 "Show less" 之间自动切换。

<img src="images\6.png" alt="6" style="zoom:50%;" />

### 任务三：完成面向 AI 应用的 Compose 布局

- **界面设计要求**：

  - **顶部栏**：显示应用标题与操作入口（`LiteRT AI Demo`）。
  - **预览区**：使用 `Box` 组件进行占位（后续可接入 CameraX 相机预览）。
  - **结果区**：使用 `Card` + `Column` 组件展示模型名称、识别结果、置信度、推理时间。
  - **按钮区**：使用 `Row` / `Column` 排列“拍照识别”、“相册导入”、“切换模型”、“清空结果”四个按钮。

- **系统配置文件：`AndroidManifest.xml`**

  此配置文件定义了应用的入口 Activity 为 `.MainActivity`，并且将其导出的 `intent-filter` 设置为主入口和桌面图标。

  ```
  <?xml version="1.0" encoding="utf-8"?>
  <manifest xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"
      xmlns:tools="[http://schemas.android.com/tools](http://schemas.android.com/tools)">
  
      <application
          android:allowBackup="true"
          android:dataExtractionRules="@xml/data_extraction_rules"
          android:fullBackupContent="@xml/backup_rules"
          android:icon="@mipmap/ic_launcher"
          android:label="@string/app_name"
          android:roundIcon="@mipmap/ic_launcher_round"
          android:supportsRtl="true"
          android:theme="@style/Theme.LiteRTAIdemo">
          <activity
              android:name=".MainActivity"
              android:exported="true"
              android:label="@string/app_name"
              android:theme="@style/Theme.LiteRTAIdemo">
              <intent-filter>
                  <action android:name="android.intent.action.MAIN" />
  
                  <category android:name="android.intent.category.LAUNCHER" />
              </intent-filter>
          </activity>
      </application>
  
  </manifest>
  ```

- 主页面布局实现：`MainActivity.kt` 核心逻辑

  页面结构按要求进行了合理的横向与纵向嵌套。预览区使用灰色的 `Box` 留空以便后续集成 CameraX；识别结果区使用 `Card` 加速层级的包裹，底部控制按钮则利用两行 `Row`，分别设置 `weight(1f)` 来保证它们能够自适应平分屏幕宽度。

  ```
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun LiteRTAIDemoScreen() {
      Column(
          modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
          // 1. 顶部栏 (TopAppBar)
          TopAppBar(
              title = { Text("LiteRT AI Demo", fontWeight = FontWeight.Bold) }
          )
  
          // 2. 预览区：用 Box 占位 (后续接入 CameraX)
          Box(
              modifier = Modifier
                  .fillMaxWidth()
                  .height(240.dp)
                  .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
          ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Icon(
                      imageVector = Icons.Default.PlayArrow,
                      contentDescription = "Camera Preview",
                      modifier = Modifier.size(48.dp),
                      tint = Color.Gray
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text("Camera Preview", color = Color.Gray)
              }
          }
  
          // 3. 结果区：使用 Card 展示置信度和推理耗时
          Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
          ) {
              Column(modifier = Modifier.padding(16.dp)) {
                  Text(text = "Model: MobileNet", fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(text = "Result: Cat")
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(text = "Confidence: 96.2%")
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(text = "Time: 28 ms")
              }
          }
  
          Spacer(modifier = Modifier.weight(1f))
  
          // 4. 按钮区：通过两行自适应 Row 实现按钮平分对齐
          Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                  Button(onClick = { /* 拍照识别 */ }, modifier = Modifier.weight(1f)) {
                      Text("拍照识别")
                  }
                  Button(onClick = { /* 相册导入 */ }, modifier = Modifier.weight(1f)) {
                      Text("相册导入")
                  }
              }
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                  Button(onClick = { /* 切换模型 */ }, modifier = Modifier.weight(1f)) {
                      Text("切换模型")
                  }
                  Button(onClick = { /* 清空结果 */ }, modifier = Modifier.weight(1f)) {
                      Text("清空结果")
                  }
              }
          }
      }
  }
  ```

- **实验结果**

  <img src="\images\demo.png" style="zoom:50%;" />

## 三、 实验总结

通过这三个逐步递进的任务，我掌握了在 Android 开发中运用 Compose 声明式布局的基本方法。与传统的 XML 布局相比，Compose 减少了大量的模板代码，通过 `Column` 和 `Row` 的嵌套，配合权重 `weight(1f)` 能够非常快速、直观地实现复杂界面的等宽自适应布局。