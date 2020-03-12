package com.devwujot.hashtag.framework.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.Resource
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.UseCases
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

class ProfileViewModel(val useCases: UseCases) : ViewModel() {

    private val _isApplyClicked = SingleLiveEvent<Boolean>()
    val isApplyClicked: LiveData<Boolean>
        get() = _isApplyClicked
    private val _isSignoutClicked = SingleLiveEvent<Boolean>()
    val isSignoutClicked: LiveData<Boolean>
        get() = _isSignoutClicked
    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String>
        get() = _uid
    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    private val _isImageClicked = MutableLiveData<Boolean>()
    val isImageClicked: LiveData<Boolean>
        get() = _isImageClicked
    private val _isLoading = SingleLiveEvent<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _getUserResponse = MutableLiveData<Resource<*>>()
    val getUserResponse: LiveData<Resource<*>>
        get() = _getUserResponse

    private val _updateResponse = MutableLiveData<Resource<*>>()
    val updateResponse: LiveData<Resource<*>>
        get() = _updateResponse

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        val id = useCases.getCurrentUser()
        id?.let {
            _uid.postValue(it)
            useCases.getUser(_user, _isLoading)
        }
    }

    fun onApplyButtonClicked() {
        _isApplyClicked.postValue(true)
    }

    fun onSignoutButtonClicked() {
        useCases.logout()
        _isSignoutClicked.postValue(true)
    }

    fun updateUser(username: String, email: String) {
        _user.value?.let {
            it.username = username
            it.email = email
        }
        useCases.updateUser(_user, _updateResponse)
    }

    fun onImageClicked() {
        _isImageClicked.postValue(true)
    }

    fun storeImage(imageUri: Uri) {
        useCases.storeImage(imageUri, _user, _isLoading)
    }
}