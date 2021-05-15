package me.darthwithap.whatsappclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_users.*
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.adapters.UserAdapter
import me.darthwithap.whatsappclone.models.User


class UsersFragment : Fragment() {

    private lateinit var viewManager: RecyclerView.LayoutManager
    private var users: ArrayList<User> = ArrayList()
    private lateinit var userAdapter: UserAdapter
    private var lastVisible: DocumentSnapshot? = null
    private var isScrolling = false
    private var isLastItemReached = false

    private val usersRef by lazy {
        FirebaseFirestore.getInstance().collection("cookie-chat-users")
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val query by lazy {
        usersRef.orderBy("name", Query.Direction.ASCENDING)
            .limit(LIMIT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(users)
        return inflater.inflate(R.layout.fragment_users, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvUsers.apply {
            layoutManager = viewManager
            adapter = userAdapter

            getUsers()
        }

    }

    private fun getUsers() {
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.forEach {
                    val user = it.toObject(User::class.java)
                    users.add(user)
                }
                userAdapter.notifyDataSetChanged()
                lastVisible = task.result?.size()?.minus(1)?.let { task.result?.documents?.get(it) }
                val onScrollListener: RecyclerView.OnScrollListener =
                    object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(
                            rv: RecyclerView,
                            newState: Int
                        ) {
                            super.onScrollStateChanged(rv, newState)
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true
                            }
                        }

                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            val llm =
                                recyclerView.layoutManager as LinearLayoutManager?
                            val firstVisibleItemPosition =
                                llm!!.findFirstVisibleItemPosition()
                            val visibleItemCount = llm.childCount
                            val totalItemCount = llm.itemCount
                            if (isScrolling && firstVisibleItemPosition + visibleItemCount == totalItemCount && !isLastItemReached) {
                                isScrolling = false
                                val nextQuery: Query =
                                    usersRef.orderBy(
                                        "name",
                                        Query.Direction.ASCENDING
                                    ).startAfter(lastVisible).limit(LIMIT)
                                nextQuery.get().addOnCompleteListener { t ->
                                    if (t.isSuccessful) {
                                        t.result?.forEach {
                                            val user = it.toObject(User::class.java)
                                            users.add(user)
                                        }
                                        userAdapter.notifyDataSetChanged()
                                        lastVisible =
                                            t.result?.size()?.minus(1)?.let {
                                                t.result?.documents?.get(
                                                    it
                                                )
                                            }
                                        if (t.result?.size()!! < LIMIT) {
                                            isLastItemReached = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                rvUsers.addOnScrollListener(onScrollListener)
            }
        }
    }

    companion object {
        private const val LIMIT = 15L
    }

}