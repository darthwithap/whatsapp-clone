package me.darthwithap.whatsappclone

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_general.view.*
import me.darthwithap.whatsappclone.models.User

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(user:User) {
        with(itemView) {
            tvTimeOrDate.isVisible = false

            tvFromName.text = user.name
            tvMessage.text = user.status

            if (user.thumbImg.isBlank()) ivProfileImg.setImageResource(R.drawable.avatar_placeholder)
            else {
                Picasso.get().load(user.thumbImg)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .into(ivProfileImg)
            }
        }
    }
}