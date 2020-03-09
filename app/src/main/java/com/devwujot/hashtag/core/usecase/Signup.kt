package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository

class Signup(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(user: User, password: String, uid: MutableLiveData<String>) =
        firebaseRepository.signup(user, password, uid)
}