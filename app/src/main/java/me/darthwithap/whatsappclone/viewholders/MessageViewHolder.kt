package me.darthwithap.whatsappclone.viewholders

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_sent_msg.view.*
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.models.Message
import me.darthwithap.whatsappclone.utils.*

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(context: Context, event: Message, onLongClick: (context: Context) -> Boolean) {
        with(itemView) {
            when (event.status) {
                MSG_STATUS_WAITING -> ivMsgStatus.setImageResource(R.drawable.ic_waiting_tick)
                MSG_STATUS_DELIVERED -> ivMsgStatus.setImageResource(R.drawable.ic_delivered_tick)
                MSG_STATUS_RECEIVED -> ivMsgStatus.setImageResource(R.drawable.ic_received_tick)
                MSG_STATUS_READ -> ivMsgStatus.setImageResource(R.drawable.ic_read_tick)
                MSG_STATUS_ERROR -> ivMsgStatus.setImageResource(R.drawable.ic_error_tick)
            }
            tvMsg.text = event.msg
            tvMsgTime.text = event.msgTime.formatAsTime()

            setOnLongClickListener {
                onLongClick.invoke(context)
            }
        }
    }
}