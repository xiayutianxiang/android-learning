package com.example.myapplication

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.myapplication.notification.CallNotificationService
import com.example.myapplication.notification.MetricsNotificationService
import com.example.myapplication.notification.NotificationHelper

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val backgroundRunnable = Runnable {
        NotificationHelper.showBackgroundNotification(this)
    }

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            handler.postDelayed(backgroundRunnable, 5000)
        }

        override fun onStart(owner: LifecycleOwner) {
            handler.removeCallbacks(backgroundRunnable)
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.showBigTextNotification(this)
        } else {
            Toast.makeText(this, "通知权限被拒绝", Toast.LENGTH_SHORT).show()
        }
    }

    private var isCallActive = false
    private var isDownloading = false
    private var downloadProgress = 0
    private var isMatchActive = false

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (isDownloading) {
                downloadProgress += 10
                if (downloadProgress > 100) {
                    downloadProgress = 100
                    NotificationHelper.showProgressNotification(
                        this@MainActivity,
                        "下载完成",
                        100
                    )
                    isDownloading = false
                    showStyleInfo("ProgressStyle", "下载已完成")
                } else {
                    NotificationHelper.showProgressNotification(
                        this@MainActivity,
                        "正在下载文件",
                        downloadProgress
                    )
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        NotificationHelper.createNotificationChannels(this)
        setupButtons()
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(backgroundRunnable)
        handler.removeCallbacks(progressRunnable)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_big_text).setOnClickListener {
            requestNotificationPermissionAndShow()
        }

        findViewById<Button>(R.id.btn_call).setOnClickListener {
            if (!NotificationHelper.hasNotificationPermission(this)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return@setOnClickListener
            }
            toggleCallNotification()
        }

        findViewById<Button>(R.id.btn_progress).setOnClickListener {
            if (!NotificationHelper.hasNotificationPermission(this)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return@setOnClickListener
            }
            toggleProgressNotification()
        }

        findViewById<Button>(R.id.btn_metric).setOnClickListener {
            if (!NotificationHelper.hasNotificationPermission(this)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return@setOnClickListener
            }
            toggleMetricsNotification()
        }
    }

    private fun requestNotificationPermissionAndShow() {
        when {
            NotificationHelper.hasNotificationPermission(this) -> {
                NotificationHelper.showBigTextNotification(this)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Toast.makeText(this, "需要通知权限才能显示通知", Toast.LENGTH_SHORT).show()
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun toggleCallNotification() {
        isCallActive = !isCallActive
        if (isCallActive) {
            CallNotificationService.startIncomingCall(this, "张三", "+86 138 0000 1234")
            showStyleInfo("CallStyle", "通话通知已显示，包含来电人信息和接听/拒绝按钮")
        } else {
            CallNotificationService.endCall(this)
            showStyleInfo("CallStyle", "通话通知已取消")
        }
    }

    private fun toggleProgressNotification() {
        isDownloading = !isDownloading
        if (isDownloading) {
            downloadProgress = 0
            handler.post(progressRunnable)
            showStyleInfo("ProgressStyle", "正在下载，通知会实时更新进度条")
        } else {
            handler.removeCallbacks(progressRunnable)
            NotificationHelper.showProgressNotification(this, "下载已取消", 0)
            showStyleInfo("ProgressStyle", "下载已取消")
        }
    }

    private fun toggleMetricsNotification() {
        isMatchActive = !isMatchActive
        if (isMatchActive) {
            // 使用 Foreground Service 在后台持续更新通知
            MetricsNotificationService.startMatch(this)
            showStyleInfo("MetricStyle", "实时比分通知已显示，后台持续更新中...")
        } else {
            MetricsNotificationService.stopMatch(this)
            showStyleInfo("MetricStyle", "比分通知已取消")
        }
    }

    private fun showStyleInfo(title: String, info: String) {
        findViewById<TextView>(R.id.tv_style_title).text = title
        findViewById<TextView>(R.id.tv_style_info).text = info
    }
}
