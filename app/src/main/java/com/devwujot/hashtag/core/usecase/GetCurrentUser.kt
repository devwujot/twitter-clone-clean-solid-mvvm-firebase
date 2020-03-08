package com.devwujot.hashtag.core.usecase

import com.devwujot.hashtag.core.repository.FirebaseRepository

class GetCurrentUser(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke() = firebaseRepository.getCurrentUser()
}