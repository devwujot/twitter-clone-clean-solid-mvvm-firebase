package com.devwujot.hashtag.presentation.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.devwujot.hashtag.R
import com.devwujot.hashtag.databinding.ActivityTweetBinding
import com.devwujot.hashtag.framework.utility.REQUEST_CODE_PHOTO
import com.devwujot.hashtag.framework.utility.reObserve
import com.devwujot.hashtag.framework.viewModel.TweetViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class TweetActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, TweetActivity::class.java)
    }

    private val viewModel: TweetViewModel by viewModel()
    private lateinit var binding: ActivityTweetBinding

    private val isAddImageClickedObserver = Observer<Boolean> { isAddImageClicked ->
        if (isAddImageClicked) {
            startPhotoIntent()
        }
    }

    private val isLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            binding.tweetProgressLayout.visibility = View.VISIBLE
        } else {
            binding.tweetProgressLayout.visibility = View.GONE
        }
    }

    private val imageObserver = Observer<String> { image ->
        image?.let {
            binding.image = image
        }
    }

    private val isFinishedObserver = Observer<Boolean> { isFinished ->
        if (isFinished) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tweet)
        binding.viewModel = viewModel

        binding.tweetProgressLayout.setOnTouchListener { v, event -> true }

        viewModel.apply {
            isAddImageClicked.reObserve(this@TweetActivity, isAddImageClickedObserver)
            isLoading.reObserve(this@TweetActivity, isLoadingObserver)
            image.reObserve(this@TweetActivity, imageObserver)
            isFinished.reObserve(this@TweetActivity, isFinishedObserver)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            storeImage(data?.data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.executePendingBindings()
    }

    private fun startPhotoIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = resources.getString(R.string.intent_type)
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    private fun storeImage(imageUri: Uri?) {
        imageUri?.let {
            Toast.makeText(this, resources.getString(R.string.toast_uploading), Toast.LENGTH_SHORT).show()
            viewModel.storeImage(it)
        }
    }
}