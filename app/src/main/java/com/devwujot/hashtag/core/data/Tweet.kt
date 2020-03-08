package com.devwujot.hashtag.core.data

data class Tweet(
    var tweetId: String? = "",
    var userIds: ArrayList<String>? = arrayListOf(),
    var username: String? = "",
    var text: String? = "",
    var imageUrl: String? = "",
    var timestamp: Long? = 0,
    var hashtags: ArrayList<String>? = arrayListOf(),
    var likes: ArrayList<String>? = arrayListOf()
)