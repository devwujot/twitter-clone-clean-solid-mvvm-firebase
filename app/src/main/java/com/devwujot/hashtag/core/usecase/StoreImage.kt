package com.devwujot.hashtag.core.usecase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseRepository
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

class StoreImage(private val firebaseRepository: FirebaseRepository) {

    operator fun invoke(imageUrl: Uri, user: MutableLiveData<User>, isLoading: SingleLiveEvent<Boolean>) = firebaseRepository.storeImage(imageUrl, user, isLoading)
}