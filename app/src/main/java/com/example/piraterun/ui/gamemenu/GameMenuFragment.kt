package com.example.piraterun.ui.gamemenu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.piraterun.*

import com.example.piraterun.databinding.GameMenuFragmentBinding
import com.example.piraterun.util.getViewModelFactory

class GameMenuFragment : Fragment() {

    companion object {
        fun newInstance() = GameMenuFragment()
    }

    private val viewModel: GameMenuViewModel by viewModels { getViewModelFactory() }

    private lateinit var binding: GameMenuFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameMenuFragmentBinding.inflate(
            inflater, container, false
        ).apply {
            viewmodel = viewModel
        }

        // set on click listener for buttons
        binding.player.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
        binding.shop.setOnClickListener {
            startActivity(Intent(activity, ShopActivity::class.java))
        }
        binding.friends.setOnClickListener {
            startActivity(Intent(activity, FriendsActivity::class.java))
        }

        //treasure hunt
        binding.treasureHunt.setOnClickListener {
            startActivity(Intent(activity, TreasureHuntActivity::class.java))
        }

        binding.scavenge.setOnClickListener {
            startActivity(Intent(activity, ScavengeActivity::class.java))
        }

        binding.avatar.setImageResource(resources.getIdentifier(
            viewModel.loggedInUser.value!!.selectedAvatar, "drawable",
            "com.example.piraterun"))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loggedInUser.observe(viewLifecycleOwner, {
            binding.username.text = it!!.username
            binding.coins.text = it.coins.toString()
        })
    }
}