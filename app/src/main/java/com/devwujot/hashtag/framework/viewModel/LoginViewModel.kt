package com.devwujot.hashtag.framework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.AuthCridential
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.UseCases
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

class LoginViewModel(val useCases: UseCases) : ViewModel() {

    val email = MutableLiveData<String>()
    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError
    val password = MutableLiveData<String>()
    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String>
        get() = _passwordError
    private val _uid = SingleLiveEvent<String>()
    val uid: LiveData<String>
        get() = _uid
    private val _goToSignup = SingleLiveEvent<Boolean>()
    val goToSignup: LiveData<Boolean>
        get() = _goToSignup

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        val id = useCases.getCurrentUser()
        _uid.postValue(id)
    }

    private fun login() {
        val authCridential = AuthCridential(email.value!!, password.value!!)
        useCases.login(authCridential, _uid)
    }

    fun onLoginClicked() {
        var proceed = true
        if (email.value.isNullOrEmpty()) {
            _emailError.postValue("Email is required")
            proceed = false
        }
        if (password.value.isNullOrEmpty()) {
            _passwordError.postValue("Password is required")
            proceed = false
        }
        if (proceed) {
            login()
        }
    }

    fun onSignupClicked() {
        _goToSignup.postValue(true)
    }
}