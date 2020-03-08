package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.repository.FirebaseRepository

class SearchTweets(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(
        tweets: MutableLiveData<List<Tweet>>,
        currenHashtag: String,
        isLoading: MutableLiveData<Boolean>
    ) = firebaseRepository.searchTweets(tweets, currenHashtag, isLoading)
}