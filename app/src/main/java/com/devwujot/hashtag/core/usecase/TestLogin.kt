package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.AuthCridential
import com.devwujot.hashtag.core.data.Resource
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository

class TestLogin(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(authCridential: AuthCridential, loginResponse: MutableLiveData<Resource<*>>) =
        firebaseRepository.testLogin(authCridential, loginResponse)
}