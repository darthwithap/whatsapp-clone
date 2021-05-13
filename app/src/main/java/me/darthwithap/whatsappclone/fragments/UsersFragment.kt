package me.darthwithap.whatsappclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import me.darthwithap.whatsappclone.R

class UsersFragment() : Fragment() {

    //lateinit var adapter = FirestorePagingAdapter<User, UserViewHolder>
    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firestoreDatabase by lazy {
        FirebaseFirestore.getInstance().collection("cookie-chat-users")
            .orderBy("name", Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users, container,false)
    }
}
