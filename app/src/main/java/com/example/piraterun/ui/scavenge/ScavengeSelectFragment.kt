package com.example.piraterun.ui.scavenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.piraterun.ScavengeMapActivity
import com.example.piraterun.databinding.ScavengeLocationSelectBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ScavengeSelectFragment : Fragment() {

    private lateinit var binding: ScavengeLocationSelectBinding
    private var lobbyOwner = false
    private lateinit var gameRef: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScavengeLocationSelectBinding.inflate(
            inflater, container, false
        )

        lobbyOwner = (activity as ScavengeMapActivity).lobbyOwner
        val gameSessionId = (activity as ScavengeMapActivity).gameSessionId
        gameRef = FirebaseFirestore.getInstance().collection("game_sessions").document(gameSessionId)
/*
        if (!lobbyOwner) {
            gameRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("yeet", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"

                if (snapshot != null && snapshot.exists()) {
                    Log.d("yeet", "$source data: ${snapshot.data}")

                    val gameStarted = snapshot.getBoolean("game_started")
                    if (gameStarted != null && gameStarted) {
                        startGame()
                    }


                } else {
                    Log.d("yeet", "$source data: null")
                }
            }

        } else {

        }*/
        binding.button2.setOnClickListener {

            startGame()

        }

        return binding.root
    }

    private fun startGame() {

        if ((activity as ScavengeMapActivity).isGameReady()) {
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            var overlayFragment = ScavengeGameFragment()

            transaction.replace((requireView().parent as ViewGroup).id, overlayFragment);
            transaction.addToBackStack(null);

            transaction.commit()
            (activity as ScavengeMapActivity).startGame()
        }
    }

}