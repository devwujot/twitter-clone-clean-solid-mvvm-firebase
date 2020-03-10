package com.devwujot.hashtag.framework.utility

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.devwujot.hashtag.R
import com.devwujot.hashtag.core.data.Tweet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.DateFormat
import java.util.*

@BindingAdapter("validateInput")
fun TextInputLayout.setError(input: String) {
    if (!input.isNullOrEmpty()) {
        error = input
        isErrorEnabled = true
    }
}

@BindingAdapter("updateField")
fun TextInputEditText.updateText(input: String?) {
    setText(input, TextView.BufferType.EDITABLE)
}

@BindingAdapter("loadImage")
fun ImageView.loadUrl(url: String?) {
    url?.let { link ->
        context?.let {
            val options = RequestOptions()
                .placeholder(progressDrawable(context))
                .error(R.drawable.default_user)
            Glide.with(context.applicationContext)
                .load(link)
                .apply(options)
                .into(this)
        }
    }
}

fun progressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
}

@BindingAdapter("convertDate")
fun TextView.convertDate(date: Long?) {
    if (date != null) {
        val df = DateFormat.getDateInstance()
        text = df.format(Date(date))
    } else {
        text = "Unknown"
    }
}

@BindingAdapter("userId", "userIds")
fun ImageView.setReTweetImage(userId: String, userIds: Tweet) {
    userIds.let {
        if (!it.userIds.isNullOrEmpty() && it.userIds?.first() == userId) {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.original))
            isClickable = false
        } else if (it.userIds?.contains(userId)!!) {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.retweet))
        } else {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.retweet_inactive))
        }
    }

}

@BindingAdapter("isHashTagFollowed")
fun ImageView.setFollowedHashTagImage(isHashTagFollowed: Boolean) {
    if (isHashTagFollowed) {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.follow))
    } else {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.follow_inactive))
    }
}

@BindingAdapter("userId", "tweet")
fun ImageView.setTweetLikeImage(userId: String, tweet: Tweet) {
    tweet.let {
        val likes = it.likes
        if (it.likes?.contains(userId)!!) {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like))
        } else {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_inactive))
        }
    }
}

@BindingAdapter("textChangedListener")
fun TextInputLayout.validate(formValidator: FormValidator?) {
    editText?.addTextChangedListener( object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (editText?.inputType == 33) {
                error = formValidator?.validateEmail(s.toString())
            } else if (editText?.inputType == 129) {
                error = formValidator?.validatePassword(s.toString())
            } else if (editText?.inputType == 1) {
                error = formValidator?.validateUsername(s.toString())
            }
        }
    })
}