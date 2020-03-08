package com.devwujot.hashtag.framework

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.AuthCridential
import com.devwujot.hashtag.core.data.Tweet
import com.devwujot.hashtag.core.data.User
import com.devwujot.hashtag.core.repository.FirebaseDataSource
import com.devwujot.hashtag.framework.utility.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseDataSourceImpl(var context: Context) : FirebaseDataSource {

    val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firebaseDB: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val firebaseStorage: StorageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    override fun login(authCridential: AuthCridential, uid: MutableLiveData<String>) {
        firebaseAuth.signInWithEmailAndPassword(
            authCridential.email,
            authCridential.password
        )
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("Login", "User not found.")
                } else {
                    uid.postValue(firebaseAuth.currentUser?.uid)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    override fun logout() = firebaseAuth.signOut()

    override fun getCurrentUser() = firebaseAuth.currentUser?.uid

    override fun signup(user: User, password: String, uid: MutableLiveData<String>) {
        firebaseAuth.createUserWithEmailAndPassword(
            user.email,
            password
        )
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("Signup", "Could not create a user.")
                } else {
                    val email = user.email
                    val name = user.username
                    val newUser = User(name, email)
                    firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(newUser)
                    uid.postValue(firebaseAuth.currentUser?.uid)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    override fun getUser(user: MutableLiveData<User>, isLoading: MutableLiveData<Boolean>) {
        isLoading.postValue(true)
        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val data = documentSnapshot.toObject(User::class.java)
                user.postValue(data)
                isLoading.postValue(false)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                isLoading.postValue(false)
            }
    }

    override fun updateUser(user: MutableLiveData<User>, isLoading: MutableLiveData<Boolean>) {
        user.value?.let {
            isLoading.postValue(true)
            val map = HashMap<String, Any>()
            map[DATA_USER_USERNAME] = it.username
            map[DATA_USER_EMAIL] = it.email
            firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).update(map)
                .addOnSuccessListener {
                    Log.d("Update User", "Success !")
                    isLoading.postValue(false)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Log.d("Update User", "Failed !")
                    isLoading.postValue(false)
                }
        }
    }

    override fun storeImage(
        imageUrl: Uri,
        user: MutableLiveData<User>,
        isLoading: SingleLiveEvent<Boolean>
    ) {
        isLoading.postValue(true)
        val filePath = firebaseStorage.child(DATA_IMAGES).child(firebaseAuth.uid!!)
        filePath.putFile(imageUrl)
            .addOnSuccessListener {
                filePath.downloadUrl
                    .addOnSuccessListener { uri ->
                        val url = uri.toString()
                        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!)
                            .update(DATA_USER_IMAGE_URL, url)
                            .addOnSuccessListener {
                                user.value?.let {
                                    val tempUser = User(it.username, it.email, url)
                                    user.postValue(tempUser)
                                    isLoading.postValue(false)
                                }
                            }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        Log.e("Image Upload", "Image upload failed.")
                        isLoading.postValue(false)
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Log.e("Image Upload", "Image upload failed.")
                isLoading.postValue(false)
            }
    }

    override fun storeTweetImage(
        imageUrl: Uri,
        image: MutableLiveData<String>,
        isLoading: MutableLiveData<Boolean>
    ) {
        isLoading.postValue(true)
        val filePath = firebaseStorage.child("TweetImages").child(firebaseAuth.uid!!)
            .child(imageUrl.lastPathSegment.toString())
        filePath.putFile(imageUrl)
            .addOnSuccessListener {
                filePath.downloadUrl
                    .addOnSuccessListener { uri ->
                        image.postValue(uri.toString())
                        isLoading.postValue(false)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        Log.e("Image Upload", "Image upload failed.")
                        isLoading.postValue(false)
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Log.e("Image Upload", "Image upload failed.")
                isLoading.postValue(false)
            }
    }

    override fun postTweet(tweet: Tweet, isLoading: MutableLiveData<Boolean>) {
        tweet.let {
            it.userIds = arrayListOf(firebaseAuth.uid!!)
            isLoading.postValue(true)
            val tweetId = firebaseDB.collection(DATA_TWEETS).document()
            it.tweetId = tweetId.id
            tweetId.set(it)
                .addOnCompleteListener {
                    Log.e("Post Tweet", "Success")
                    isLoading.postValue(false)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    isLoading.postValue(false)
                }
        }
    }

    override fun searchTweets(
        tweets: MutableLiveData<List<Tweet>>,
        currenHashtag: String,
        isLoading: MutableLiveData<Boolean>
    ) {
        isLoading.postValue(true)
        firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, currenHashtag)
            .get()
            .addOnSuccessListener { list ->
                val tempTweets = arrayListOf<Tweet>()
                for (document in list.documents) {
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.let { tempTweets.add(it) }
                }
                val sortedTweets = tempTweets.sortedWith(compareByDescending { it.timestamp })
                tweets.postValue(sortedTweets)
                isLoading.postValue(false)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                isLoading.postValue(false)
            }
    }

    override fun updateFollowHashTags(
        currentHashTag: String,
        user: MutableLiveData<User>,
        isLoading: MutableLiveData<Boolean>
    ) {
        isLoading.postValue(true)
        val followed = user.value?.followHashtags
        followed?.let {
            if (it.contains(currentHashTag)) {
                it.remove(currentHashTag)
            } else {
                it.add(currentHashTag)
            }
        }
        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!)
            .update(DATA_USER_HASHTAGS, followed)
            .addOnSuccessListener {
                isLoading.postValue(false)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                isLoading.postValue(false)
            }
    }

    override fun updateTweetLikes(tweet: Tweet, isLoading: MutableLiveData<Boolean>) {
        isLoading.postValue(true)
        tweet.let {
            val likes = it.likes
            if (it.likes?.contains(firebaseAuth.uid)!!) {
                likes?.remove(firebaseAuth.uid)
            } else {
                likes?.add(firebaseAuth.uid!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!)
                .update(DATA_TWEET_LIKES, likes)
                .addOnSuccessListener {
                    isLoading.postValue(false)
                }
                .addOnFailureListener {
                    isLoading.postValue(false)
                }
        }
    }

    override fun updateReTweet(tweet: Tweet, isLoading: MutableLiveData<Boolean>) {
        isLoading.postValue(true)
        tweet.let {
            val reTweets = tweet.userIds
            if (reTweets?.contains(firebaseAuth.uid) == true) {
                reTweets.remove(firebaseAuth.uid)
            } else {
                reTweets?.add(firebaseAuth.uid!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!)
                .update(DATA_TWEET_USERIDS, reTweets)
                .addOnSuccessListener {
                    isLoading.postValue(false)
                }
                .addOnFailureListener {
                    isLoading.postValue(false)
                }
        }
    }

    override fun unfollowUser(
        followedUsers: ArrayList<String>,
        isLoading: MutableLiveData<Boolean>
    ) {
        isLoading.postValue(true)
        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!)
            .update(DATA_USERS_FOLLOW, followedUsers)
            .addOnSuccessListener {
                isLoading.postValue(false)
            }
            .addOnFailureListener {
                isLoading.postValue(false)
            }
    }

    override fun followUser(followedUsers: ArrayList<String>, isLoading: MutableLiveData<Boolean>) {
        isLoading.postValue(true)
        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!)
            .update(DATA_USERS_FOLLOW, followedUsers)
            .addOnSuccessListener {
                isLoading.postValue(false)
            }
            .addOnFailureListener {
                isLoading.postValue(false)
            }
    }

    override fun getHomeTweets(
        user: MutableLiveData<User>,
        tweets: MutableLiveData<ArrayList<Tweet>>,
        isLoading: MutableLiveData<Boolean>
    ) {
        isLoading.postValue(true)
        user.value?.let { it ->
            val followedHashTag = it.followHashtags
            val newTweets = arrayListOf<Tweet>()
            for (hashTag in followedHashTag!!) {
                firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, hashTag)
                    .get()
                    .addOnSuccessListener { list ->
                        Log.e("From DB", list.toString())
                        for (document in list.documents) {
                            val tweet = document.toObject(Tweet::class.java)
                            tweet?.let { newTweets.add(it) }
                        }
                        tweets.postValue(newTweets)
                        isLoading.postValue(false)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        isLoading.postValue(false)
                        Log.e("From DB", "failure")
                    }
            }
        }
    }
}