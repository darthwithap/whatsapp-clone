package me.darthwithap.whatsappclone.ui

import android.graphics.Color
import android.graphics.Typeface.*
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import me.darthwithap.whatsappclone.BaseActivity
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.adapters.TabWiseSlideAdapter

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tbTop)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //viewPager.adapter = TabWiseSlideAdapter()

        initTabLayoutViews(0)
    }

    private fun initTabLayoutViews(initTab: Int) {
        for (i in 0..tlTop.tabCount) {
            val tab = tlTop.getTabAt(i)
            if (tab != null) {
                val tv = TextView(this)
                tab.customView = tv
                tv.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tv.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                tv.text = tab.text
                updateUnselectedTabTextAppearance(tv, 0)
                if (i == initTab) updateSelectedTabTextAppearance(tv)
            }
        }
        tlTop.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                val tv = tab.customView as TextView
                updateSelectedTabTextAppearance(tv)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tv = tab?.customView as TextView
                updateUnselectedTabTextAppearance(tv, 2)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun updateUnselectedTabTextAppearance(tv: TextView, trimFromEnd: Int) {
        tv.typeface = create(resources.getFont(R.font.raleway_black), NORMAL)
        tv.textSize = 14f
        tv.setTextColor(resources.getColor(R.color.faint_gray))
        val text = tv.text
        tv.text = text.substring(0, text.length - trimFromEnd)
    }

    private fun updateSelectedTabTextAppearance(tv: TextView) {
        val text = "${tv.text} \u25CF"
        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(
                Color.parseColor(
                    String.format(
                        "#%06x",
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.light_green
                        )
                    )
                )
            ),
            text.length - 2,
            text.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        tv.text = spannable
        tv.typeface = create(resources.getFont(R.font.raleway_black), BOLD)
        tv.setTextColor(resources.getColor(R.color.just_black))
        tv.textSize = 20f
    }
}