package com.devwujot.hashtag.presentation.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.devwujot.hashtag.R
import com.devwujot.hashtag.databinding.ActivitySignupBinding
import com.devwujot.hashtag.framework.utility.FormValidator
import com.devwujot.hashtag.framework.utility.reObserve
import com.devwujot.hashtag.framework.viewModel.SignupViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SignupActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SignupActivity::class.java)
    }

    private val viewModel: SignupViewModel by viewModel()
    private val formValidator: FormValidator by inject()
    private lateinit var binding: ActivitySignupBinding

    private val goToLoginObserver = Observer<Boolean> { goToLogin ->
        if (goToLogin) {
            startActivity(
                LoginActivity.newIntent(
                    this
                )
            )
            finish()
        }
    }

    private val uidObserver = Observer<String> { uid ->
        uid?.let {
            startActivity(
                (HomeActivity.newIntent(
                    this
                ))
            )
            finish()
        }
    }

    private val validateObserver = Observer<Boolean> { validationClicked ->
        if (validationClicked) {
            handleValidation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.viewModel = viewModel
        binding.formValidator = formValidator

        binding.signupProgressLayout.setOnTouchListener { v, event -> true }

        viewModel.apply {
            goToLogin.reObserve(this@SignupActivity, goToLoginObserver)
            uid.reObserve(this@SignupActivity, uidObserver)
            validate.reObserve(this@SignupActivity, validateObserver)
        }
    }

    private fun handleValidation() {
        binding.usernameTIL.error =
            formValidator.validateUsername(binding.usernameET.text.toString())
        binding.emailTIL.error = formValidator.validateEmail(binding.emailET.text.toString())
        binding.passwordTIL.error =
            formValidator.validatePassword(binding.passwordET.text.toString())
        if (binding.usernameTIL.error == null || binding.emailTIL.error == null || binding.passwordTIL.error == null) {
            viewModel.signup()
        }
    }
}