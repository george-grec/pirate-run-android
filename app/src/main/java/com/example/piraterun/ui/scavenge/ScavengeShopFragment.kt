package com.example.piraterun.ui.scavenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.piraterun.ScavengeMapActivity
import com.example.piraterun.databinding.ScavengeShopFragmentBinding

class ScavengeShopFragment : Fragment() {

    private lateinit var binding: ScavengeShopFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScavengeShopFragmentBinding.inflate(
            inflater, container, false
        )

        binding.buyButton1.setOnClickListener {

            (activity as ScavengeMapActivity).stealBack()
        }

        binding.buyButton2.setOnClickListener {

            (activity as ScavengeMapActivity).getDirections()
        }

        binding.buyButton3.setOnClickListener {

            (activity as ScavengeMapActivity).chestReveal()
        }

        return binding.root
    }


}