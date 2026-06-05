# Notification Style Demo Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现一个 Android 通知样式学习 Demo，页面包含四个按钮，点击分别显示 BigTextStyle、CallStyle、ProgressStyle 和 MetricStyle 四种通知。

**Architecture:** 采用传统 View + XML 布局，所有通知相关类放在 `notification/` 包下，详情页通过 Intent 携带参数跳转到对应页面。

**Tech Stack:** Kotlin / View + XML / Android SDK 36 / NotificationCompat

---

## 文件结构

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt                    # 修改：4个按钮 + 样式说明
├── notification/
│   ├── NotificationHelper.kt         # 新增：通知工具类
│   ├── NotificationActivity.kt        # 新增：中转页
│   └── detail/
│       ├── BigTextDetailActivity.kt  # 新增
│       ├── CallDetailActivity.kt     # 新增
│       ├── ProgressDetailActivity.kt # 新增
│       └── MetricDetailActivity.kt   # 新增
└── widget/
    └── CountdownView.kt              # 保留

app/src/main/res/layout/
├── activity_main.xml                 # 修改
├── activity_notification.xml         # 新增
├── activity_big_text_detail.xml      # 新增
├── activity_call_detail.xml          # 新增
├── activity_progress_detail.xml       # 新增
└── activity_metric_detail.xml        # 新增
```

---

## Task 1: 修改 MainActivity 布局文件

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`

- [ ] **Step 1: 替换布局文件内容**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="通知样式 Demo"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_gravity="center_horizontal"
            android:layout_marginBottom="24dp"/>

        <Button
            android:id="@+id/btn_big_text"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="BigTextStyle"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_call"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="CallStyle"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_progress"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="ProgressStyle"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_metric"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="MetricStyle"
            android:layout_marginBottom="24dp"/>

        <TextView
            android:id="@+id/tv_style_title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="选择上方按钮查看样式说明"
            android:textSize="18sp"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:id="@+id/tv_style_info"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:lineSpacingExtra="4dp"/>

    </LinearLayout>
</ScrollView>
```

---

## Task 2: 创建 NotificationHelper 通知工具类

**Files:**
- Create: `app/src/main/java/com/example/myapplication/notification/NotificationHelper.kt`

- [ ] **Step 1: 创建通知工具类**

```kotlin
package com.example.myapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R

object NotificationHelper {

    const val CHANNEL_ID = "notification_style_demo"
    const val EXTRA_STYLE_TYPE = "style_type"

