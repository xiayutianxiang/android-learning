package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.live_updates.SnackbarNotificationManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        updatePermissionCardVisibility()
        val message = if (isGranted) "Notification permission granted" else "Notification permission denied"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        SnackbarNotificationManager.initialize(this)
    }

    override fun onResume() {
        super.onResume()
        updatePermissionCardVisibility()
        updateLiveUpdatesSection()
    }

    private fun setupListeners() {
        binding.btnGrantPermission.setOnClickListener {
            requestNotificationPermission()
        }

        binding.btnGoToSettings.setOnClickListener {
            openNotificationSettings()
        }

        binding.btnCheckout.setOnClickListener {
            onCheckout()
        }
    }

    private fun requestNotificationPermission() {
        when {
            // API 33+ 需要运行时权限
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        updatePermissionCardVisibility()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                        binding.permissionRationale.visibility = View.VISIBLE
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    else -> {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            // API < 33 不需要运行时权限
            else -> {
                updatePermissionCardVisibility()
            }
        }
    }

    private fun openNotificationSettings() {
        val intent = when {
            // API 34+ Live Updates 设置
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                Intent(Settings.ACTION_APP_NOTIFICATION_PROMOTION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
            }
            // API 26+ 通知设置
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
            }
            // API < 26 应用详情
            else -> {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
            }
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "无法打开设置", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePermissionCardVisibility() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            binding.permissionCard.visibility = if (hasPermission) View.GONE else View.VISIBLE
            binding.permissionRationale.visibility = if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        } else {
            // API < 33 不需要运行时权限
            binding.permissionCard.visibility = View.GONE
        }
    }

    private fun updateLiveUpdatesSection() {
        // 只有 API 36+ 才显示 Live Updates 增强功能说明
        binding.postPromotedSection.visibility = if (SnackbarNotificationManager.isLiveUpdatesSupported()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun onCheckout() {
        // 检查通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请先授予通知权限", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // 发送通知
        SnackbarNotificationManager.start()
        Toast.makeText(this, getString(R.string.checking_out), Toast.LENGTH_SHORT).show()
    }
}
