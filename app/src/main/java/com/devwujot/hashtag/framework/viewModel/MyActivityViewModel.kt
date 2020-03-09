package com.devwujot.hashtag.framework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.UseCases

class MyActivityViewModel(val useCases: UseCases) : ViewModel() {

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String>
        get() = _userId
    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    private val _tweets = MutableLiveData<ArrayList<Tweet>>()
    val tweets: LiveData<ArrayList<Tweet>>
        get() = _tweets
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        val currentId = useCases.getCurrentUser()
        currentId?.let {
            _userId.postValue(it)
            useCases.getUser(_user, _isLoading)
        }
    }

    fun getMyActivityTweets() {
        useCases.getMyActivityTweets(_tweets, _isLoading)
    }

    fun updateTweeLikes(tweet: Tweet) {
        useCases.updateTweetLikes(tweet, _isLoading)
        getMyActivityTweets()
    }

    fun updateReTweet(tweet: Tweet) {
        useCases.updateReTweet(tweet, _isLoading)
        getMyActivityTweets()
    }

    fun unfollowUser(followedUsers: ArrayList<String>) {
        useCases.unfollowUser(followedUsers, _isLoading)
        getCurrentUser()
    }

    fun followUser(followedUsers: ArrayList<String>) {
        useCases.followUser(followedUsers, _isLoading)
        getCurrentUser()
    }
}