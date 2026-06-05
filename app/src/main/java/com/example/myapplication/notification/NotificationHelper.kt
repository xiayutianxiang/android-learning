package com.example.myapplication.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R

object NotificationHelper {

    private const val CHANNEL_BIG_TEXT = "big_text_channel"
    private const val CHANNEL_CALL = "call_channel"
    private const val CHANNEL_BACKGROUND = "background_channel"
    private const val CHANNEL_PROGRESS = "progress_channel"
    private const val CHANNEL_METRICS = "metrics_channel"

    const val NOTIFICATION_ID_BIG_TEXT = 1
    const val NOTIFICATION_ID_CALL = 2
    const val NOTIFICATION_ID_BACKGROUND = 3
    const val NOTIFICATION_ID_PROGRESS = 4
    const val NOTIFICATION_ID_METRICS = 5

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val bigTextChannel = NotificationChannel(
            CHANNEL_BIG_TEXT,
            "大文本通知",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "展示 BigTextStyle 样式的大文本通知"
            enableLights(true)
            enableVibration(true)
        }

        val callChannel = NotificationChannel(
            CHANNEL_CALL,
            "通话通知",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "展示 CallStyle 样式的实时通话通知"
            enableLights(true)
            enableVibration(true)
            setSound(null, null)
        }

        val backgroundChannel = NotificationChannel(
            CHANNEL_BACKGROUND,
            "后台通知",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "应用进入后台时发送的通知"
            enableLights(true)
            enableVibration(true)
        }

        val progressChannel = NotificationChannel(
            CHANNEL_PROGRESS,
            "进度通知",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "展示下载或处理进度的通知"
        }

        val metricsChannel = NotificationChannel(
            CHANNEL_METRICS,
            "实时比分",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "展示体育比赛实时比分和统计数据"
            enableLights(true)
            enableVibration(true)
        }

        notificationManager.createNotificationChannels(
            listOf(bigTextChannel, callChannel, backgroundChannel, progressChannel, metricsChannel)
        )
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createPendingIntent(
        context: Context,
        requestCode: Int = 0
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun showBigTextNotification(context: Context) {
        if (!hasNotificationPermission(context)) return

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(
                "这是 BigTextStyle 的详细内容区域，可以显示更长的文本信息。" +
                        "Android 16 对通知系统进行了多项增强，包括更好的实时通知支持、" +
                        "改进的动画效果以及更智能的通知分组。点击此通知可以打开应用。"
            )
            .setBigContentTitle("BigTextStyle 实时通知")
            .setSummaryText("Android 16 通知演示")

        val notification = NotificationCompat.Builder(context, CHANNEL_BIG_TEXT)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Android 16 实时通知")
            .setRequestPromotedOngoing(true)
            .setOngoing(true)
            .setContentText("点击查看 BigTextStyle 详情")
            .setStyle(bigTextStyle)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(createPendingIntent(context, NOTIFICATION_ID_BIG_TEXT))
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()

        sendNotification(context, NOTIFICATION_ID_BIG_TEXT, notification)
    }

    fun showCallNotification(
        context: Context,
        callerName: String = "未知联系人",
        callerNumber: String = "+86 138 0000 1234"
    ) {
        if (!hasNotificationPermission(context)) return

        val caller = Person.Builder()
            .setName(callerName)
            .setUri("tel:$callerNumber")
            .build()

        val callStyle = NotificationCompat.CallStyle.forIncomingCall(
            caller,
            createPendingIntent(context, NOTIFICATION_ID_CALL + 100),
            createPendingIntent(context, NOTIFICATION_ID_CALL + 101)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_CALL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(callerName)
            .setContentText("Android 16 来电")
            .setStyle(callStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setFullScreenIntent(createPendingIntent(context, NOTIFICATION_ID_CALL + 200), true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()

        sendNotification(context, NOTIFICATION_ID_CALL, notification)
    }

    fun cancelCallNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_CALL)
    }

    fun showBackgroundNotification(context: Context) {
        if (!hasNotificationPermission(context)) return

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(
                "应用已进入后台 5 秒，这是后台实时通知。" +
                        "Android 16 增强了通知系统，支持更丰富的样式和更好的实时性能。"
            )
            .setBigContentTitle("后台通知")
            .setSummaryText("应用已进入后台")

        val notification = NotificationCompat.Builder(context, CHANNEL_BACKGROUND)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Android 16 实时通知")
            .setContentText("应用已进入后台 5 秒")
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(createPendingIntent(context, NOTIFICATION_ID_BACKGROUND))
            .setAutoCancel(true)
            .build()

        sendNotification(context, NOTIFICATION_ID_BACKGROUND, notification)
    }

    fun showProgressNotification(context: Context, title: String = "文件下载", progress: Int = 0) {
        if (!hasNotificationPermission(context)) return

        val isIndeterminate = progress < 0

        val notification = NotificationCompat.Builder(context, CHANNEL_PROGRESS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(if (isIndeterminate) "正在处理..." else "下载进度: $progress%")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(!isIndeterminate && progress < 100)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, isIndeterminate)
            .setContentIntent(createPendingIntent(context, NOTIFICATION_ID_PROGRESS))
            .setAutoCancel(!isIndeterminate && progress >= 100)
            .build()

        sendNotification(context, NOTIFICATION_ID_PROGRESS, notification)
    }

    fun showMetricsNotification(context: Context) {
        if (!hasNotificationPermission(context)) return

        // 硬编码的比赛数据
        val homeTeam = "主队"
        val awayTeam = "客队"
        val homeScore = 2
        val awayScore = 1
        val matchTime = "45'"
        val homePossession = 55
        val awayPossession = 45
        val homeShots = 12
        val awayShots = 8

        val homeTeamIcon = "⚽"
        val awayTeamIcon = "⚽"

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            val inboxStyle = NotificationCompat.MetricStyle()
                .addMetric(
                    NotificationCompat.Metric(
                        NotificationCompat.Metric.FixedText("主队"),
                        homeScore.toString()
                    )
                )
                .addMetric(
                    NotificationCompat.Metric(
                        NotificationCompat.Metric.FixedText("客队"),
                        awayScore.toString()
                    )
                )

            val notification = NotificationCompat.Builder(context, CHANNEL_METRICS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("$homeTeam $homeScore - $awayScore $awayTeam")
                .setContentText("实时比分通知 - MetricStyle 替代方案")
                .setStyle(inboxStyle)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setRequestPromotedOngoing(true)
                .setOngoing(true)
                .setOnlyAlertOnce(false)
                .setContentIntent(createPendingIntent(context, NOTIFICATION_ID_METRICS))
                .build()
            sendNotification(context, NOTIFICATION_ID_METRICS, notification)
        }*/
    }

    fun cancelMetricsNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_METRICS)
    }

    private fun sendNotification(
        context: Context,
        notificationId: Int,
        notification: android.app.Notification
    ) {
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // 权限不足
        }
    }
}
