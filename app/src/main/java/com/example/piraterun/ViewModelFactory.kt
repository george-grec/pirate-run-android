package com.example.piraterun

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.piraterun.data.UserRepository
import com.example.piraterun.ui.friends.FriendsViewModel
import com.example.piraterun.ui.gamemenu.GameMenuViewModel
import com.example.piraterun.ui.profile.ProfileViewModel
import com.example.piraterun.ui.scavenge.ScavengeViewModel
import com.example.piraterun.ui.shop.ShopViewModel
import com.example.piraterun.ui.treasure_hunt.TreasureHuntViewModel
import java.lang.IllegalArgumentException

/**
 * Factory for all viewModels
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val userRepository: UserRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    )= with(modelClass) {
        when {
            //TreasureHuntViewModel
            isAssignableFrom(GameMenuViewModel::class.java) ->
                GameMenuViewModel(userRepository)
            isAssignableFrom(ShopViewModel::class.java) ->
                ShopViewModel(userRepository)
            isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(userRepository)
            isAssignableFrom(FriendsViewModel::class.java) ->
                FriendsViewModel(userRepository)
            isAssignableFrom(ScavengeViewModel::class.java) ->
                ScavengeViewModel(userRepository)
            isAssignableFrom(TreasureHuntViewModel::class.java) ->
                TreasureHuntViewModel(userRepository)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}