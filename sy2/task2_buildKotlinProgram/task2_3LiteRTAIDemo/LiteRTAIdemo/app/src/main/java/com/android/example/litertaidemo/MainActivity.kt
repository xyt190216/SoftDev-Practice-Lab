package com.android.example.litertaidemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 必须保留这个 MainActivity 类，它是 App 的启动入口
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent 负责将 Compose UI 渲染到屏幕上
        setContent {
            MaterialTheme {
                AIDemoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIDemoScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LiteRT AI Demo") }
            )
        }
    ) { paddingValues ->
        // 外层使用 Column 组织页面
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 预览区 (使用 Box 占位)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 占据大部分高度空间
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Camera Preview", color = Color.Gray)
                }
            }

            // 2. 结果区 (Card + Column 展示信息)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Model: MobileNet")
                    Text("Result: Cat")
                    Text("Confidence: 96.2%")
                    Text("Time: 28 ms")
                }
            }

            // 3. 按钮区 (Column 嵌套 Row 排列)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { /* 拍照逻辑 */ }, modifier = Modifier.weight(1f)) {
                        Text("拍照识别")
                    }
                    Button(onClick = { /* 导入逻辑 */ }, modifier = Modifier.weight(1f)) {
                        Text("相册导入")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { /* 切换模型逻辑 */ }, modifier = Modifier.weight(1f)) {
                        Text("切换模型")
                    }
                    Button(onClick = { /* 清空逻辑 */ }, modifier = Modifier.weight(1f)) {
                        Text("清空结果")
                    }
                }
            }
        }
    }
}