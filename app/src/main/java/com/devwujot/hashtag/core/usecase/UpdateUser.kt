package com.devwujot.hashtag.core.usecase

import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.Resource
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository

class UpdateUser(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(user: MutableLiveData<User>, updateResponse: MutableLiveData<Resource<*>>) =
        firebaseRepository.updateUser(user, updateResponse)
}