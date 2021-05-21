package me.darthwithap.whatsappclone.models

import android.content.Context
import me.darthwithap.whatsappclone.utils.MSG_STATUS_FROM_OTHER
import me.darthwithap.whatsappclone.utils.MSG_STATUS_WAITING
import me.darthwithap.whatsappclone.utils.MSG_TYPE_TEXT
import me.darthwithap.whatsappclone.utils.formatAsHeader
import java.util.*

data class Message(
    val msg: String,
    val senderId: String,
    val msgId: String,
    val type: String = MSG_TYPE_TEXT,
    val status: Int = MSG_STATUS_WAITING,
    val liked: Boolean = false,
    override val msgTime: Date = Date()
) : ChatEvent {
    constructor() : this("", "", "", MSG_TYPE_TEXT, MSG_STATUS_WAITING, false, Date())
}

data class DateHeader(val context: Context, override val msgTime: Date = Date()) : ChatEvent {
    val date: String = msgTime.formatAsHeader(context)
}

interface ChatEvent {
    val msgTime: Date
}