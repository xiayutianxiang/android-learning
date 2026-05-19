package com.example.myapplication.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.myapplication.R

class CountdownView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var targetTimeMillis: Long = 0L

    private val cardBackgroundColor = ContextCompat.getColor(context, R.color.card_background)
    private val textColor = ContextCompat.getColor(context, R.color.white)
    private val labelColor = ContextCompat.getColor(context, R.color.label_gray)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = labelColor
        textAlign = Paint.Align.CENTER
    }

    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = cardBackgroundColor
        style = Paint.Style.FILL
    }

    private var remainingMillis: Long = 0L
    private val cardRect = RectF()
    private val cornerRadius = 24f
    private val cardWidth = 112f
    private val cardHeight = 160f
    private val spacing = 16f

    private val labels = arrayOf("天", "小时", "分钟", "秒")

    private val runnable = object : Runnable {
        override fun run() {
            if (targetTimeMillis <= 0) return
            remainingMillis = maxOf(0L, targetTimeMillis - System.currentTimeMillis())
            invalidate()
            if (remainingMillis > 0) {
                postDelayed(this, 100)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = (cardWidth * 4 + spacing * 3).toInt()
        val totalHeight = cardHeight.toInt()
        setMeasuredDimension(totalWidth, totalHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val days = (remainingMillis / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((remainingMillis / (1000 * 60 * 60)) % 24).toInt()
        val minutes = ((remainingMillis / (1000 * 60)) % 60).toInt()
        val seconds = ((remainingMillis / 1000) % 60).toInt()

        val values = arrayOf(days, hours, minutes, seconds)
        val textSize = cardHeight * 0.28f
        val labelSize = cardHeight * 0.1f

        textPaint.textSize = textSize
        labelPaint.textSize = labelSize

        val fontMetrics = textPaint.fontMetrics
        val textY = cardHeight * 0.55f + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent

        for (i in 0..3) {
            val left = i * (cardWidth + spacing)
            cardRect.set(left, 0f, left + cardWidth, cardHeight)
            canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, cardPaint)

            val displayValue = values[i].toString().padStart(2, '0')
            canvas.drawText(displayValue, left + cardWidth / 2, textY, textPaint)
            canvas.drawText(labels[i], left + cardWidth / 2, cardHeight * 0.82f, labelPaint)
        }
    }

    fun setTargetTime(targetTime: Long) {
        targetTimeMillis = targetTime
        remainingMillis = maxOf(0L, targetTimeMillis - System.currentTimeMillis())
        removeCallbacks(runnable)
        post(runnable)
    }

    fun stop() {
        removeCallbacks(runnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(runnable)
    }
}
