package com.devwujot.hashtag.presentation.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import org.koin.android.viewmodel.ext.android.viewModel
import com.devwujot.hashtag.databinding.FragmentHomeBinding
import com.devwujot.hashtag.framework.utility.reObserve
import com.devwujot.hashtag.framework.viewModel.HomeFragmentViewModel
import com.devwujot.hashtag.presentation.adapters.TweetListAdapter

class HomeFragment : BaseFragment() {

    private var userId = ""
    private lateinit var user: User
    private val viewModel: HomeFragmentViewModel by viewModel()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var tweetListAdapter: TweetListAdapter

    private val isLoadingObserver = Observer<Boolean> { isLoading ->
        binding.tagList.isClickable = !isLoading
    }

    private val userIdObserver = Observer<String> { userId ->
        userId?.let {
            this.userId = it
            tweetListAdapter.userId = it
            Log.e("From Home feagment", userId)
        }
    }

    private val userObserver = Observer<User> {
        user = it
        Log.e("From Home feagment", it.toString())
    }

    private val tweetsObserver = Observer<ArrayList<Tweet>> { list ->
        list?.let {
            tweetListAdapter.updateTweets(list)
            binding.tagList?.visibility = View.VISIBLE
            Log.e("From Home feagment", it.toString())
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

        viewModel.getHomeTweets()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.executePendingBindings()
    }

    private fun initializeTweetListAdapter() {
        tweetListAdapter = TweetListAdapter(userId, arrayListOf())
//        tweetListAdapter.onTweetClickListener = { tweet ->
//            handleFollowUser(tweet)
//        }
//        tweetListAdapter.onLikeClickListener = { tweet ->
//            viewModel.updateTweeLikes(tweet)
//        }
//        tweetListAdapter.onReTweetClickListener = { tweet ->
//            viewModel.updateReTweet(tweet)
//        }
        binding.tagList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tweetListAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun updateList() {
        viewModel.getHomeTweets()
    }
}
