package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.repository.FirebaseRepository

class GetMyActivityTweets(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(
        tweets: MutableLiveData<ArrayList<Tweet>>,
        isLoading: MutableLiveData<Boolean>
    ) = firebaseRepository.getMyActivityTweets(tweets, isLoading)
}