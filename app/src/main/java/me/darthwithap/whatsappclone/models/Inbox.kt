package me.darthwithap.whatsappclone.models

data class Inbox(

    val name: String,
    val msg: String,
    val lastMsg: String,
    val newMsgCount: Int,
    val isOnline: Boolean,
    val hasStatus: Boolean,
    val msgStatus: Int,
    val from: String
) {
    constructor() : this("", "", "", 0, false, false, 3, "")
}