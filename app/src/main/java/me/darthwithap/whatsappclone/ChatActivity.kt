package me.darthwithap.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_chat.*
import me.darthwithap.whatsappclone.utils.IMG
import me.darthwithap.whatsappclone.utils.NAME
import me.darthwithap.whatsappclone.utils.UID

class ChatActivity : AppCompatActivity() {
    private var name: String = ""
    private var uid: String = ""
    private var img: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        name = intent.getStringExtra(NAME).toString()
        uid = intent.getStringExtra(UID).toString()
        img = intent.getStringExtra(IMG).toString()

        tvCentre.text = "$uid\n$name\n$img"
    }
}