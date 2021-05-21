package me.darthwithap.whatsappclone.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_date_header.view.*
import me.darthwithap.whatsappclone.models.DateHeader

class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(event: DateHeader) {
        itemView.tvNewDayTitle.text = event.date
    }
}