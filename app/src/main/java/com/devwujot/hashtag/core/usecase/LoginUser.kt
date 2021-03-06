package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.AuthCridential
import com.devwujot.hashtag.core.data.Resource
import com.devwujot.hashtag.core.repository.FirebaseRepository

class LoginUser(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(authCridential: AuthCridential, loginResponse: MutableLiveData<Resource<*>>) =
        firebaseRepository.login(authCridential, loginResponse)
}