package com.devwujot.hashtag.framework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.UseCases

class SearchViewModel(private val useCases: UseCases) : ViewModel() {

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String>
        get() = _userId
    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    private val _tweets = MutableLiveData<List<Tweet>>()
    val tweets: LiveData<List<Tweet>>
        get() = _tweets
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val isHashTagFollowed = MutableLiveData<Boolean>()
    private lateinit var hashTag: String

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

    fun searchTweets(currentHashtag: String) {
        hashTag = currentHashtag
        useCases.searchTweets(_tweets, currentHashtag, _isLoading)
        handleFollowed(currentHashtag)
    }

    fun onFollowHashTagsClicked(currentHashtag: String) {
        useCases.updateFollowHashTags(currentHashtag, _user, _isLoading)
        getCurrentUser()
        handleFollowed(currentHashtag)
    }

    private fun handleFollowed(currentHashtag: String) {
        val followed = _user.value?.followHashtags
        followed?.let {
            if (it.contains(currentHashtag)) {
                isHashTagFollowed.postValue(true)
            } else {
                isHashTagFollowed.postValue(false)
            }
        }
    }

    fun updateTweeLikes(tweet: Tweet) {
        useCases.updateTweetLikes(tweet, _isLoading)
        searchTweets(hashTag)
    }

    fun updateReTweet(tweet: Tweet) {
        useCases.updateReTweet(tweet, _isLoading)
        searchTweets(hashTag)
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