package com.devwujot.hashtag.framework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.UseCases
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

class HomeActivityViewModel(val useCases: UseCases) : ViewModel() {

    private val _logout = SingleLiveEvent<Boolean>()
    val logout: LiveData<Boolean>
        get() = _logout
    private val _uid = SingleLiveEvent<String>()
    val uid: LiveData<String>
        get() = _uid
    private val _isLogoClicked = SingleLiveEvent<Boolean>()
    val isLogoClicked: LiveData<Boolean>
        get() = _isLogoClicked
    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private val _isAddTweetClicked = MutableLiveData<Boolean>()
    val isAddTweetClicked: LiveData<Boolean>
        get() = _isAddTweetClicked

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

    fun refresh() {
        getCurrentUser()
    }

    fun onLogoClicked() {
        _isLogoClicked.postValue(true)
    }

    fun onAddTweetButtonClicked() {
        _isAddTweetClicked.postValue(true)
    }
}