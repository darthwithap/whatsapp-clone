package me.darthwithap.whatsappclone.viewholders

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_general.view.*
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.models.User

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(user:User, onClick: (uid: String, name: String, img: String) -> Unit) {
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

            setOnClickListener {
                onClick.invoke(user.uid, user.name, user.thumbImg)
            }
        }
    }
}