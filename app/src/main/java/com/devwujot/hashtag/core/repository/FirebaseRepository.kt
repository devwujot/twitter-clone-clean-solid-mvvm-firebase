package com.devwujot.hashtag.core.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.AuthCridential
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

class FirebaseRepository(private val dataSource: FirebaseDataSource) {

    fun login(authCridential: AuthCridential, uid: MutableLiveData<String>) =
        dataSource.login(authCridential, uid)

    fun getCurrentUser() = dataSource.getCurrentUser()

    fun logout() = dataSource.logout()

    fun signup(user: User, password: String, uid: MutableLiveData<String>) =
        dataSource.signup(user, password, uid)

    fun getUser(user: MutableLiveData<User>, isLoading: MutableLiveData<Boolean>) =
        dataSource.getUser(user, isLoading)

    fun updateUser(user: MutableLiveData<User>, isLoading: MutableLiveData<Boolean>) =
        dataSource.updateUser(user, isLoading)

    fun storeImage(
        imageUrl: Uri,
        user: MutableLiveData<User>,
        isLoading: SingleLiveEvent<Boolean>
    ) = dataSource.storeImage(imageUrl, user, isLoading)

    fun storeTweetImage(
        imageUrl: Uri,
        image: MutableLiveData<String>,
        isLoading: MutableLiveData<Boolean>
    ) = dataSource.storeTweetImage(imageUrl, image, isLoading)

    fun postTweet(tweet: Tweet, isLoading: MutableLiveData<Boolean>) =
        dataSource.postTweet(tweet, isLoading)

    fun searchTweets(
        tweets: MutableLiveData<List<Tweet>>,
        currenHashtag: String,
        isLoading: MutableLiveData<Boolean>
    ) = dataSource.searchTweets(tweets, currenHashtag, isLoading)

    fun updateFollowHashTags(
        currentHashtag: String,
        user: MutableLiveData<User>,
        isLoading: MutableLiveData<Boolean>
    ) = dataSource.updateFollowHashTags(currentHashtag, user, isLoading)

    fun updateTweetLikes(tweet: Tweet, isLoading: MutableLiveData<Boolean>) =
        dataSource.updateTweetLikes(tweet, isLoading)

    fun updateReTweet(tweet: Tweet, isLoading: MutableLiveData<Boolean>) =
        dataSource.updateReTweet(tweet, isLoading)

    fun followUser(followedUsers: ArrayList<String>, isLoading: MutableLiveData<Boolean>) =
        dataSource.followUser(followedUsers, isLoading)

    fun unfollowUser(followedUsers: ArrayList<String>, isLoading: MutableLiveData<Boolean>) =
        dataSource.unfollowUser(followedUsers, isLoading)

    fun getHomeTweets(
        user: MutableLiveData<User>,
        tweets: MutableLiveData<ArrayList<Tweet>>,
        isLoading: MutableLiveData<Boolean>) = dataSource.getHomeTweets(user,tweets, isLoading)
}