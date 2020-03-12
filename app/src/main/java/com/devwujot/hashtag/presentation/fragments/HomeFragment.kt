package com.devwujot.hashtag.presentation.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devwujot.hashtag.R
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import org.koin.android.viewmodel.ext.android.viewModel
import com.devwujot.hashtag.databinding.FragmentHomeBinding
import com.devwujot.hashtag.framework.utility.reObserve
import com.devwujot.hashtag.framework.viewModel.HomeFragmentViewModel
import com.devwujot.hashtag.presentation.adapters.TweetListAdapter

class HomeFragment : BaseFragment() {

    private val viewModel: HomeFragmentViewModel by viewModel()
    private lateinit var binding: FragmentHomeBinding

    private val isLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            binding.tagList.isClickable = false
            binding.homeProgressLayout.visibility = View.VISIBLE
        } else {
            binding.tagList.isClickable = true
            binding.homeProgressLayout.visibility = View.GONE
        }
    }

    private val userIdObserver = Observer<String> { userId ->
        userId?.let {
            this.userId = it
            tweetListAdapter.userId = it
        }
    }

    private val userObserver = Observer<User> {
        this.user = it
    }

    private val tweetsObserver = Observer<ArrayList<Tweet>> { list ->
        list?.let {
            tweetListAdapter.updateTweets(list)
            binding.tagList?.visibility = View.VISIBLE

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeTweetListAdapter()

        viewModel.apply {
            isLoading.reObserve(this@HomeFragment, isLoadingObserver)
            userId.reObserve(this@HomeFragment, userIdObserver)
            user.reObserve(this@HomeFragment, userObserver)
            tweets.reObserve(this@HomeFragment, tweetsObserver)
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            updateList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.executePendingBindings()
    }

    private fun initializeTweetListAdapter() {
        tweetListAdapter = TweetListAdapter(userId, arrayListOf())
        tweetListAdapter.onTweetClickListener = { tweet ->
            handleFollowUser(tweet)
        }
        tweetListAdapter.onLikeClickListener = { tweet ->
            viewModel.updateTweeLikes(tweet)
        }
        tweetListAdapter.onReTweetClickListener = { tweet ->
            viewModel.updateReTweet(tweet)
        }
        binding.tagList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tweetListAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun updateList() {
        tweetListAdapter.updateTweets(arrayListOf())
        viewModel.getHomeTweets()
    }

    private fun handleFollowUser(tweet: Tweet) {
        tweet.let {
            val owner = tweet.userIds?.get(0)
            if (owner != userId) {
                if (user.followUsers?.contains(owner) == true) {
                    AlertDialog.Builder(binding.tagList.context)
                        .setTitle("${resources.getString(R.string.unfollow)} ${tweet.username}")
                        .setPositiveButton(resources.getString(R.string.modal_yes)) { dialog, which ->
                            binding.tagList.isClickable = false
                            var followedUsers = user.followUsers
                            if (followedUsers == null) {
                                followedUsers = arrayListOf()
                            }
                            followedUsers.remove(owner)
                            viewModel.unfollowUser(followedUsers)
                        }
                        .setNegativeButton(resources.getString(R.string.modal_cancel)) { dialog, which -> }
                        .show()
                } else {
                    AlertDialog.Builder(binding.tagList.context)
                        .setTitle("${resources.getString(R.string.follow)} ${tweet.username}")
                        .setPositiveButton(resources.getString(R.string.modal_yes)) { dialog, which ->
                            binding.tagList.isClickable = false
                            var followedUsers = user.followUsers
                            if (followedUsers == null) {
                                followedUsers = arrayListOf()
                            }
                            owner?.let {
                                followedUsers?.add(owner)
                                viewModel.followUser(followedUsers!!)
                            }
                        }
                        .setNegativeButton(resources.getString(R.string.modal_cancel)) { dialog, which -> }
                        .show()
                }
            }
        }
    }
}
