package com.example.myapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R

class MetricsNotificationService : Service() {

    companion object {
        private const val CHANNEL_METRICS = "metrics_channel"
        const val NOTIFICATION_ID_METRICS = 5
        private const val ACTION_START = "com.example.myapplication.START_METRICS"
        private const val ACTION_STOP = "com.example.myapplication.STOP_METRICS"

        fun startMatch(context: android.content.Context) {
            val intent = Intent(context, MetricsNotificationService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopMatch(context: android.content.Context) {
            val intent = Intent(context, MetricsNotificationService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private var matchTimeSeconds = 75 * 60 + 30
    private var homeScore = 2
    private var awayScore = 1
    private var homePossession = 58
    private var awayPossession = 42
    private var homeShots = 12
    private var awayShots = 7

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateMatch()
            handler.postDelayed(this, 3000)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startMetricsUpdate()
            ACTION_STOP -> stopMetricsUpdate()
        }
        return START_NOT_STICKY
    }

    private fun startMetricsUpdate() {
        createNotificationChannel()
        resetMatchData()
        startForeground(NOTIFICATION_ID_METRICS, buildNotification())
        handler.post(updateRunnable)
    }

    private fun stopMetricsUpdate() {
        handler.removeCallbacks(updateRunnable)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun resetMatchData() {
        matchTimeSeconds = 75 * 60 + 30
        homeScore = 2
        awayScore = 1
        homePossession = 58
        awayPossession = 42
        homeShots = 12
        awayShots = 7
    }

    private fun updateMatch() {
        matchTimeSeconds += 30
        if (matchTimeSeconds >= 90 * 60) {
            matchTimeSeconds = 90 * 60
            updateNotification()
        } else {
            if ((0..9).random() == 0) {
                if ((0..1).random() == 0) {
                    homeScore++
                    homeShots++
                } else {
                    awayScore++
                    awayShots++
                }
            }
            if ((0..4).random() == 0) {
                homePossession = (55..65).random()
                awayPossession = 100 - homePossession
            }
            updateNotification()
        }
    }

    private fun updateNotification() {
        val minutes = matchTimeSeconds / 60
        val seconds = matchTimeSeconds % 60
        val timeStr = String.format("%02d:%02d", minutes, seconds)

        val notification = buildNotification(timeStr)
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID_METRICS, notification)
    }

    private fun buildNotification(timeStr: String = "75:30"): android.app.Notification {
        val homeTeam = "巴塞罗那"
        val awayTeam = "皇家马德里"

        val inboxStyle = NotificationCompat.InboxStyle()
            .addLine("⚽ $homeTeam: $homeScore")
            .addLine("⚽ $awayTeam: $awayScore")
            .addLine("⏱ 比赛时间: $timeStr")
            .addLine("控球率: $homePossession% - $awayPossession%")
            .addLine("射门: $homeShots - $awayShots")
            .setBigContentTitle("$homeTeam $homeScore - $awayScore $awayTeam")
            .setSummaryText("实时比分通知")

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_METRICS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("$homeTeam $homeScore - $awayScore $awayTeam")
            .setContentText("实时比分通知")
            .setStyle(inboxStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_METRICS,
                "实时比分",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "展示体育比赛实时比分和统计数据"
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }
}
