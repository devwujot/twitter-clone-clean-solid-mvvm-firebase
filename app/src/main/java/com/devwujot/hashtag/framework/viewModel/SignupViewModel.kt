package com.devwujot.hashtag.framework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.UseCases
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

class SignupViewModel(val useCases: UseCases): ViewModel() {

    val username = MutableLiveData<String>()
    private val _usernameError = MutableLiveData<String>()
    val usernameError: LiveData<String>
        get() = _usernameError
    val email = MutableLiveData<String>()
    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError
    val password = MutableLiveData<String>()
    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String>
        get() = _passwordError
    private val _goToLogin = SingleLiveEvent<Boolean>()
    val goToLogin: LiveData<Boolean>
        get() = _goToLogin
    private val _uid = SingleLiveEvent<String>()
    val uid: LiveData<String>
        get() = _uid

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        val id = useCases.getCurrentUser()
        _uid.postValue(id)
    }

    fun onSignupClicked() {
        var proceed = true
        if (username.value.isNullOrEmpty()) {
            _usernameError.postValue("Username is required")
            proceed = false
        }
        if (email.value.isNullOrEmpty()) {
            _emailError.postValue("Email is required")
            proceed = false
        }
        if (password.value.isNullOrEmpty()) {
            _passwordError.postValue("Password is required")
            proceed = false
        }
        if (proceed) {
            signup()
        }
    }

    private fun signup() {
        val user = User(username.value!!, email.value!!)
        useCases.signup(user, password.value!!, _uid)
    }

    fun onLoginClicked() {
        _goToLogin.postValue(true)
    }
}