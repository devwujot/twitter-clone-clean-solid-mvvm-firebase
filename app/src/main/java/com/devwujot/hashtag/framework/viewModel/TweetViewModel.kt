package com.devwujot.hashtag.framework.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.framework.UseCases

class TweetViewModel(val useCases: UseCases) : ViewModel() {

    val tweetText = MutableLiveData<String>()
    private val _image = MutableLiveData<String>()
    val image: LiveData<String>
        get() = _image
    private val _isAddImageClicked = MutableLiveData<Boolean>()
    val isAddImageClicked: LiveData<Boolean>
        get() = _isAddImageClicked
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private val _isFinished = MutableLiveData<Boolean>()
    val isFinished: LiveData<Boolean>
        get() = _isFinished
    private var _user = MutableLiveData<User>()

    init {
        useCases.getUser(_user, isLoading = MutableLiveData())
    }

    fun onAddImageClicked() {
        _isAddImageClicked.postValue(true)
    }

    fun onPostTweetClicked() {
        val tweet = Tweet()
        tweetText.value?.let {
            tweet.text = it
            tweet.hashtags = getHashTag(it)
            tweet.timestamp = System.currentTimeMillis()
            tweet.username = _user.value?.username
            tweet.imageUrl = image.value
        }
        useCases.postTweet(tweet, _isLoading)
        _isFinished.postValue(true)
    }

    private fun getHashTag(source: String): ArrayList<String> {
        val hashTags = arrayListOf<String>()
        val text = source
        val words = text.split(" ")
        for (word in words) {
            if (word.get(0) == '#') {
                val tempWord = word.substring(1)
                hashTags.add(tempWord)
            }
        }
        return hashTags
    }

    fun storeImage(imageUri: Uri) {
        useCases.storeTweetImage(imageUri, _image, _isLoading)
    }
}