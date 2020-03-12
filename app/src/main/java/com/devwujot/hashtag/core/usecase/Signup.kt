package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.Resource
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository

class Signup(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(user: User, password: String, signupResponse: MutableLiveData<Resource<*>>) =
        firebaseRepository.signup(user, password, signupResponse)
}