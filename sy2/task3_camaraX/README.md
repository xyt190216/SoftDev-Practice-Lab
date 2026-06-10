## 实验 2-3：构建 Android CameraX 应用报告

- **课程名称**：软件项目研发实践
- **实验名称**：构建 Android CameraX 应用
- **学生姓名**：熊依婷
- **学号**：121052023050
- **专业班级**：软工2班
- **指导教师**：林立

## 一、 实验内容

本实验主要基于 Android Jetpack CameraX 库，在 Android 21+ 环境下构建一个具备基础相机硬件调用能力的应用程序。实验核心内容包括：

1. **项目布局设计**：配置 `PreviewView` 及拍照、录制控制按钮。
2. **硬件权限申请**：配置并动态申请相机及麦克风权限。
3. **实现 Preview（预览）**：将摄像头画面实时显示在界面上。
4. **实现 ImageCapture（拍照）**：支持高质量静态图片捕捉并保存到系统相册。
5. **实现 ImageAnalysis（图像分析）**：实时处理每一帧画面，计算平均亮度并输出。
6. **实现 VideoCapture（录像）**：支持录制视频与音频并保存至媒体库。

## 二、 关键配置说明

项目已上传完整 Kotlin 源代码，此处仅摘录最基础的配置文件改动：

### 1. 核心 Gradle 依赖与 ViewBinding 配置 (`build.gradle`)

```
android {
    buildFeatures {
        viewBinding true
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    def camerax_version = "1.1.0-beta01"
    implementation "androidx.camera:camera-core:${camerax_version}"
    implementation "androidx.camera:camera-camera2:${camerax_version}"
    implementation "androidx.camera:camera-lifecycle:${camerax_version}"
    implementation "androidx.camera:camera-video:${camerax_version}"
    implementation "androidx.camera:camera-view:${camerax_version}"
    implementation "androidx.camera:camera-extensions:${camerax_version}"
}
```

### 2. 清单文件权限声明 (`AndroidManifest.xml`)

在 `<application>` 标签前添加以下硬件使用声明：

```
<uses-feature android:name="android.hardware.camera.any" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
```

## 三、 实验结果与截图佐证

以下为我本机/真机运行调试本实验时对应的关键步骤截图验证：

### 1. UI 界面布局设计

使用 `ConstraintLayout` 布局，上方由 `PreviewView`（ID: `viewFinder`）作为取景器占满屏幕，下方通过一条垂直对齐辅助线（Guideline）均分并排列了 “Take Photo” 与 “Start Capture” 两个控制按钮。

> <img src="screenshot\UI.png" alt="UI" style="zoom:50%;" />

### 2. 硬件权限获取

应用首次启动时，系统会根据清单文件声明动态弹出权限请求对话框，提示用户授权“拍摄照片和录制视频”以及“录制音频”权限。授权通过后自动回调 `startCamera()` 启动相机。

> <img src="screenshot\权限获取.png" alt="权限获取" style="zoom:50%;" />

### 3. 实时预览与拍照功能验证

相机启动后，`viewFinder` 实时显示摄像头画面。点击 “Take Photo” 按钮，成功触发 `imageCapture.takePicture` 回调，捕获高质量静态照片并保存至系统相册，同时屏幕下方弹出成功保存的 Toast 提示。照片自动保存到相册。

> <img src="screenshot\拍照.png" alt="拍照" style="zoom:50%;" />

### 4. 视频录像功能验证

点击 “Start Capture” 按钮，录像功能启动，同时控制按钮的文本动态切换为 “Stop Capture”。点击停止录制后，视频媒体流正常保存落盘，并弹出成功写入系统 Movies 目录的路径提示。

> <img src="screenshot\录像.png" alt="录像" style="zoom:50%;" />

### 5. 实时图像分析控制台输出

通过编写 `LuminosityAnalyzer`（图像亮度分析器）并在 `startCamera` 中进行用例绑定，系统大约每秒会回传当前取景框内的图像帧。代码内提取了像素数据并计算出平均亮度，成功在 Android Studio 的 Logcat 控制台中实时打印。

> <img src="screenshot\logcat输出.png" alt="logcat输出" style="zoom:50%;" />

## 四、 实验总结

在本次实验的推进过程中，主要遇到了以下两个典型的工程环境问题，均已排查并解决：

#### 问题一：Android Studio 模板选择与旧版教程的冲突

- **问题描述**：在进行子实验 2.3（构建 CameraX 应用）时，参考教程要求创建一个 "Empty Activity"。但由于使用的是最新版的 Android Studio，"Empty Activity" 默认生成的是纯 Compose 架构的项目，不会自动生成 `res/layout` 文件夹及 XML 布局文件，导致代码无法按照教程继续编写。
- **解决方案**：经过排查 Android Studio 的版本差异，在新建 CameraX 工程时，主动放弃选择 "Empty Activity"，改为选择 **"Empty Views Activity"** 模板。这样系统正确生成了 `activity_main.xml` 和基于 `setContentView` 渲染的 `MainActivity`，顺利接入教程的后续步骤。

#### 问题二：模拟器运行 CameraX 持续卡死与通道断裂 

- **问题描述**：在 CameraX 实验中，代码编译成功并部署到模拟器后，应用频繁停留在主界面不断刷新，或者在请求相机权限时直接崩溃。Logcat 中抛出 `Channel is unrecoverably broken` 和 `Surface failed to disconnect (Error -32 DEAD_OBJECT)` 等底层渲染错误。

- **解决方案**：此问题并非代码逻辑错误，而是模拟器的显卡驱动在处理复杂的视频流和相机硬件映射时资源崩溃导致。通过打开 Android Studio 的 Device Manager，对 Pixel 模拟器进行了以下两步优化：

  1. 将 Graphics 渲染模式从 `Automatic` 强制修改为 **`Software - GLES 2.0`**（软件渲染模式）。

  2. 对模拟器执行了 **Cold Boot Now（冷启动）** 清除了之前卡死的硬件状态和残留锁文件。

     重启后，模拟器成功渲染出了 CameraX 的预览画面，拍照与视频录制功能均恢复正常。