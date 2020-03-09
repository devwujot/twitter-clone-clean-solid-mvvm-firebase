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
import com.devwujot.hashtag.databinding.ActivityLoginBinding
import com.devwujot.hashtag.framework.viewModel.LoginViewModel
import com.devwujot.hashtag.framework.utility.reObserve
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    private val viewModel: LoginViewModel by viewModel()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel

        setTextChangeListener(binding.emailET, binding.emailTIL)
        setTextChangeListener(binding.passwordET, binding.passwordTIL)

        loginProgressLayout.setOnTouchListener { v, event -> true }

        viewModel.apply {
            uid.reObserve(this@LoginActivity, uidObserver)
            emailError.reObserve(this@LoginActivity, emailErrorObserver)
            passwordError.reObserve(this@LoginActivity, passwordErrorObserver)
            goToSignup.reObserve(this@LoginActivity, goToSignupObserver)
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