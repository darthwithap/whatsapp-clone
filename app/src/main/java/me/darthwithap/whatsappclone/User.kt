package me.darthwithap.whatsappclone

data class User(
    val uid: String,
    val name: String,
    val phone: String,
    val profileImgUrl: String,
    val thumbImg: String,
    val deviceToken: String,
    val status: String,
    val online: Boolean
) {
    constructor() :
            this("", "", "", "", "", "", "I am now on Cookie Chat!", false)

    constructor(uid: String, name: String, phone: String) :
            this(uid, name, phone, "", "", "", "I am now on Cookie Chat!", false)

    constructor(uid: String, name: String, phone: String, profileImgUrl: String, thumbImg: String) :
            this(uid, name, phone, profileImgUrl, thumbImg, "", "I am now on Cookie Chat!", false)
}