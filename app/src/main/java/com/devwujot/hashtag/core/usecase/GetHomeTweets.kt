package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository

class GetHomeTweets(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(user: MutableLiveData<User>, tweets: MutableLiveData<ArrayList<Tweet>>, isLoading: MutableLiveData<Boolean>) = firebaseRepository.getHomeTweets(user,tweets, isLoading)
}