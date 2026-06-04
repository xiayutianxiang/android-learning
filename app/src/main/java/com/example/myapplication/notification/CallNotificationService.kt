package com.example.myapplication.notification

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.example.myapplication.MainActivity
import com.example.myapplication.R

class CallNotificationService : Service() {

    companion object {
        private const val CHANNEL_CALL = "call_channel"
        const val NOTIFICATION_ID_CALL = 2
        private const val ACTION_SHOW_CALL = "com.example.myapplication.SHOW_CALL"
        private const val ACTION_END_CALL = "com.example.myapplication.END_CALL"
        private const val EXTRA_CALLER_NAME = "caller_name"
        private const val EXTRA_CALLER_NUMBER = "caller_number"

        fun startIncomingCall(
            context: android.content.Context,
            callerName: String,
            callerNumber: String
        ) {
            val intent = Intent(context, CallNotificationService::class.java).apply {
                action = ACTION_SHOW_CALL
                putExtra(EXTRA_CALLER_NAME, callerName)
                putExtra(EXTRA_CALLER_NUMBER, callerNumber)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun endCall(context: android.content.Context) {
            val intent = Intent(context, CallNotificationService::class.java).apply {
                action = ACTION_END_CALL
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW_CALL -> {
                val callerName = intent.getStringExtra(EXTRA_CALLER_NAME) ?: "未知联系人"
                val callerNumber = intent.getStringExtra(EXTRA_CALLER_NUMBER) ?: "+86 138 0000 1234"
                showCallNotification(callerName, callerNumber)
            }
            ACTION_END_CALL -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_CALL,
                "通话通知",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "展示 CallStyle 样式的实时通话通知"
                enableLights(true)
                enableVibration(true)
                setSound(null, null)
            }
            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showCallNotification(callerName: String, callerNumber: String) {
        createNotificationChannel()

        val caller = Person.Builder()
            .setName(callerName)
            .setUri("tel:$callerNumber")
            .build()

        val answerIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "answer")
        }
        val answerPendingIntent = PendingIntent.getActivity(
            this, 101, answerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val rejectIntent = Intent(this, CallNotificationService::class.java).apply {
            action = ACTION_END_CALL
        }
        val rejectPendingIntent = PendingIntent.getService(
            this, 102, rejectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 200, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val callStyle = NotificationCompat.CallStyle.forIncomingCall(
            caller,
            answerPendingIntent,
            rejectPendingIntent
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_CALL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(callerName)
            .setContentText("Android 16 来电")
            .setStyle(callStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()

        startForeground(NOTIFICATION_ID_CALL, notification)
    }
}
