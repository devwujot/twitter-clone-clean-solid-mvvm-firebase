package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.repository.FirebaseRepository

class PostTweet(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(tweet: Tweet, isLoading: MutableLiveData<Boolean>) =
        firebaseRepository.postTweet(tweet, isLoading)
}