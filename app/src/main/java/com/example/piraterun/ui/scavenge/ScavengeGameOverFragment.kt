package com.example.piraterun.ui.scavenge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.piraterun.GameMenuActivity
import com.example.piraterun.ScavengeActivity
import com.example.piraterun.ScavengeMapActivity
import com.example.piraterun.databinding.ScavengeGameOverBinding

class ScavengeGameOverFragment : Fragment() {

    private lateinit var binding: ScavengeGameOverBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScavengeGameOverBinding.inflate(
            inflater, container, false
        )

        binding.mainMenuButton.setOnClickListener {
            startActivity(Intent(activity, GameMenuActivity::class.java))
            (activity as ScavengeMapActivity).finish()
        }

        binding.playAgainButton.setOnClickListener {

            // TODO: play again creates new lobby
            // only if both players click play again
            val startIntent = Intent(activity, ScavengeActivity::class.java)

            startActivity(startIntent)
            (activity as ScavengeMapActivity).finish()
        }

        return binding.root
    }

}