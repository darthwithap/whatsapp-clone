package me.darthwithap.whatsappclone.viewholders

import android.graphics.Typeface
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_general.view.*
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.models.Inbox

class InboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(inbox: Inbox) {
        with(itemView) {
            tvFromName.text = inbox.name
            tvTimeOrDate.text = inbox.lastMsg
            tvOnlineDot.isVisible = inbox.isOnline
            if (inbox.newMsgCount > 0) {
                tvMessage.typeface = Typeface.create(
                    resources.getFont(R.font.raleway_bold),
                    Typeface.NORMAL
                )
                tvMessage.text = inbox.msg
                tvNewMsgCount.text = inbox.newMsgCount.toString()
                tvNewMsgCount.visibility = View.VISIBLE
            }
            if (inbox.hasStatus) {
                ivProfileImg.setStrokeColorResource(R.color.light_green)
                ivProfileImg.strokeWidth = 4F
            }
            tvMessage.text = inbox.msg


            with(ivBeforeMessage) {
                visibility = View.VISIBLE
                when(inbox.msgStatus) {
                    0 -> setImageResource(R.drawable.ic_delivered_tick)
                    1 -> setImageResource(R.drawable.ic_received_tick)
                    2 -> setImageResource(R.drawable.ic_read_tick)
                    3 -> visibility = View.GONE
                }
            }
        }
    }
}