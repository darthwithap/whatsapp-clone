package me.darthwithap.whatsappclone

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun adjustFontScale(configuration: Configuration) {
        Log.d(Companion.TAG, "adjustFontScale: ${configuration.fontScale}")
        if (configuration.fontScale > 1.30f) configuration.fontScale = 1.30f
        else configuration.fontScale = 1.00f

        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}