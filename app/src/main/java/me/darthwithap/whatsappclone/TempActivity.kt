package me.darthwithap.whatsappclone

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_temp.*
import kotlinx.android.synthetic.main.list_item_general.view.*


class TempActivity : AppCompatActivity() {
    private val sampleInboxTiles = arrayListOf(
        SampleInbox(
            "Parth Takkar",
            "This is a sample message and a random one pls ignore.",
            "10 mins ago",
            1,
            isOnline = false,
            hasStatus = true,
            msgStatus = 3

        ),
        SampleInbox(
            "Madhav Shroff",
            "Bhai sun?",
            "14 mins ago",
            0,
            isOnline = false,
            hasStatus = false,
            msgStatus = 0
        ),
        SampleInbox(
            "Anushka Gupta",
            "Woww! Fancyy rich kid stuff",
            "Just now",
            9,
            isOnline = true,
            hasStatus = true,
            msgStatus = 3
        ),
        SampleInbox(
            "Yiyasu Paudel",
            "Ohh haha!",
            "2 hours ago",
            1,
            isOnline = false,
            hasStatus = false,
            msgStatus = 3
        ),
        SampleInbox(
            "Yash Raj",
            "Nice!",
            "12/05/21",
            0,
            isOnline = true,
            hasStatus = false,
            msgStatus = 1
        ),
        SampleInbox(
            "Mayuri Salecha",
            "Yeahh",
            "5 hours ago",
            0,
            isOnline = false,
            hasStatus = true,
            msgStatus = 1
        ),
        SampleInbox(
            "Jeevan Koshy",
            "Yeah we should definitely do that!",
            "11/05/21",
            0,
            isOnline = true,
            hasStatus = false,
            msgStatus = 3
        ),
        SampleInbox(
            "Gautam Malhotra",
            "Oh bc yeh kya hahah",
            "04/05/21",
            1,
            isOnline = false,
            hasStatus = false,
            msgStatus = 3
        ),
        SampleInbox(
            "Kotam",
            "Yaa chheee! I am ded. Done for life",
            "2 mins ago",
            3,
            isOnline = true,
            hasStatus = true,
            msgStatus = 3
        ),
        SampleInbox(
            "Chaitanya Agarwal",
            "Cool.",
            "23 hours ago",
            0,
            isOnline = false,
            hasStatus = false,
            msgStatus = 1
        ),
        SampleInbox(
            "Aishwarya",
            "Wazzzuppp!",
            "10/05/21",
            1,
            isOnline = true,
            hasStatus = true,
            msgStatus = 3
        ),
        SampleInbox(
            "Priyanshi Gupta",
            "Ji sahi baat hai bilkul",
            "7 mins ago",
            0,
            isOnline = false,
            hasStatus = true,
            msgStatus = 0
        ),
        SampleInbox(
            "Bhairavi Muralidharan",
            "Damn cool bro!",
            "09/05/21",
            0,
            isOnline = true,
            hasStatus = false,
            msgStatus = 2
        ),
        SampleInbox(
            "Adarsh",
            "Abe yaar easy chance",
            "4 mins ago",
            1,
            isOnline = true,
            hasStatus = false,
            msgStatus = 3
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)

        rvSampleInboxes.layoutManager = LinearLayoutManager(this)
        rvSampleInboxes.adapter = SampleInboxAdapter(sampleInboxTiles)

    }

    data class SampleInbox(
        val name: String,
        val msg: String,
        val lastMsg: String,
        val newMsgCount: Int,
        val isOnline: Boolean,
        val hasStatus: Boolean,
        val msgStatus: Int
    )

    inner class SampleInboxAdapter(private val inboxes: ArrayList<SampleInbox>) :
        RecyclerView.Adapter<SampleInboxAdapter.SampleInboxViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SampleInboxViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_general, parent, false
                )
            )

        override fun onBindViewHolder(holder: SampleInboxViewHolder, position: Int) {
            holder.bind(inboxes[position])
        }

        override fun getItemCount() = inboxes.size

        inner class SampleInboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(inbox: SampleInbox) {
                with(itemView) {
                    tvFromName.text = inbox.name
                    tvTimeOrDate.text = inbox.lastMsg
                    tvOnlineDot.isVisible = inbox.isOnline
                    if (inbox.newMsgCount > 0) {
                        tvMessage.typeface = Typeface.create(
                            resources.getFont(R.font.raleway_bold),
                            Typeface.NORMAL
                        )
                        tvMessage.text = inbox.msg
                        tvNewMsgCount.text = inbox.newMsgCount.toString()
                        tvNewMsgCount.visibility = View.VISIBLE
                    }
                    if (inbox.hasStatus) {
                        ivProfileImg.setStrokeColorResource(R.color.light_green)
                        ivProfileImg.strokeWidth = 4F
                    }
                    tvMessage.text = inbox.msg


                    with(ivBeforeMessage) {
                        visibility = View.VISIBLE
                        when(inbox.msgStatus) {
                            0 -> setImageResource(R.drawable.ic_delivered_tick)
                            1 -> setImageResource(R.drawable.ic_received_tick)
                            2 -> setImageResource(R.drawable.ic_read_tick)
                            3 -> visibility = View.GONE
                        }
                    }
                }
            }
        }

    }
}