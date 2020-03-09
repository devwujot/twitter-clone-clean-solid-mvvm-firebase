package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository

class FollowUser(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(followedUsers: ArrayList<String>, isLoading: MutableLiveData<Boolean>) =
        firebaseRepository.followUser(followedUsers, isLoading)
}