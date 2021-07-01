package com.example.piraterun.ui.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piraterun.data.UserRepository
import com.example.piraterun.data.entities.User
import com.google.firebase.firestore.FirebaseFirestore

class FriendsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _loggedInUser: LiveData<User?> = userRepository.user
    val loggedInUser: LiveData<User?> = _loggedInUser

    private val _selectedFriend: MutableLiveData<User> = MutableLiveData()
    val selectedFriend: LiveData<User> = _selectedFriend

    // mock friends, are overwritten in the init method
    val friends: MutableLiveData<ArrayList<User>> = MutableLiveData(
        userRepository.friends
    )

    // init friends of user and list of all users from Firestore
    init {
        firestore.collection("users").limit(15).get().addOnSuccessListener { documents ->
            val users = ArrayList<User>()
            for (d in documents) {
                val u = d.toObject(User::class.java)
                if (u.email != loggedInUser.value!!.email) users.add(u)
            }
            _allUsers.value = users
        }
    }

    // mock all users in DB, are overwritten in the init method
    private val _allUsers: MutableLiveData<List<User>> = MutableLiveData()
    val allUsers: LiveData<List<User>> = _allUsers

    fun selectFriend(user: User) {
        _selectedFriend.value = user
    }

    fun addFriend(user: User) {
        val friendList: ArrayList<User> = friends.value!!
        friendList.add(user)
        friends.value = friendList
        Log.i("friend add", friendList.toString())
        userRepository.addFriend(user.id)
    }

    fun removeFriend() {
        val friendList: ArrayList<User> = friends.value!!
        friendList.remove(selectedFriend.value)
        friends.value = friendList
        Log.i("friend remove", friendList.toString())
        userRepository.removeFriend(selectedFriend.value!!.id)
    }

    fun displayRank() = if (loggedInUser.value?.selectedRank == "") {
        "No rank acquired yet!"
    } else {
        "Rank: ${loggedInUser.value?.selectedRank}"
    }
}