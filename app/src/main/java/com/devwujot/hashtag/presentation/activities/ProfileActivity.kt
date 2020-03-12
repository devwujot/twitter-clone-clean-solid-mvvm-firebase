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
import com.devwujot.hashtag.core.data.Resource
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.databinding.ActivityProfileBinding
import com.devwujot.hashtag.framework.utility.REQUEST_CODE_PHOTO
import com.devwujot.hashtag.framework.utility.reObserve
import com.devwujot.hashtag.framework.viewModel.ProfileViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
    }

    private val viewModel: ProfileViewModel by viewModel()
    private lateinit var binding: ActivityProfileBinding

    private val isSignoutClickedObserver = Observer<Boolean> { isSignoutClicked ->
        if (isSignoutClicked) {
            startActivity(LoginActivity.newIntent(this))
            finish()
        }
    }

    private val uidObserver = Observer<String> { uid ->
        if (uid.isNullOrEmpty()) {
            finish()
        }
    }

    private val userObserver = Observer<User> { user ->
        user?.let {
            binding.user = user
        }
    }

    private val isApplyClickedObserver = Observer<Boolean> { isApplyClicked ->
        if (isApplyClicked) {
            viewModel.updateUser(
                binding.usernameET.text.toString(),
                binding.emailET.text.toString()
            )
            finish()
        }
    }

    private val isImageClickedObserver = Observer<Boolean> { isImageClicked ->
        if (isImageClicked) {
            startPhotoIntent()
        }
    }

    private val isLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            binding.profileProgressLayout.visibility = View.VISIBLE
        } else {
            binding.profileProgressLayout.visibility = View.GONE
        }
    }

    private val updateResponseObserver = Observer<Resource<*>> { updateResponse ->
        when (updateResponse.status) {
            Resource.Status.SUCCESS -> {
                binding.profileProgressLayout.visibility = View.GONE
                Toast.makeText(this, updateResponse.data.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
            Resource.Status.ERROR -> {
                binding.profileProgressLayout.visibility = View.GONE
                Toast.makeText(this, updateResponse.errorMessage.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
            Resource.Status.LOADING -> {
                if (updateResponse.data == true) {
                    binding.profileProgressLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.viewModel = viewModel

        binding.profileProgressLayout.setOnTouchListener { v, event -> true }

        viewModel.apply {
            isApplyClicked.reObserve(this@ProfileActivity, isApplyClickedObserver)
            isSignoutClicked.reObserve(this@ProfileActivity, isSignoutClickedObserver)
            uid.reObserve(this@ProfileActivity, uidObserver)
            user.reObserve(this@ProfileActivity, userObserver)
            isImageClicked.reObserve(this@ProfileActivity, isImageClickedObserver)
            isLoading.reObserve(this@ProfileActivity, isLoadingObserver)
            updateResponse.reObserve(this@ProfileActivity, updateResponseObserver)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            storeImage(data?.data)
        }
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