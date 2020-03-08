package com.devwujot.hashtag.framework

import com.devwujot.hashtag.core.usecase.*

data class UseCases (
    val login: LoginUser,
    val getCurrentUser: GetCurrentUser,
    val logout: Logout,
    val signup: Signup,
    val getUser: GetUser,
    val updateUser: UpdateUser,
    val storeImage: StoreImage,
    val postTweet: PostTweet,
    val storeTweetImage: StoreTweetImage,
    val searchTweets: SearchTweets,
    val updateFollowHashTags: UpdateFollowHashTags,
    val updateTweetLikes: UpdateTweetLikes,
    val updateReTweet: UpdateReTweet,
    val followUser: FollowUser,
    val unfollowUser: UnfollowUser,
    val getHomeTweets: GetHomeTweets
    )