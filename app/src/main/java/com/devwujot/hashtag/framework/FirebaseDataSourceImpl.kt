package com.devwujot.hashtag.framework

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.devwujot.hashtag.core.data.AuthCridential
import com.devwujot.hashtag.core.data.Resource
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

    override fun login(
        authCridential: AuthCridential,
        loginResponse: MutableLiveData<Resource<*>>
    ) {
        loginResponse.postValue(Resource.loading(true))
        firebaseAuth.signInWithEmailAndPassword(
            authCridential.email,
            authCridential.password
        )
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    loginResponse.postValue(Resource.error("Could not login"))
                } else {
                    loginResponse.postValue(Resource.success(firebaseAuth.currentUser?.uid))
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                loginResponse.postValue(Resource.error(e.message))
            }
    }

    override fun logout() = firebaseAuth.signOut()

    override fun getCurrentUser() = firebaseAuth.currentUser?.uid

    override fun signup(user: User, password: String, signupResponse: MutableLiveData<Resource<*>>) {
        signupResponse.postValue(Resource.loading(true))
        firebaseAuth.createUserWithEmailAndPassword(
            user.email,
            password
        )
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    signupResponse.postValue(Resource.error("Could not create a user"))
                } else {
                    val email = user.email
                    val name = user.username
                    val newUser = User(name, email)
                    firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(newUser)
                    signupResponse.postValue(Resource.success(firebaseAuth.currentUser?.uid))
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                signupResponse.postValue(Resource.error(e.message))
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

    override fun updateUser(user: MutableLiveData<User>, updateResponse: MutableLiveData<Resource<*>>) {
        user.value?.let {
            updateResponse.postValue(Resource.loading(true))
            val map = HashMap<String, Any>()
            map[DATA_USER_USERNAME] = it.username
            map[DATA_USER_EMAIL] = it.email
            firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).update(map)
                .addOnSuccessListener {
                    updateResponse.postValue(Resource.success("User updated successfully!"))
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    updateResponse.postValue(Resource.error("Could not update user. Please, try again."))
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
                        for (document in list.documents) {
                            val tweet = document.toObject(Tweet::class.java)
                            tweet?.let { newTweets.add(it) }
                        }
                        tweets.postValue(ArrayList(sortTweetList(newTweets)))
                        isLoading.postValue(false)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        isLoading.postValue(false)
                    }
            }

            for (followedUser in it.followUsers!!) {
                firebaseDB.collection(DATA_TWEETS)
                    .whereArrayContains(DATA_TWEET_USERIDS, followedUser).get()
                    .addOnSuccessListener { list ->
                        for (document in list.documents) {
                            val tweet = document.toObject(Tweet::class.java)
                            tweet?.let { newTweets.add(it) }
                            tweets.postValue(ArrayList(sortTweetList(newTweets)))
                            isLoading.postValue(false)
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        isLoading.postValue(false)
                    }
            }
        }
    }

    private fun removeDuplicates(originalList: List<Tweet>) =
        originalList.distinctBy { it.timestamp }

    private fun sortTweetList(tweets: List<Tweet>): List<Tweet> {
        val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
        return removeDuplicates(sortedTweets)
    }

    override fun getMyActivityTweets(
        tweets: MutableLiveData<ArrayList<Tweet>>,
        isLoading: MutableLiveData<Boolean>
    ) {
        isLoading.postValue(true)
        val newTweets = arrayListOf<Tweet>()

        firebaseDB.collection(DATA_TWEETS)
            .whereArrayContains(DATA_TWEET_USERIDS, firebaseAuth.uid!!).get()
            .addOnSuccessListener { list ->
                for (document in list.documents) {
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.let { newTweets.add(tweet) }
                }
                tweets.postValue(ArrayList(sortTweetList(newTweets)))
                isLoading.postValue(false)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                isLoading.postValue(false)
            }
    }

    override fun testLogin(
        authCridential: AuthCridential,
        loginResponse: MutableLiveData<Resource<*>>
    ) {
        loginResponse.postValue(Resource.loading(true))
        firebaseAuth.signInWithEmailAndPassword(
            authCridential.email,
            authCridential.password
        )
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    loginResponse.postValue(Resource.error("User does not exist"))
                } else {
                    loginResponse.postValue(Resource.success(firebaseAuth.currentUser?.uid))
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                loginResponse.postValue(Resource.error("Login failed. Please, try again"))
            }
    }
}