package com.example.piraterun.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.piraterun.data.entities.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // in-memory cache of the loggedInUser object
    var user: MutableLiveData<User?> = MutableLiveData()
        private set

    // Firestore userRef of the loggedInUser
    var userRef: String = ""
        private set

    // friends of current logged in user
    var friends = ArrayList<User>()

    // purchased ranks
    var ranks = ArrayList<String>()

    // purchased avatars
    var avatars = ArrayList<String>()

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user.value = null
    }

    fun logout() {
        user.value = null
    }

    // load all friends from firebase
    fun loadFriends(userRef: String) {
        firestore.collection("users").document(userRef).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("listener", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val friendList = ArrayList<User>()
                if (snapshot.data?.get("friends") != null) {
                    for (f in snapshot.data!!["friends"] as List<DocumentReference>) {
                        f.get().addOnSuccessListener { friend ->
                            if (friend != null) {
                                friendList.add(friend.toObject(User::class.java)!!)
                            }
                        }
                    }
                    friends = friendList
                }
            }
        }
    }

    fun setLoggedInUser(loggedInUser: User) {
        this.user.value = loggedInUser
        ranks = loggedInUser.ranks
        avatars = loggedInUser.avatars
        Log.i("logged in user", this.user.value.toString())

        // load ranks
        firestore.collection("user").document(loggedInUser.id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("listener", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    if (snapshot.data != null) {
                        ranks = snapshot.data!!["ranks"] as ArrayList<String>
                        avatars = snapshot.data!!["avatars"] as ArrayList<String>
                    }
                }

        }
    }

    fun setLoggedInUserId(id: String) {
        this.userRef = id
        Log.i("logged in user ref", id)
    }

    // add friend to the currently logged in user to Firestore
    fun addFriend(friendId: String) {
        firestore.collection("users").document(userRef)
            .update(
                "friends",
                FieldValue.arrayUnion(firestore.collection("users").document(friendId))
            )
    }

    // remove friend from the currently logged in user from the Firestore
    fun removeFriend(friendId: String) {
        firestore.collection("users").document(userRef)
            .update(
                "friends",
                FieldValue.arrayRemove(firestore.collection("users").document(friendId))
            )
    }

    // add a rank to the currently logged in user from the Firestore
    fun buyRank(rank: String) {
        firestore.collection("users").document(userRef)
            .update(
                "coins", user.value!!.coins,
                "ranks", FieldValue.arrayUnion(rank)
            )
        ranks.add(rank)
    }

    // add an avatar to the currently logged in user from the Firestore
    fun buyAvatar(avatar: String) {
        firestore.collection("users").document(userRef)
            .update(
                "coins", user.value!!.coins,
                "avatars", FieldValue.arrayUnion(avatar)
            )
        avatars.add(avatar)
    }

    fun addCoins() {
        firestore.collection("users").document(userRef)
            .update(
                "coins", user.value!!.coins,
            )
    }
}