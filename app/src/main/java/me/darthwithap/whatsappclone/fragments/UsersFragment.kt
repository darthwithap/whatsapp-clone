package me.darthwithap.whatsappclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_users.*
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.adapters.UserAdapter
import me.darthwithap.whatsappclone.models.User
import me.darthwithap.whatsappclone.utils.LIMIT


class UsersFragment : Fragment(), UserAdapter.OnSwipeRefreshListener {

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var userAdapter: UserAdapter

    private val usersRef by lazy {
        FirebaseFirestore.getInstance().collection("cookie-chat-users")
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
        setupUserAdapter()
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    private fun setupUserAdapter() {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(LIMIT.toInt())
            .build()

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(query, config, User::class.java)
            .build()

        userAdapter = UserAdapter(context!!, options)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvUsers.apply {
            layoutManager = viewManager
            userAdapter.setOnSwipeRefreshListener(this@UsersFragment)
            adapter = userAdapter
        }
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL -> swipeRefresh.isRefreshing = true
            LoadingState.LOADING_MORE -> swipeRefresh.isRefreshing = true
            LoadingState.LOADED -> swipeRefresh.isRefreshing = false
            LoadingState.FINISHED -> swipeRefresh.isRefreshing = false
            LoadingState.ERROR -> swipeRefresh.isRefreshing = false
        }
    }
}