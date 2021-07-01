package com.example.piraterun.ui.gamemenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.piraterun.data.UserRepository
import com.example.piraterun.data.entities.User

class GameMenuViewModel(userRepository: UserRepository) : ViewModel() {

    private val _loggedInUser: LiveData<User?> = userRepository.user
    val loggedInUser: LiveData<User?> = _loggedInUser
}