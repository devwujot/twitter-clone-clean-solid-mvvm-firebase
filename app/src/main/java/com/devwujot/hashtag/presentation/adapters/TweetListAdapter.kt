package com.devwujot.hashtag.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.devwujot.hashtag.R
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.databinding.ItemTagBinding
import com.devwujot.hashtag.presentation.listeners.TweetListener

class TweetListAdapter(var userId: String, val tweets: ArrayList<Tweet>) :
    RecyclerView.Adapter<TweetListAdapter.TweetViewHolder>(), TweetListener {

    private lateinit var view: TweetViewHolder
    lateinit var onTweetClickListener: (Tweet) -> Unit
    lateinit var onLikeClickListener: (Tweet) -> Unit
    lateinit var onReTweetClickListener: (Tweet) -> Unit

    fun updateTweets(newTweets: List<Tweet>) {
        tweets.clear()
        tweets.addAll(newTweets)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemTagBinding>(
            inflater,
            R.layout.item_tag,
            parent,
            false
        )
        return TweetViewHolder(view)
    }

    override fun getItemCount() = tweets.size

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        view = holder
        holder.view.listener = this
        holder.view.tweet = tweets[position]
        holder.view.userId = userId
    }

    override fun onLayoutClick(v: View) {
        for (tweet in tweets) {
            if (v.tag == tweet.tweetId) {
                onTweetClickListener.invoke(tweet)
            }
        }
    }

    override fun onLike(v: View) {
        for (tweet in tweets) {
            if (v.tag == tweet.tweetId) {
                onLikeClickListener.invoke(tweet)
            }
        }
    }

    override fun onReTweet(v: View) {
        for (tweet in tweets) {
            if (v.tag == tweet.tweetId) {
                onReTweetClickListener.invoke(tweet)
            }
        }
    }

    class TweetViewHolder(var view: ItemTagBinding) :
            RecyclerView.ViewHolder(view.root)
}