package com.devwujot.hashtag.presentation.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import com.devwujot.hashtag.R
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.databinding.ActivityHomeBinding
import com.devwujot.hashtag.framework.utility.reObserve
import com.devwujot.hashtag.framework.viewModel.HomeActivityViewModel
import com.devwujot.hashtag.presentation.fragments.HomeFragment
import com.devwujot.hashtag.presentation.fragments.MyActivityFragment
import com.devwujot.hashtag.presentation.fragments.SearchFragment
import com.google.android.material.tabs.TabLayout
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }

    private var sectionsPagerAdapter: SectionPagerAdapter? = null
    private val viewModel: HomeActivityViewModel by viewModel()
    private lateinit var binding: ActivityHomeBinding
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()
    private var user: User? = null

    private val logoutObserver = Observer<Boolean> { isLoggedOut ->
        if (isLoggedOut) {
            startActivity(
                LoginActivity.newIntent(
                    this
                )
            )
            finish()
        }
    }

    private val uidObserver = Observer<String> { uid ->
        if (uid.isNullOrEmpty()) {
            startActivity(LoginActivity.newIntent(this))
            finish()
        } else {
            Log.e("SUCCESS:", "Got it.")
        }
    }

    private val isLogoClickedObserver = Observer<Boolean> { isLogoClicked ->
        if (isLogoClicked) {
            startActivity(ProfileActivity.newIntent(this))
        }
    }

    private val userObserver = Observer<User> { user ->
        user?.let {
            binding.user = user
            this.user = user
        }
    }

    private val isLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            binding.homeProgressLayout.visibility = View.VISIBLE
        } else {
            binding.homeProgressLayout.visibility = View.GONE
        }
    }

    private val isAddTweetClickedObserver = Observer<Boolean> { isAddTweetClicked ->
        if (isAddTweetClicked) {
            startActivity(TweetActivity.newIntent(this, user?.username))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewModel = viewModel

        initViewPager()

        binding.homeProgressLayout.setOnTouchListener { v, event -> true }

        viewModel.apply {
            logout.reObserve(this@HomeActivity, logoutObserver)
            uid.reObserve(this@HomeActivity, uidObserver)
            isLogoClicked.reObserve(this@HomeActivity, isLogoClickedObserver)
            user.reObserve(this@HomeActivity, userObserver)
            isLoading.reObserve(this@HomeActivity, isLoadingObserver)
            isAddTweetClicked.reObserve(this@HomeActivity, isAddTweetClickedObserver)
        }

        binding.search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFragment.newHashTag(v?.text.toString())
            }
            true
        }
    }

    private fun initViewPager() {
        sectionsPagerAdapter = SectionPagerAdapter(supportFragmentManager)
        binding.apply {
            container.adapter = sectionsPagerAdapter
            container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabs))
            tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.container))
            tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position) {
                        0 -> {
                            binding.titleBar.visibility = View.VISIBLE
                            binding.titleBar.text = "Home"
                            binding.searchBar.visibility = View.GONE
                        }
                        1 -> {
                            binding.titleBar.visibility = View.GONE
                            binding.searchBar.visibility = View.VISIBLE
                        }
                        2 -> {
                            binding.titleBar.visibility = View.VISIBLE
                            binding.titleBar.text = "My Activity"
                            binding.searchBar.visibility = View.GONE
                        }
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.refresh()
    }

    inner class SectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> homeFragment
                1 -> searchFragment
                else -> myActivityFragment
            }
        }

        override fun getCount() = 3

    }

    override fun onDestroy() {
        super.onDestroy()

        binding.executePendingBindings()
    }
}