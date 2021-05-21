package me.darthwithap.whatsappclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.models.ChatEvent
import me.darthwithap.whatsappclone.models.DateHeader
import me.darthwithap.whatsappclone.models.Message
import me.darthwithap.whatsappclone.utils.*
import me.darthwithap.whatsappclone.viewholders.DateViewHolder
import me.darthwithap.whatsappclone.viewholders.EmptyViewHolder
import me.darthwithap.whatsappclone.viewholders.MessageViewHolder

class ChatAdapter(
    private val context: Context,
    private val mUid: String,
    private val events: MutableList<ChatEvent>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = { layout: Int ->
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }

        return when (viewType) {
            MSG_SENT -> {
                MessageViewHolder(inflate(R.layout.list_item_sent_msg))
            }
            MSG_RECEIVED -> {
                MessageViewHolder(inflate(R.layout.list_item_received_msg))
            }
            DATE_HEADER -> {
                DateViewHolder(inflate(R.layout.list_item_date_header))
            }
            MSG_UNSUPPORTED -> {
                EmptyViewHolder(inflate(R.layout.empty_view))
            }
            else -> {
                EmptyViewHolder(inflate(R.layout.empty_view))
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (val event = events[position]) {
            is DateHeader -> {
                val holder = viewHolder as DateViewHolder
                holder.bind(event)
            }
            is Message -> {
                val holder = viewHolder as MessageViewHolder
                holder.bind(context, event) { context ->
                    Toast.makeText(context, "On long click clicked", Toast.LENGTH_SHORT).show()
                    return@bind true
                }
            }
        }
    }

    override fun getItemCount() = events.size

    override fun getItemViewType(position: Int): Int {
        return when (val event = events[position]) {
            is DateHeader -> {
                DATE_HEADER
            }
            is Message -> {
                if (event.senderId == mUid) {
                    MSG_SENT
                } else MSG_RECEIVED
            }
            else -> MSG_UNSUPPORTED
        }
    }
}