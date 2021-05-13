package me.darthwithap.whatsappclone.models

data class Inbox(
    val name: String,
    val from: String,
    val msg: String,
    val time: String,
    val count: Int = 0
) {
    constructor(): this("", "", "", "", 0)
}