package me.darthwithap.whatsappclone.adapters

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.darthwithap.whatsappclone.fragments.CallsFragment
import me.darthwithap.whatsappclone.fragments.ChatsFragment
import me.darthwithap.whatsappclone.fragments.StatusFragment

class TabWiseSlideAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = NUM_PAGES

    override fun createFragment(position: Int) =
        when (position) {
            0 -> StatusFragment()
            1 -> ChatsFragment()
            else -> CallsFragment()
        }

    companion object {
        private const val NUM_PAGES = 3
    }

}