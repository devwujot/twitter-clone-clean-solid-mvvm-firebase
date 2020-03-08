package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository

class UpdateFollowHashTags(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(currentHashtag: String, user: MutableLiveData<User>, isLoading: MutableLiveData<Boolean>) = firebaseRepository.updateFollowHashTags(currentHashtag, user, isLoading)
}