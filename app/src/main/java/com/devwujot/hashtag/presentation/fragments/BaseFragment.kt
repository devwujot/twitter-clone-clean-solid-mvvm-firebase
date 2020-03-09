package com.devwujot.hashtag.presentation.fragments

import androidx.fragment.app.Fragment
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.presentation.adapters.TweetListAdapter

abstract class BaseFragment : Fragment() {

    protected var userId = ""
    protected lateinit var user: User
    protected lateinit var tweetListAdapter: TweetListAdapter

    abstract fun updateList()
}