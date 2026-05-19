# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Android 学习项目

## 项目信息
- **用途**: Android 学习项目，用于探索各种库和功能
- **仓库**: https://github.com/xiayutianxiang/android-learning

## 技术栈
- **构建工具**: Gradle (Kotlin DSL) + Version Catalog (`gradle/libs.versions.toml`)
- **语言**: Kotlin
- **UI 框架**: Hybrid — 传统 View（自定义控件）+ Jetpack Compose（主题配置）
- **Min SDK**: 24 | **Target/Compile SDK**: 36
- **Java 版本**: 11
- **包名**: com.example.myapplication

## 架构概览
- **单模块结构**（`app`）
- **UI 层**: 传统 View 用于自定义控件，Compose 用于主题配置
- **自定义控件**: `app/src/main/java/com/example/myapplication/widget/`
- **Compose 主题**: `app/src/main/java/com/example/myapplication/ui/theme/`

## 依赖管理
- 使用 Version Catalog (`gradle/libs.versions.toml`) 管理依赖，禁止内联版本号
- 核心依赖：AndroidX Core KTX、Lifecycle、Activity Compose、Compose BOM、Material3、AppCompat
- 测试：JUnit（单元测试）、Espresso（集成测试）

## 常用命令
```bash
./gradlew build          # 完整构建
./gradlew clean          # 清理构建产物
./gradlew test           # 运行单元测试
./gradlew lint           # 运行 Lint 检查
./gradlew test --tests "com.example.myapplication.ExampleUnitTest"  # 运行单个测试类
```

## 约定
- 这是一个学习项目，会陆续添加各种库
- 新增依赖后请同步更新此文档
- 使用 Version Catalog 管理版本，不要内联版本号
