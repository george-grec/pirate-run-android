package com.example.piraterun.ui.scavenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.piraterun.ScavengeActivity
import com.example.piraterun.databinding.ScavengeCreategameFragmentBinding
import com.example.piraterun.util.getViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

class ScavengeCreateGameFragment : Fragment() {
    private lateinit var binding: ScavengeCreategameFragmentBinding
    private val viewModel: ScavengeViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScavengeCreategameFragmentBinding.inflate(
            inflater, container, false
        )

        val db = FirebaseFirestore.getInstance()
        var sharedRadius = false
        binding.startGameButton.setOnClickListener {
            var radius = binding.radiusTextInput.text.toString().toDouble()
            if (radius <= 0.0) {
                radius = 5.0 // default radius
            }
            val chestNumber = binding.chestsTextInput.text.toString().toInt()
            val coinNumber = binding.coinsTextInput.text.toString().toInt()
            val player1Id = viewModel.loggedInUser.value!!.id

            /** Set game session variables
             * **/
            val game = hashMapOf(
                "player1_id" to player1Id,
                "player2_id" to null,
                "game_ready" to false,
                "game_started" to false,
                "game_finished" to false,
                "chest_goal" to chestNumber,
                "coin_number" to coinNumber,
                "radius" to radius,
                "locations" to null,
                "locations2" to null,
                "player1_locations" to null,
                "player1_ready" to false,
                "player1_locations_found" to 0,
                "player1_winner" to false,
                "player2_locations" to null,
                "player2_ready" to false,
                "player2_locations_found" to 0,
                "player2_winner" to false
            )

            binding.radiusSwitch.isChecked

            db.collection("game_sessions").add(game)
                .addOnSuccessListener { documentReference ->
                    // set random game session id
                    Log.i("game sessions", "Session added with ID: ${documentReference.id}")
                    (activity as ScavengeActivity).gameSessionId = documentReference.id
                    (activity as ScavengeActivity).sharedRadius = sharedRadius

                    // switch to lobby fragment
                    val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                    val overlayFragment = ScavengeLobbyFragment()
                    transaction.replace((requireView().parent as ViewGroup).id, overlayFragment);
                    transaction.addToBackStack(null);
                    transaction.commit()
                }
        }

        // radius range slider
        binding.radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.radiusTextInput.setText((progress / 4.0).toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.radiusSwitch.setOnCheckedChangeListener{ _, isChecked ->
            sharedRadius = isChecked
        }


        return binding.root
    }
}