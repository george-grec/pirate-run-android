package com.example.piraterun.ui.treasure_hunt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.piraterun.data.UserRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class TreasureHuntViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TreasureHuntViewModel::class.java)) {
            return TreasureHuntViewModel(userRepository = userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}