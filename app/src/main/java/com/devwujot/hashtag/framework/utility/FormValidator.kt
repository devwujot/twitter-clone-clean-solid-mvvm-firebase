package com.devwujot.hashtag.framework.utility

import android.content.Context
import com.devwujot.hashtag.R

class FormValidator(val context: Context) {

    fun validateEmail(input: String): String? {
        var error: String? = null
        if (input.isNullOrEmpty()) {
            error = context.resources.getString(R.string.form_error_email_empty)
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            error = context.resources.getString(R.string.form_error_email_incorrect)
        }
        return error
    }

    fun validatePassword(input: String): String? {
        var error: String? = null
        if (input.isNullOrEmpty()) {
            error = context.resources.getString(R.string.form_error_password_empty)
        } else if (input.length < FORM_PASSWORD_MIN_CHAR) {
            error = context.resources.getString(R.string.form_error_password_min)
        }
        return error
    }

    fun validateUsername(input: String?): String? {
        var error: String? = null
        if (input.isNullOrEmpty()) {
            error = context.resources.getString(R.string.form_error_username_empty)
        } else if (input.length < FORM_USERNAME_MIN_CHAR) {
            error = context.resources.getString(R.string.form_error_username_min)
        }
        return error
    }
}