    const val STYLE_BIG_TEXT = "big_text"
    const val STYLE_CALL = "call"
    const val STYLE_PROGRESS = "progress"
    const val STYLE_METRIC = "metric"

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "通知样式 Demo",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "用于演示各种通知样式"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showBigTextStyleNotification(context: Context) {
        createNotificationChannel(context)

        val intent = Intent(context, NotificationActivity::class.java).apply {
            putExtra(EXTRA_STYLE_TYPE, STYLE_BIG_TEXT)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("BigTextStyle 通知")
            .setContentText("这是一条 BigTextStyle 通知示例")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("这是一条 BigTextStyle 通知示例。BigTextStyle 支持显示大段文本内容，适用于消息应用、长文本内容展示等场景。当通知内容较长时，使用 BigTextStyle 可以完整显示所有文本内容，避免被截断。"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1, notification)
    }

    fun showCallStyleNotification(context: Context) {
        createNotificationChannel(context)

        val intent = Intent(context, NotificationActivity::class.java).apply {
            putExtra(EXTRA_STYLE_TYPE, STYLE_CALL)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("CallStyle 通知")
            .setContentText("来电：张三")
            .setStyle(NotificationCompat.CallStyle.forIncomingCall(
                "张三",
                null,
                pendingIntent
            ))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(2, notification)
    }

    fun showProgressStyleNotification(context: Context) {
        createNotificationChannel(context)

        val intent = Intent(context, NotificationActivity::class.java).apply {
            putExtra(EXTRA_STYLE_TYPE, STYLE_PROGRESS)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("ProgressStyle 通知")
            .setContentText("下载进度: 45%")
            .setProgress(100, 45, false)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(3, notification)
    }

    fun showMetricStyleNotification(context: Context) {
        createNotificationChannel(context)

        val intent = Intent(context, NotificationActivity::class.java).apply {
            putExtra(EXTRA_STYLE_TYPE, STYLE_METRIC)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("MetricStyle 通知")
            .setContentText("今日步数: 8,542 步")
            .setStyle(NotificationCompat.MetricStyle()
                .addMetric("步数", "8,542", "步")
                .addMetric("距离", "6.2", "公里")
                .addMetric("卡路里", "320", "千卡"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(4, notification)
    }

    fun getStyleInfo(styleType: String): String {
        return when (styleType) {
            STYLE_BIG_TEXT -> """
                |【BigTextStyle】
                |
                |特点：
                |• 支持显示大段文本内容
                |• 文本不会在通知栏被截断
                |• 可以添加摘要文本
                |
                |API 用法：
                |NotificationCompat.BigTextStyle()
                |    .bigText("大段文本内容")
                |    .setSummaryText("摘要")
                |
                |适用场景：
                |• 消息应用的长文本内容
                |• 邮件正文展示
                |• 公告通知
            """.trimMargin()

            STYLE_CALL -> """
                |【CallStyle】
                |
                |特点：
                |• 专为来电/视频通话设计
                |• 支持头像、联系人名称显示
                |• 可显示通话时长
                |• 带有接听/拒绝按钮
                |
                |API 用法：
                |NotificationCompat.CallStyle.forIncomingCall(
                |    contactName, contactIcon, intent
                |)
                |
                |适用场景：
                |• VoIP 通话应用
                |• 视频通话
                |• 普通来电提醒
            """.trimMargin()

            STYLE_PROGRESS -> """
                |【ProgressStyle】
                |
                |特点：
                |• 显示下载/上传进度条
                |• 支持确定和不确定进度
                |• 实时更新进度百分比
                |• 自动处理进度通知的折叠/展开状态
                |
                |API 用法：
                |setProgress(max, progress, indeterminate)
                |
                |适用场景：
                |• 文件下载/上传
                |• 系统更新
                |• 后台任务进度
            """.trimMargin()

            STYLE_METRIC -> """
                |【MetricStyle】(Android 16 新增)
                |
                |特点：
                |• 用于展示指标/统计数据
                |• 支持多个指标并行显示
                |• 每个指标包含数值、单位、标签
                |• 类似卡片式的数据展示
                |
                |API 用法：
                |NotificationCompat.MetricStyle()
                |    .addMetric(label, value, unit)
                |
                |适用场景：
                |• 健身数据（步数、心率、卡路里）
                |• 实时监控数据
                |• 性能指标展示
            """.trimMargin()

            else -> "未知样式"
        }
    }
}
```

---

## Task 3: 创建 NotificationActivity 中转页

**Files:**
- Create: `app/src/main/java/com/example/myapplication/notification/NotificationActivity.kt`
- Create: `app/src/main/res/layout/activity_notification.xml`

- [ ] **Step 1: 创建布局文件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="正在跳转..."
        android:textSize="16sp"/>

</LinearLayout>
```

- [ ] **Step 2: 创建 Activity 类**

```kotlin
package com.example.myapplication.notification

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.notification.detail.BigTextDetailActivity
import com.example.myapplication.notification.detail.CallDetailActivity
import com.example.myapplication.notification.detail.ProgressDetailActivity
import com.example.myapplication.notification.detail.MetricDetailActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val styleType = intent.getStringExtra(NotificationHelper.EXTRA_STYLE_TYPE) ?: run {
            finish()
            return
        }

        val targetActivity = when (styleType) {
            NotificationHelper.STYLE_BIG_TEXT -> BigTextDetailActivity::class.java
            NotificationHelper.STYLE_CALL -> CallDetailActivity::class.java
            NotificationHelper.STYLE_PROGRESS -> ProgressDetailActivity::class.java
            NotificationHelper.STYLE_METRIC -> MetricDetailActivity::class.java
            else -> null
        }

        if (targetActivity != null) {
            startActivity(Intent(this, targetActivity).apply {
                putExtra(NotificationHelper.EXTRA_STYLE_TYPE, styleType)
            })
        }

        finish()
    }
}
```

---

## Task 4: 创建详情页面基类和公共布局

**Files:**
- Create: `app/src/main/java/com/example/myapplication/notification/detail/BaseDetailActivity.kt`
- Create: `app/src/main/res/layout/activity_detail_base.xml`

- [ ] **Step 1: 创建基类布局**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/tv_title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="样式简介"
            android:textSize="16sp"
            android:textStyle="bold"
            android:textColor="#333333"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:id="@+id/tv_intro"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:lineSpacingExtra="4dp"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="API 示例"
            android:textSize="16sp"
            android:textStyle="bold"
            android:textColor="#333333"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:id="@+id/tv_api"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="12sp"
            android:fontFamily="monospace"
            android:background="#f5f5f5"
            android:padding="12dp"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="适用场景"
            android:textSize="16sp"
            android:textStyle="bold"
            android:textColor="#333333"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:id="@+id/tv_scenario"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:lineSpacingExtra="4dp"/>

    </LinearLayout>
</ScrollView>
```

- [ ] **Step 2: 创建基类 Activity**

```kotlin
package com.example.myapplication.notification.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.notification.NotificationHelper

abstract class BaseDetailActivity : AppCompatActivity() {

    protected abstract val styleType: String
    protected abstract val title: String
    protected abstract val intro: String
    protected abstract val apiCode: String
    protected abstract val scenario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setupViews()
    }

    protected fun getLayoutId(): Int {
        return com.example.myapplication.R.layout.activity_detail_base
    }

    protected open fun setupViews() {
        findViewById<android.widget.TextView>(com.example.myapplication.R.id.tv_title).text = title
        findViewById<android.widget.TextView>(com.example.myapplication.R.id.tv_intro).text = intro
        findViewById<android.widget.TextView>(com.example.myapplication.R.id.tv_api).text = apiCode
        findViewById<android.widget.TextView>(com.example.myapplication.R.id.tv_scenario).text = scenario
    }
}
```

---

## Task 5: 创建 BigTextDetailActivity

**Files:**
- Create: `app/src/main/java/com/example/myapplication/notification/detail/BigTextDetailActivity.kt`

- [ ] **Step 1: 创建 BigTextDetailActivity**

```kotlin
package com.example.myapplication.notification.detail

import com.example.myapplication.notification.NotificationHelper

class BigTextDetailActivity : BaseDetailActivity() {

    override val styleType: String = NotificationHelper.STYLE_BIG_TEXT

    override val title: String = "BigTextStyle"

    override val intro: String = """
        BigTextStyle 是 Android 通知中用于显示大段文本内容的样式。当通知内容较长时，使用 BigTextStyle 可以完整显示所有文本内容，避免被系统自动截断。

        主要特点：
        • 支持显示任意长度的文本
        • 展开后可查看完整内容
        • 可配合 SummaryText 显示摘要信息
        • 兼容性好，支持所有 Android 版本
    """.trimIndent()

    override val apiCode: String = """
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("标题")
            .setContentText("简短描述（折叠时显示）")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("这里是完整的大段文本内容，会在通知展开时显示。可以包含多行文字，不会被截断。")
                .setSummaryText("摘要信息"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    """.trimIndent()

    override val scenario: String = """
        • 消息应用：聊天记录、群消息
        • 邮件客户端：邮件正文预览
        • 新闻应用：文章摘要或公告内容
        • 社交应用：长动态、评论内容
        • 系统通知：错误日志、详细提示信息
    """.trimIndent()
}
```

---

## Task 6: 创建 CallDetailActivity

**Files:**
- Create: `app/src/main/java/com/example/myapplication/notification/detail/CallDetailActivity.kt`

- [ ] **Step 1: 创建 CallDetailActivity**

```kotlin
package com.example.myapplication.notification.detail

import com.example.myapplication.notification.NotificationHelper

class CallDetailActivity : BaseDetailActivity() {

    override val styleType: String = NotificationHelper.STYLE_CALL

    override val title: String = "CallStyle"

    override val intro: String = """
        CallStyle 是专为来电和视频通话场景设计的通知样式。它提供了完整的通话交互界面，包括接听、拒绝按钮，以及联系人信息和通话状态显示。

        主要特点：
        • 专为 VoIP 和视频通话设计
        • 支持显示联系人名称和头像
        • 内置接听/拒绝/挂断按钮
        • 可显示视频通话图标
        • 支持显示通话时长
    """.trimIndent()

    override val apiCode: String = """
        // 来电样式
        val incomingCall = NotificationCompat.CallStyle.forIncomingCall(
            "张三",                           // 联系人名称
            null,                             // 联系人头像 (Bitmap)
            pendingIntent                    // 点击事件
        )

        // 呼出样式
        val outgoingCall = NotificationCompat.CallStyle.forOutgoingCall(
            "张三",
            null,
            pendingIntent,
            hangupPendingIntent              // 挂断按钮点击事件
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_call)
            .setStyle(incomingCall)
            .build()
    """.trimIndent()

    override val scenario: String = """
        • VoIP 应用：Skype、微信语音通话
        • 视频通话：Zoom、腾讯会议
        • 传统电话：来电视觉增强
        • 视频聊天应用
        • 在线客服/医疗咨询
    """.trimIndent()
}
```

---

## Task 7: 创建 ProgressDetailActivity

**Files:**
- Create: `app/src/main/java/com/example/myapplication/notification/detail/ProgressDetailActivity.kt`

- [ ] **Step 1: 创建 ProgressDetailActivity**

```kotlin
package com.example.myapplication.notification.detail

import com.example.myapplication.notification.NotificationHelper

class ProgressDetailActivity : BaseDetailActivity() {

    override val styleType: String = NotificationHelper.STYLE_PROGRESS

    override val title: String = "ProgressStyle"

    override val intro: String = """
        ProgressStyle 用于显示长时间运行任务的进度，如文件下载、系统更新等。通知会显示一个进度条，实时反映任务完成百分比。

        主要特点：
        • 实时显示任务进度
        • 支持确定和不确定进度模式
        • 自动处理折叠/展开状态
        • 可更新进度值
        • 任务完成后可自动消失
    """.trimIndent()

    override val apiCode: String = """
        // 确定进度
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle("下载中")
            .setContentText("已完成 $progress%")
            .setProgress(100, progress, false)  // max, current, indeterminate
            .build()

        // 不确定进度（下载速度未知时）
        val indeterminateNotification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle("下载中")
            .setContentText("正在连接...")
            .setProgress(0, 0, true)  // 第三个参数为 true 表示不确定进度
            .build()

        // 更新进度
        notificationBuilder.setProgress(100, newProgress, false)
        notificationManager.notify(notificationId, notificationBuilder.build())
    """.trimIndent()

    override val scenario: String = """
        • 文件下载：APK、文档、图片
        • 文件上传：照片备份、云同步
        • 系统更新：系统版本更新进度
        • 应用安装/更新
        • 视频/音乐缓存
        • 批量文件处理
    """.trimIndent()
}
```

---

## Task 8: 创建 MetricDetailActivity

**Files:**
- Create: `app/src/main/java/com/example/myapplication/notification/detail/MetricDetailActivity.kt`

- [ ] **Step 1: 创建 MetricDetailActivity**

```kotlin
package com.example.myapplication.notification.detail

import com.example.myapplication.notification.NotificationHelper

class MetricDetailActivity : BaseDetailActivity() {

    override val styleType: String = NotificationHelper.STYLE_METRIC

    override val title: String = "MetricStyle (Android 16 新增)"

    override val intro: String = """
        MetricStyle 是 Android 16 (API 36) 新增的通知样式，专门用于展示指标或统计数据。它可以同时显示多个指标项，每个指标包含标签、数值和单位，类似于卡片式的布局。

        主要特点：
        • 支持多个指标并行显示
        • 每个指标包含标签、数值、单位
        • 类似仪表盘/数据卡片的展示效果
        • 专为数据可视化设计
        • 属于 Android 16 新增 API
    """.trimIndent()

    override val apiCode: String = """
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_metrics)
            .setContentTitle("今日运动数据")
            .setContentText("共记录 3 项指标")
            .setStyle(NotificationCompat.MetricStyle()
                .addMetric("步数", "8,542", "步")
                .addMetric("距离", "6.2", "公里")
                .addMetric("卡路里", "320", "千卡"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // API 36+ 可用
        // requires compileSdk 36 and minSdk 24
    """.trimIndent()

    override val scenario: String = """
        • 健身/健康应用：步数、心率、睡眠时长
        • 运动追踪：跑步、骑行、游泳数据
        • 天气应用：温度、湿度、空气质量
        • 股票应用：实时行情数据
        • 设备状态：电量、存储空间、网络速度
        • IoT 设备：传感器数据展示
    """.trimIndent()
}
```

---

## Task 9: 修改 MainActivity

**Files:**
- Modify: `app/src/main/java/com/example/myapplication/MainActivity.kt`

- [ ] **Step 1: 修改 MainActivity**

```kotlin
package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.notification.NotificationHelper

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 权限请求回调
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestNotificationPermission()

        setupButtons()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 已有权限
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun setupButtons() {
        val tvStyleInfo = findViewById<TextView>(R.id.tv_style_info)

        findViewById<Button>(R.id.btn_big_text).setOnClickListener {
            NotificationHelper.showBigTextStyleNotification(this)
            tvStyleInfo.text = NotificationHelper.getStyleInfo(NotificationHelper.STYLE_BIG_TEXT)
        }

        findViewById<Button>(R.id.btn_call).setOnClickListener {
            NotificationHelper.showCallStyleNotification(this)
            tvStyleInfo.text = NotificationHelper.getStyleInfo(NotificationHelper.STYLE_CALL)
        }

        findViewById<Button>(R.id.btn_progress).setOnClickListener {
            NotificationHelper.showProgressStyleNotification(this)
            tvStyleInfo.text = NotificationHelper.getStyleInfo(NotificationHelper.STYLE_PROGRESS)
        }

        findViewById<Button>(R.id.btn_metric).setOnClickListener {
            NotificationHelper.showMetricStyleNotification(this)
            tvStyleInfo.text = NotificationHelper.getStyleInfo(NotificationHelper.STYLE_METRIC)
        }
    }
}
```

---

## Task 10: 更新 AndroidManifest.xml

**Files:**
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: 添加 Activity 注册和权限**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyApplication">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.MyApplication">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity
            android:name=".notification.NotificationActivity"
            android:exported="false"
            android:theme="@style/Theme.MyApplication" />
        <activity
            android:name=".notification.detail.BigTextDetailActivity"
            android:exported="false"
            android:theme="@style/Theme.MyApplication" />
        <activity
            android:name=".notification.detail.CallDetailActivity"
            android:exported="false"
            android:theme="@style/Theme.MyApplication" />
        <activity
            android:name=".notification.detail.ProgressDetailActivity"
            android:exported="false"
            android:theme="@style/Theme.MyApplication" />
        <activity
            android:name=".notification.detail.MetricDetailActivity"
            android:exported="false"
            android:theme="@style/Theme.MyApplication" />
    </application>

</manifest>
```

---

## Task 11: 编译验证

- [ ] **Step 1: 运行构建**

```bash
./gradlew assembleDebug
```

预期：构建成功，生成 APK

---

## 执行选项

**Plan complete and saved to `docs/superpowers/plans/2026-06-04-notification-style-demo.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
