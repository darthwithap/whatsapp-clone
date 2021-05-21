package me.darthwithap.whatsappclone.models

import me.darthwithap.whatsappclone.utils.MSG_STATUS_FROM_OTHER
import me.darthwithap.whatsappclone.utils.MSG_STATUS_WAITING
import java.util.*

data class Inbox(

    var name: String,
    val msg: String,
    var from: String,
    var img: String,
    var newMsgCount: Int = 0,
    val time: Date = Date(),
    val isOnline: Boolean = false,
    val hasStatus: Boolean = false,
    var status: Int = MSG_STATUS_WAITING
) {
    constructor() : this("", "", "", "", 0, Date(), false, false, MSG_STATUS_WAITING)
}