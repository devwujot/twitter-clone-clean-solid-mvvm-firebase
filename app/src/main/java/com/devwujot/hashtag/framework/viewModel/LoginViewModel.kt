package com.devwujot.hashtag.framework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.AuthCridential
import com.devwujot.hashtag.core.data.Resource
import com.devwujot.hashtag.framework.UseCases
import com.devwujot.hashtag.framework.utility.SingleLiveEvent

class LoginViewModel(val useCases: UseCases) : ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private val _uid = SingleLiveEvent<String>()
    val uid: LiveData<String>
        get() = _uid
    private val _goToSignup = SingleLiveEvent<Boolean>()
    val goToSignup: LiveData<Boolean>
        get() = _goToSignup
    private val _validate = MutableLiveData<Boolean>()
    val validate: LiveData<Boolean>
        get() = _validate
    private val _loginResponse = MutableLiveData<Resource<*>>()
    val loginResponse: LiveData<Resource<*>>
        get() = _loginResponse

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        val id = useCases.getCurrentUser()
        _uid.postValue(id)
    }

    fun login() {
        val authCridential = AuthCridential(email.value!!, password.value!!)
        useCases.login(authCridential, _loginResponse)
    }

    fun onLoginClicked() {
        _validate.postValue(true)
    }

    fun onSignupClicked() {
        _goToSignup.postValue(true)
    }
}