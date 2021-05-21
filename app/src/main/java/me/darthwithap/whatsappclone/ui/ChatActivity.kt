package me.darthwithap.whatsappclone.ui

import android.os.Bundle
import android.renderscript.Sampler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.ios.IosEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.list_item_general.view.*
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.adapters.ChatAdapter
import me.darthwithap.whatsappclone.models.*
import me.darthwithap.whatsappclone.utils.*
import java.util.*


class ChatActivity : AppCompatActivity() {
    private var name: String = ""
    private var uid: String = ""
    private var img: String = ""
    private val mUid: String = FirebaseAuth.getInstance().uid!!
    private val database = FirebaseDatabase.getInstance().getReference("cookie-chat")
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var mUser: User
    private var isMicEnabled = true
    private val events = mutableListOf<ChatEvent>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(IosEmojiProvider())
        setContentView(R.layout.activity_chat)
        setSupportActionBar(tbChat)

        name = intent.getStringExtra(NAME).toString()
        uid = intent.getStringExtra(UID).toString()
        img = intent.getStringExtra(IMG).toString()

        initViews()
        firestore.collection("cookie-chat-users").document(mUid).get()
            .addOnSuccessListener {
                mUser = it.toObject(User::class.java)!!
            }

        chatAdapter = ChatAdapter(this, mUid, events)
        rvChatMsg.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
        listenForMessages()
        markAsDone()
    }

    private fun initViews() {
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (img.isBlank()) ivToolbarImg.setImageResource(R.drawable.avatar_placeholder)
        else {
            Picasso.get().load(img)
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .into(ivToolbarImg)
        }
        tvToolbarName.text = name
        eetChatMsg.addTextChangedListener(MessageTextWatcher())

        ivSendOrMic.setOnClickListener {
            if (isMicEnabled) {
                Toast.makeText(this, "Mic feature coming soon", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Will send message ${eetChatMsg.text.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                with(eetChatMsg.text!!) {
                    sendMessage(this.toString())
                    this.clear()
                }
            }
        }

        val emojiPopUp = EmojiPopup.Builder.fromRootView(rootView).build(eetChatMsg)
        ivEmojiEmoticon.setOnClickListener {
            emojiPopUp.toggle()
        }
    }

    private fun sendMessage(msg: String) {
        val id = getMessages(uid).push().key
        checkNotNull(id) { "Cannot be null" }
        val message = Message(
            msg, mUid, id
        )
        getMessages(uid).child(id).setValue(message).addOnSuccessListener {
            updateLastMessage(message)
        }
            .addOnFailureListener {
                Log.d(TAG, "sendMessage: ${it.localizedMessage}")
            }
    }

    private fun updateLastMessage(message: Message) {
        val inbox = Inbox(name, message.msg, uid, img)


        getChats(mUid, uid).setValue(inbox).addOnSuccessListener {
            //my inbox populated
            getChats(uid, mUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Inbox::class.java)
                    inbox.apply {
                        name = mUser.name
                        from = message.senderId
                        img = mUser.thumbImg
                        status = MSG_STATUS_FROM_OTHER
                        newMsgCount = 1
                    }
                    value?.let {
                        if (it.from == message.senderId) {
                            inbox.newMsgCount = value.newMsgCount + 1
                        }
                    }

                    getChats(uid, mUid).setValue(inbox).addOnSuccessListener {
                        //friend inbox populated
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun listenForMessages() {
        getMessages(uid)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue(Message::class.java)!!
                    addMessage(msg)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // when status of message changes
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun addMessage(msg: Message) {
        val eventBefore = events.lastOrNull()

        if (eventBefore != null && !eventBefore.msgTime.isSameDayAs(msg.msgTime) || eventBefore == null) {
            events.add(
                DateHeader(this, msg.msgTime)
            )
        }
        events.add(msg)
        chatAdapter.notifyItemInserted(events.size - 1)
        rvChatMsg.scrollToPosition(events.size - 1)
    }

    private fun markAsRead() {
        getChats(uid, mUid).child("count").setValue(0)
    }

    private fun getChats(to: String, from: String) =
        database.child("chats/$to/$from")

    private fun getMessages(uid: String) =
        database.child("messages/${getMsgId(uid)}")


    private fun getMsgId(uid: String): String {
        return if (uid > mUid) mUid + uid
        else uid + mUid
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuVoiceCall -> {
                Toast.makeText(this, "Voice call coming soon", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menuVideoCall -> {
                Toast.makeText(this, "Video call coming soon", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_toolbar_menu, menu)
        return true
    }

    internal inner class MessageTextWatcher() : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val text = s?.toString()
            isMicEnabled = if (text?.isBlank()!!) {
                ivSendOrMic.setImageResource(R.drawable.ic_microphone)
                true
            } else {
                ivSendOrMic.setImageResource(R.drawable.ic_send)
                false
            }

        }

    }

    companion object {
        private const val TAG = "ChatActivity"
    }
}