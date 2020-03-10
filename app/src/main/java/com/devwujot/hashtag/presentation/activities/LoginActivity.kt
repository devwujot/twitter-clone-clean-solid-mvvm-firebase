package com.devwujot.hashtag.presentation.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.devwujot.hashtag.R
import com.devwujot.hashtag.databinding.ActivityLoginBinding
import com.devwujot.hashtag.framework.utility.FormValidator
import com.devwujot.hashtag.framework.viewModel.LoginViewModel
import com.devwujot.hashtag.framework.utility.reObserve
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    private val viewModel: LoginViewModel by viewModel()
    private val formValidator: FormValidator by inject()
    private lateinit var binding: ActivityLoginBinding

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

    private val goToSignupObserver = Observer<Boolean> { goToSignup ->
        if (goToSignup) {
            startActivity(
                SignupActivity.newIntent(
                    this
                )
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
        setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel
        binding.formValidator = formValidator

        loginProgressLayout.setOnTouchListener { v, event -> true }

        viewModel.apply {
            uid.reObserve(this@LoginActivity, uidObserver)
            goToSignup.reObserve(this@LoginActivity, goToSignupObserver)
            validate.reObserve(this@LoginActivity, validateObserver)
        }
    }

    private fun handleValidation() {
        binding.emailTIL.error = formValidator.validateEmail(binding.emailET.text.toString())
        binding.passwordTIL.error = formValidator.validatePassword(binding.passwordET.text.toString())
        if (binding.emailTIL.error == null || binding.passwordTIL.error == null) {
            viewModel.login()
        }
    }
}