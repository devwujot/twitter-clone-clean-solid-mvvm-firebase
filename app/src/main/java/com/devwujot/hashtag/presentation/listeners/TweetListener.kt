package com.devwujot.hashtag.presentation.listeners

import android.view.View

interface TweetListener {
    fun onLayoutClick(v: View)
    fun onLike(v: View)
    fun onReTweet(v: View)
}