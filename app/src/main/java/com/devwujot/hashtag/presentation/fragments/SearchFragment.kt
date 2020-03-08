package com.devwujot.hashtag.presentation.fragments


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import org.koin.android.viewmodel.ext.android.viewModel
import com.devwujot.hashtag.databinding.FragmentSearchBinding
import com.devwujot.hashtag.framework.utility.reObserve
import com.devwujot.hashtag.framework.viewModel.SearchViewModel
import com.devwujot.hashtag.presentation.adapters.TweetListAdapter

class SearchFragment : BaseFragment() {

    private var currentHashtag = ""
    private var userId = ""
    private lateinit var user: User
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var tweetListAdapter: TweetListAdapter

    private val isLoadingObserver = Observer<Boolean> { isLoading ->
        binding.tagList.isClickable = !isLoading
    }

    private val userIdObserver = Observer<String> { userId ->
        userId?.let {
            this.userId = it
            tweetListAdapter.userId = it
        }
    }

    private val userObserver = Observer<User> {
        user = it
    }

    private val tweetsObserver = Observer<List<Tweet>> { list ->
        list?.let {
            tweetListAdapter.updateTweets(list)
            binding.tagList?.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeTweetListAdapter()

        viewModel.apply {
            isLoading.reObserve(this@SearchFragment, isLoadingObserver)
            userId.reObserve(this@SearchFragment, userIdObserver)
            user.reObserve(this@SearchFragment, userObserver)
            tweets.reObserve(this@SearchFragment, tweetsObserver)
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

    fun newHashTag(term: String) {
        this.currentHashtag = term
        binding.apply {
            currentHashTag = term
            followHashtag.visibility = View.VISIBLE
        }
        updateList()
    }

    fun updateList() {
        binding.tagList?.visibility = View.GONE
        viewModel.searchTweets(currentHashtag)
    }

    private fun handleFollowUser(tweet: Tweet) {
        tweet.let {
            val owner = tweet.userIds?.get(0)
            if (owner != userId) {
                if (user.followUsers?.contains(owner) == true) {
                    AlertDialog.Builder(binding.tagList.context)
                        .setTitle("Unfollow ${tweet.username}")
                        .setPositiveButton("yes") { dialog, which ->
                            binding.tagList.isClickable = false
                            var followedUsers = user.followUsers
                            if (followedUsers == null) {
                                followedUsers = arrayListOf()
                            }
                            followedUsers.remove(owner)
                            viewModel.unfollowUser(followedUsers)
                        }
                        .setNegativeButton("cancel") { dialog, which ->  }
                        .show()
                } else {
                    AlertDialog.Builder(binding.tagList.context)
                        .setTitle("Follow ${tweet.username}")
                        .setPositiveButton("yes") { dialog, which ->
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
                        .setNegativeButton("cancel") { dialog, which ->  }
                        .show()
                }
            }
        }
    }
}