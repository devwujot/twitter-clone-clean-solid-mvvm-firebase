package com.devwujot.hashtag.framework.di

import com.devwujot.hashtag.core.usecase.*
import com.devwujot.hashtag.framework.UseCases
import org.koin.dsl.module

val useCasesModule = module {
    single {
        UseCases(
            LoginUser(get()),
            GetCurrentUser(get()),
            Logout(get()),
            Signup(get()),
            GetUser(get()),
            UpdateUser(get()),
            StoreImage(get()),
            PostTweet(get()),
            StoreTweetImage(get()),
            SearchTweets(get()),
            UpdateFollowHashTags(get()),
            UpdateTweetLikes(get()),
            UpdateReTweet(get()),
            FollowUser(get()),
            UnfollowUser(get()),
            GetHomeTweets(get()),
            GetMyActivityTweets(get()),
            TestLogin(get())
        )
    }
}