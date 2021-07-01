package com.example.piraterun.ui.scavenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.piraterun.ScavengeMapActivity
import com.example.piraterun.databinding.ScavengeGameFragmentBinding
import com.example.piraterun.util.getViewModelFactory

class ScavengeGameFragment : Fragment() {

    private lateinit var binding: ScavengeGameFragmentBinding
    private val viewModel: ScavengeViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScavengeGameFragmentBinding.inflate(
            inflater, container, false
        )
        val r1 = resources.getIdentifier(viewModel.loggedInUser.value!!.selectedAvatar,
            "drawable",
            "com.example.piraterun"
        )
        binding.imageView3.setImageResource(r1)
        val r2 = resources.getIdentifier(viewModel.friend.value!!.selectedAvatar,
            "drawable",
            "com.example.piraterun"
        )
        binding.imageView2.setImageResource(r2)

        binding.shopButton.text = "100"

        binding.shopButton.setOnClickListener {
            (activity as ScavengeMapActivity).toggleShop()
        }
        viewModel.friend.observe(viewLifecycleOwner) {
            val friendRes = resources.getIdentifier(it!!.selectedAvatar,
                "drawable",
                "com.example.piraterun"
            )
            binding.imageView2.setImageResource(friendRes)
        }

        return binding.root
    }

}