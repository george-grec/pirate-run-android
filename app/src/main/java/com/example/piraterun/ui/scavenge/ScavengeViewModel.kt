package com.example.piraterun.ui.scavenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piraterun.data.UserRepository
import com.example.piraterun.data.entities.User
import com.google.firebase.firestore.FirebaseFirestore

class ScavengeViewModel(userRepository: UserRepository) : ViewModel() {
    private val _loggedInUser: MutableLiveData<User?> = userRepository.user
    val loggedInUser: LiveData<User?> = _loggedInUser

    private val _friend: MutableLiveData<User?> = MutableLiveData(User())
    val friend: LiveData<User?> = _friend

    private val _remainingChests: MutableLiveData<Int> = MutableLiveData(0)
    val remainingChests: LiveData<Int> = _remainingChests

    private val firestore = FirebaseFirestore.getInstance()

    fun setFriend(friendId: String) {
        firestore.collection("users").document(friendId).get()
            .addOnSuccessListener { snapshot ->
                val u = snapshot.toObject(User::class.java)
                _friend.value = u
            }
    }

    fun increaseRemaingChests() {
        _remainingChests.value = _remainingChests.value!! + 1
    }

    fun decreaseRemainingChests() {
        _remainingChests.value = _remainingChests.value!! - 1
    }

    fun setChestNumber(chestNumber: Int) {
        _remainingChests.value = chestNumber
    }
}