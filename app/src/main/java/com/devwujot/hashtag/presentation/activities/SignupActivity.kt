package com.devwujot.hashtag.presentation.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.devwujot.hashtag.R
import com.devwujot.hashtag.databinding.ActivitySignupBinding
import com.devwujot.hashtag.framework.utility.reObserve
import com.devwujot.hashtag.framework.viewModel.SignupViewModel
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.viewmodel.ext.android.viewModel

class SignupActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SignupActivity::class.java)
    }

    private val viewModel: SignupViewModel by viewModel()
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

    private val emailErrorObserver = Observer<String> { error ->
        error?.let {
            binding.emailTIL.error = error
            binding.emailTIL.isErrorEnabled = true
        }
    }

    private val passwordErrorObserver = Observer<String> { error ->
        error?.let {
            binding.passwordTIL.error = error
            binding.passwordTIL.isErrorEnabled = true
        }
    }

    private val usernameErrorObserver = Observer<String> { error ->
        error?.let {
            binding.usernameTIL.error = error
            binding.usernameTIL.isErrorEnabled = true
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.viewModel = viewModel

        setTextChangeListener(binding.usernameET, binding.usernameTIL)
        setTextChangeListener(binding.emailET, binding.emailTIL)
        setTextChangeListener(binding.passwordET, binding.passwordTIL)

        binding.signupProgressLayout.setOnTouchListener { v, event -> true }

        viewModel.apply {
            goToLogin.reObserve(this@SignupActivity, goToLoginObserver)
            usernameError.reObserve(this@SignupActivity, usernameErrorObserver)
            emailError.reObserve(this@SignupActivity, emailErrorObserver)
            passwordError.reObserve(this@SignupActivity, passwordErrorObserver)
            uid.reObserve(this@SignupActivity, uidObserver)
        }
    }

    fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }
        })
    }
}
