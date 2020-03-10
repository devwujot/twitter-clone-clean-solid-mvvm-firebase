package com.devwujot.hashtag.framework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.UseCases
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

class SignupViewModel(val useCases: UseCases) : ViewModel() {

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private val _goToLogin = SingleLiveEvent<Boolean>()
    val goToLogin: LiveData<Boolean>
        get() = _goToLogin
    private val _uid = SingleLiveEvent<String>()
    val uid: LiveData<String>
        get() = _uid
    private val _validate = MutableLiveData<Boolean>()
    val validate: LiveData<Boolean>
        get() = _validate

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        val id = useCases.getCurrentUser()
        _uid.postValue(id)
    }

    fun onSignupClicked() {
        _validate.postValue(true)
    }

    fun signup() {
        val user = User(username.value!!, email.value!!)
        useCases.signup(user, password.value!!, _uid)
    }

    fun onLoginClicked() {
        _goToLogin.postValue(true)
    }
}