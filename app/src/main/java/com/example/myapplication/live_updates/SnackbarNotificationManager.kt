/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication.live_updates

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R

object SnackbarNotificationManager {
    private const val TAG = "SnackbarNotifMgr"
    private lateinit var notificationManager: NotificationManager
    private lateinit var appContext: Context
    const val CHANNEL_ID = "live_updates_channel_id"
    private const val CHANNEL_NAME = "Live Order Updates"
    internal const val NOTIFICATION_ID = 1234
    private const val TOTAL_DURATION = 20000L // 20秒总时长

    fun initialize(context: Context) {
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initialize(context, notifManager)
    }

    fun initialize(context: Context, notifManager: NotificationManager) {
        notificationManager = notifManager
        appContext = context
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Order tracking notifications"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun isLiveUpdatesSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
    }

    fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 启动前台服务
            val serviceIntent = Intent(appContext, OrderTrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appContext.startForegroundService(serviceIntent)
            } else {
                appContext.startService(serviceIntent)
            }
        }
    }

    // 停止追踪
    fun stop() {
        val serviceIntent = Intent(appContext, OrderTrackingService::class.java)
        appContext.stopService(serviceIntent)
    }
}

// 前台服务用于持续更新通知
class OrderTrackingService : Service() {

    private lateinit var notificationManager: NotificationManager
    private var countDownTimer: CountDownTimer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 500L // 每500ms更新一次进度
    private var startTime = 0L
    private val totalDuration = 20000L // 20秒

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(SnackbarNotificationManager.NOTIFICATION_ID, buildNotification(0, 100))
        startProgressUpdates()
        return START_NOT_STICKY
    }

    private fun startProgressUpdates() {
        startTime = System.currentTimeMillis()

        // 使用 Handler 持续更新进度
        val progressRunnable = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = ((elapsed * 100) / totalDuration).toInt().coerceIn(0, 100)

                // 更新通知
                val notification = buildNotification(progress, 100)
                notificationManager.notify(SnackbarNotificationManager.NOTIFICATION_ID, notification)

                // 进度未完成时继续更新
                if (progress < 100) {
                    handler.postDelayed(this, updateInterval)
                } else {
                    // 完成时显示最终通知
                    showCompletedNotification()
                    stopSelf()
                }
            }
        }

        handler.post(progressRunnable)
    }

    private fun buildNotification(progress: Int, max: Int): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = when {
            progress < 25 -> "Order placed - Confirming with bakery..."
            progress < 50 -> "Preparing your order..."
            progress < 75 -> "Your order is on the way!"
            progress < 100 -> "Your order is arriving!"
            else -> "Your order has been delivered!"
        }

        val builder = NotificationCompat.Builder(this, SnackbarNotificationManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.small_icon)
            .setContentTitle("Order Tracking")
            .setContentText(contentText)
            .setProgress(max, progress, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)

        // API 36+ 使用增强的 ProgressStyle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            builder.setStyle(buildProgressStyle(progress))
        }

        // 最终完成状态
        if (progress >= 100) {
            builder
                .setContentTitle("Order Complete!")
                .setContentText("Enjoy your meal!")
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.check_circle)
        }

        return builder.build()
    }

    @Suppress("NewApi")
    private fun buildProgressStyle(progress: Int): NotificationCompat.ProgressStyle {
        val pointColor = android.graphics.Color.rgb(236, 183, 255)
        val segmentColor = android.graphics.Color.rgb(134, 247, 250)

        val style = NotificationCompat.ProgressStyle()
            .setProgressPoints(
                listOf(
                    NotificationCompat.ProgressStyle.Point(25).setColor(pointColor),
                    NotificationCompat.ProgressStyle.Point(50).setColor(pointColor),
                    NotificationCompat.ProgressStyle.Point(75).setColor(pointColor),
                    NotificationCompat.ProgressStyle.Point(100).setColor(pointColor)
                )
            )
            .setProgressSegments(
                listOf(
                    NotificationCompat.ProgressStyle.Segment(25).setColor(segmentColor),
                    NotificationCompat.ProgressStyle.Segment(25).setColor(segmentColor),
                    NotificationCompat.ProgressStyle.Segment(25).setColor(segmentColor),
                    NotificationCompat.ProgressStyle.Segment(25).setColor(segmentColor)
                )
            )

        // 根据当前进度高亮点
        val highlightedPoints = mutableListOf<NotificationCompat.ProgressStyle.Point>()
        if (progress >= 25) highlightedPoints.add(NotificationCompat.ProgressStyle.Point(25).setColor(pointColor))
        if (progress >= 50) highlightedPoints.add(NotificationCompat.ProgressStyle.Point(50).setColor(pointColor))
        if (progress >= 75) highlightedPoints.add(NotificationCompat.ProgressStyle.Point(75).setColor(pointColor))
        if (progress >= 100) highlightedPoints.add(NotificationCompat.ProgressStyle.Point(100).setColor(pointColor))

        style.setProgressPoints(highlightedPoints)

        return style
    }

    private fun showCompletedNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, SnackbarNotificationManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.check_circle)
            .setContentTitle("Order Delivered!")
            .setContentText("Enjoy your meal!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setOngoing(false)
            .build()

        notificationManager.notify(SnackbarNotificationManager.NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        countDownTimer?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
