package me.darthwithap.whatsappclone.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import me.darthwithap.whatsappclone.ui.ChatActivity
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.models.User
import me.darthwithap.whatsappclone.utils.*
import me.darthwithap.whatsappclone.viewholders.EmptyViewHolder
import me.darthwithap.whatsappclone.viewholders.UserViewHolder

private const val TAG = "UserAdapter"
class UserAdapter(val context: Context, options: FirestorePagingOptions<User>): FirestorePagingAdapter<User, RecyclerView.ViewHolder>(options) {
    private var onSwipeRefreshListener : OnSwipeRefreshListener? = null
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            NORMAL_VIEW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_general, parent, false)
                UserViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.empty_view, parent, false)
                EmptyViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
        if (holder is UserViewHolder) {
            holder.bind(model) { uid: String, name: String, img: String ->
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(UID, uid)
                intent.putExtra(NAME, name)
                intent.putExtra(IMG, img)
                context.startActivity(intent)
            }
        }
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        super.onLoadingStateChanged(state)
        onSwipeRefreshListener?.onLoadingStateChanged(state)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)?.toObject(User::class.java)
        return if (auth.uid == item!!.uid) EMPTY_VIEW
        else NORMAL_VIEW
    }

    interface OnSwipeRefreshListener{
        fun onLoadingStateChanged(state: LoadingState)
    }

    fun setOnSwipeRefreshListener(swipeRefreshListener: OnSwipeRefreshListener) {
        onSwipeRefreshListener = swipeRefreshListener
    }
}
