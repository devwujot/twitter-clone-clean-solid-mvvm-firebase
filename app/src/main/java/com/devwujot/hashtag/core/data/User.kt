package com.devwujot.hashtag.core.data

data class User(
    var username: String = "",
    var email: String = "",
    var imageUrl: String = "",
    val followHashtags: ArrayList<String>? = arrayListOf(),
    val followUsers: ArrayList<String>? = arrayListOf()
)