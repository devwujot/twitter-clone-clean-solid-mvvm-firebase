package com.devwujot.hashtag.core.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.AuthCridential
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

interface FirebaseDataSource {

    fun login(authCridential: AuthCridential, uid: MutableLiveData<String>)

    fun getCurrentUser(): String?

    fun logout()

    fun signup(user: User, password: String, uid: MutableLiveData<String>)

    fun getUser(user: MutableLiveData<User>, isLoading: MutableLiveData<Boolean>)

    fun updateUser(user: MutableLiveData<User>, isLoading: MutableLiveData<Boolean>)

    fun storeImage(imageUrl: Uri, user: MutableLiveData<User>, isLoading: SingleLiveEvent<Boolean>)

    fun storeTweetImage(
        imageUrl: Uri,
        image: MutableLiveData<String>,
        isLoading: MutableLiveData<Boolean>
    )

    fun postTweet(tweet: Tweet, isLoading: MutableLiveData<Boolean>)

    fun searchTweets(
        tweets: MutableLiveData<List<Tweet>>,
        currenHashtag: String,
        isLoading: MutableLiveData<Boolean>
    )

    fun updateFollowHashTags(
        currentHashtag: String,
        user: MutableLiveData<User>,
        isLoading: MutableLiveData<Boolean>
    )

    fun updateTweetLikes(tweet: Tweet, isLoading: MutableLiveData<Boolean>)

    fun updateReTweet(tweet: Tweet, isLoading: MutableLiveData<Boolean>)

    fun followUser(followedUsers: ArrayList<String>, isLoading: MutableLiveData<Boolean>)

    fun unfollowUser(followedUsers: ArrayList<String>, isLoading: MutableLiveData<Boolean>)

    fun getHomeTweets(user: MutableLiveData<User>, tweets: MutableLiveData<ArrayList<Tweet>>, isLoading: MutableLiveData<Boolean>)
}