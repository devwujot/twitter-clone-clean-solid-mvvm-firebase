package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository

class GetUser(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(user: MutableLiveData<User>, isLoading: MutableLiveData<Boolean>) = firebaseRepository.getUser(user, isLoading)
}