package com.zhuwenhao.flipped.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.ext.toColorHex

class ColorChooserView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), SeekBar.OnSeekBarChangeListener {

    private lateinit var viewARGB: View
    private lateinit var textHexValue: TextView

    private lateinit var sbAlpha: SeekBar
    private lateinit var textAlpha: TextView
    private lateinit var sbRed: SeekBar
    private lateinit var textRed: TextView
    private lateinit var sbGreen: SeekBar
    private lateinit var textGreen: TextView
    private lateinit var sbBlue: SeekBar
    private lateinit var textBlue: TextView

    var color: Int = Color.BLACK
        private set

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_color_chooser, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        viewARGB = findViewById(R.id.viewARGB)
        textHexValue = findViewById(R.id.textHexValue)
        sbAlpha = findViewById(R.id.sbAlpha)
        textAlpha = findViewById(R.id.textAlpha)
        sbRed = findViewById(R.id.sbRed)
        textRed = findViewById(R.id.textRed)
        sbGreen = findViewById(R.id.sbGreen)
        textGreen = findViewById(R.id.textGreen)
        sbBlue = findViewById(R.id.sbBlue)
        textBlue = findViewById(R.id.textBlue)

        sbAlpha.tint(resolveColor())
        sbAlpha.setOnSeekBarChangeListener(this)
        sbRed.tint(Color.RED)
        sbRed.setOnSeekBarChangeListener(this)
        sbGreen.tint(Color.GREEN)
        sbGreen.setOnSeekBarChangeListener(this)
        sbBlue.tint(Color.BLUE)
        sbBlue.setOnSeekBarChangeListener(this)

        setColorARGB(color)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (sbAlpha.progress == 0 && (sbRed.progress != 0 || sbGreen.progress != 0 || sbBlue.progress != 0)) {
            sbAlpha.progress = 255
            return
        }

        when (seekBar.id) {
            R.id.sbAlpha -> textAlpha.text = progress.toString()
            R.id.sbRed -> textRed.text = progress.toString()
            R.id.sbGreen -> textGreen.text = progress.toString()
            R.id.sbBlue -> textBlue.text = progress.toString()
        }

        color = Color.argb(sbAlpha.progress, sbRed.progress, sbGreen.progress, sbBlue.progress)
        viewARGB.background = ColorDrawable(color)
        textHexValue.text = color.toColorHex()

        val tintColor = tintColor(color)
        textHexValue.setTextColor(tintColor)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    private fun resolveColor(): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(android.R.attr.textColorSecondary))
        try {
            return a.getColor(0, 0)
        } finally {
            a.recycle()
        }
    }

    private fun Int.isColorDark(threshold: Double = 0.5): Boolean {
        if (this == Color.TRANSPARENT) {
            return false
        }
        val darkness = 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
        return darkness >= threshold
    }

    private fun tintColor(color: Int) = if (color.isColorDark() && Color.alpha(color) >= 50) {
        Color.WHITE
    } else {
        Color.BLACK
    }

    @Suppress("DEPRECATION")
    private fun SeekBar.tint(color: Int) {
        progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        thumb.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setColorARGB(color: Int) {
        sbAlpha.progress = Color.alpha(color)
        sbRed.progress = Color.red(color)
        sbGreen.progress = Color.green(color)
        sbBlue.progress = Color.blue(color)
    }
}