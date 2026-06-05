---
name: notification-style-demo-design
description: Android 通知四种样式 (BigText/Call/Progress/Metric) Demo 设计文档
metadata:
  type: spec
  date: 2026-06-04
---

# Notification Style Demo 设计文档

## 概述

实现一个 Android 通知样式学习 Demo，页面包含四个按钮，点击分别显示 BigTextStyle、CallStyle、ProgressStyle 和 MetricStyle 四种通知，并展示各样式在 Android 16 上的区别。

## 目标

- 学习 BigTextStyle、CallStyle、ProgressStyle、MetricStyle 四种通知样式的区别
- 覆盖内容：视觉布局差异、API 使用差异、适用场景差异

## 页面结构

| Activity | 用途 |
|----------|------|
| MainActivity | 主页面，4个按钮 + 各样式说明 |
| BigTextDetailActivity | BigText 样式详情页 |
| CallDetailActivity | Call 样式详情页 |
| ProgressDetailActivity | Progress 样式详情页 |
| MetricDetailActivity | Metric 样式详情页 |

## 功能流程

1. **主页面交互**
   - 显示4个按钮：BigText、Call、Progress、Metric
   - 按钮下方/上方显示当前选中样式的说明（特点、API 用法、适用场景）
   - 点击按钮 → 发送对应样式通知 + 高亮该样式说明

2. **通知交互**
   - 点击通知 → 跳转到对应详情页面
   - 通知 Intent 携带样式类型参数

3. **详情页面**
   - 显示完整的样式信息
   - 包含：样式简介、API 示例代码、适用场景说明

## 核心组件

| 组件 | 职责 |
|------|------|
| NotificationHelper | 封装四种通知的创建逻辑 |
| NotificationActivity | 通知处理中转 Activity |
| BigTextDetailActivity | BigText 详情 |
| CallDetailActivity | Call 详情 |
| ProgressDetailActivity | Progress 详情 |
| MetricDetailActivity | Metric 详情 |

## 技术栈

- 语言：Kotlin
- UI：传统 View + XML 布局
- Min SDK：24
- Target/Compile SDK：36 (Android 16)
- 权限：POST_NOTIFICATIONS

## 文件结构

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt                    # 主页面（4个按钮 + 样式说明）
├── notification/
│   ├── NotificationHelper.kt         # 通知创建工具类
│   ├── NotificationActivity.kt        # 通知点击处理中转页
│   └── detail/
│       ├── BigTextDetailActivity.kt
│       ├── CallDetailActivity.kt
│       ├── ProgressDetailActivity.kt
│       └── MetricDetailActivity.kt
└── widget/
    └── CountdownView.kt

app/src/main/res/layout/
├── activity_main.xml
├── activity_notification.xml
├── activity_big_text_detail.xml
├── activity_call_detail.xml
├── activity_progress_detail.xml
└── activity_metric_detail.xml
```

## 通知样式说明

### BigTextStyle
- 简介：支持显示大段文本内容
- API：`NotificationCompat.BigTextStyle`
- 适用场景：消息应用、长文本内容展示

### CallStyle
- 简介：来电/视频通话样式，带头像、时长显示
- API：`NotificationCompat.CallStyle`
- 适用场景：VoIP 通话、视频通话、来电提醒

### ProgressStyle
- 简介：显示下载/上传进度
- API：`NotificationCompat.BigPictureStyle` (实为 ProgressStyle)
- 适用场景：文件下载、系统更新、后台任务进度

### MetricStyle (Android 16 新增)
- 简介：指标/统计数据展示样式
- API：`NotificationCompat.MetricStyle`
- 适用场景：健身数据、实时指标、监控数据
