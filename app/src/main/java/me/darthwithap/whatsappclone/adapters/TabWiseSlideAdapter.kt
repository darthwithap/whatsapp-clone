package me.darthwithap.whatsappclone.adapters

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.darthwithap.whatsappclone.fragments.ChatsFragment
import me.darthwithap.whatsappclone.fragments.StatusFragment
import me.darthwithap.whatsappclone.fragments.UsersFragment
import me.darthwithap.whatsappclone.utils.NUM_PAGES

class TabWiseSlideAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = NUM_PAGES

    override fun createFragment(position: Int) =
        when (position) {
            0 -> StatusFragment()
            1 -> ChatsFragment()
            else -> UsersFragment()
        }



}