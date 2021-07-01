package com.example.piraterun.ui.treasure_hunt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piraterun.data.UserRepository
import com.example.piraterun.data.entities.User

class TreasureHuntViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loggedInUser: MutableLiveData<User?> = userRepository.user
    val loggedInUser: LiveData<User?> = _loggedInUser

    fun addCoins(coins: Int): Boolean {
        _loggedInUser.value!!.coins += coins
        // to force UI changes
        _loggedInUser.value = _loggedInUser.value
        userRepository.addCoins()
        return true
    }

    var set: Int = 0
}