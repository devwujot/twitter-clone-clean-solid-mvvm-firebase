package com.devwujot.hashtag.core.usecase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.repository.FirebaseRepository

class StoreTweetImage(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(
        imageUrl: Uri,
        image: MutableLiveData<String>,
        isLoading: MutableLiveData<Boolean>
    ) = firebaseRepository.storeTweetImage(imageUrl, image, isLoading)
}