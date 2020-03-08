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
        var text = source
        while (text.contains("#")) {
            var hashTag = ""
            val hash = text.indexOf("#")
            text = text.substring(hash + 1)
            val firstSpace = text.indexOf(" ")
            val firstHash = text.indexOf("#")
            if (firstSpace == -1 && firstHash == -1) {
                hashTag = text.substring(0)
            } else if (firstSpace != -1 && firstSpace < firstHash) {
                hashTag = text.substring(0, firstSpace)
                text = text.substring(firstSpace + 1)
            } else {
                hashTag = text.substring(0, firstHash)
                text.substring(firstHash)
            }
            if (!hashTag.isNullOrEmpty()) {
                hashTags.add(hashTag)
            }
        }
        return hashTags
    }

    fun storeImage(imageUri: Uri) {
        useCases.storeTweetImage(imageUri, _image, _isLoading)
    }
}