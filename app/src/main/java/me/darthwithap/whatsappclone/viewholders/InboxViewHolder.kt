package me.darthwithap.whatsappclone.viewholders

import android.graphics.Typeface
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_general.view.*
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.models.Inbox
import me.darthwithap.whatsappclone.utils.*

class InboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(inbox: Inbox, onClick: (uid: String, name: String, img: String) -> Unit) {
        with(itemView) {
            tvFromName.text = inbox.name
            tvTimeOrDate.text = inbox.time.formatAsListItem(context)
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
                when(inbox.status) {
                    MSG_STATUS_WAITING -> setImageResource(R.drawable.ic_waiting_tick)
                    MSG_STATUS_DELIVERED -> setImageResource(R.drawable.ic_delivered_tick)
                    MSG_STATUS_RECEIVED -> setImageResource(R.drawable.ic_received_tick)
                    MSG_STATUS_READ -> setImageResource(R.drawable.ic_read_tick)
                    MSG_STATUS_ERROR -> setImageResource(R.drawable.ic_error_tick)
                    MSG_STATUS_FROM_OTHER -> visibility = View.GONE
                }
            }

            setOnClickListener {
                onClick.invoke(inbox.from, inbox.name, inbox.img)
            }
        }
    }
}