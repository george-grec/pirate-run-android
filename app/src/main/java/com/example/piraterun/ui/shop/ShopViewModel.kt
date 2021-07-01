package com.example.piraterun.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piraterun.data.UserRepository
import com.example.piraterun.data.entities.Avatar
import com.example.piraterun.data.entities.Rank
import com.example.piraterun.data.entities.User

class ShopViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val resources = ShopFragment.RESOURCES

    private val _loggedInUser: MutableLiveData<User?> = userRepository.user
    val loggedInUser: LiveData<User?> = _loggedInUser

    // dummy data for ranks
    val ranks: List<Rank> = listOf(
        Rank(1, "Cook", 300),
        Rank(2, "Gunner", 500), Rank(3, "Captain", 1000)
    )

    // dummy data for avatars
    val avatars: List<Avatar> = listOf(
        Avatar(
            "ic_cook",
            resources.getIdentifier("ic_cook", "drawable", "com.example.piraterun"),
            "Cook",
            500
        ),
        Avatar("ic_pirate",
            resources.getIdentifier("ic_pirate", "drawable", "com.example.piraterun"),
            "Gunner",
            700),
        Avatar("ic_parrot",
            resources.getIdentifier("ic_parrot", "drawable", "com.example.piraterun"),
            "Captain",
            1500)
    )

    /**
     * Update the user coins after buying a rank item
     * @return true if buy was successful, otherwise false is returned
     */
    fun buyRank(price: Int, rank: String): Boolean {
        val userCoins = _loggedInUser.value!!.coins
        if (userCoins < price) return false
        _loggedInUser.value!!.coins -= price
        // to force UI changes
        _loggedInUser.value = _loggedInUser.value
        // save changes to Firestore
        userRepository.buyRank(rank)
        return true
    }

    fun buyAvatar(price: Int, avatar: String): Boolean {
        val userCoins = _loggedInUser.value!!.coins
        if (userCoins < price) return false
        _loggedInUser.value!!.coins -= price
        // to force UI changes
        _loggedInUser.value = _loggedInUser.value
        // save changes to Firestore
        userRepository.buyAvatar(avatar)
        return true
    }
}