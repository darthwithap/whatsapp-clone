package me.darthwithap.whatsappclone

import android.content.res.Configuration
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    fun adjustFontScale(configuration: Configuration) {
        if (configuration.fontScale > 1.30f) configuration.fontScale = 1.30f
        else configuration.fontScale = 1.00f

        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }
}