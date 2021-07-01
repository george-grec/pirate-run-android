package com.example.piraterun.ui.scavenge

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.piraterun.ScavengeActivity
import com.example.piraterun.ScavengeMapActivity
import com.example.piraterun.databinding.ScavengeLobbyFragmentBinding
import com.example.piraterun.util.getViewModelFactory
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ScavengeLobbyFragment : Fragment() {
    private lateinit var binding: ScavengeLobbyFragmentBinding
    private lateinit var currentGameSessionId: String
    private lateinit var gameRef: DocumentReference
    private var isLobbyOwner = false
    private var gameStarted = false
    private lateinit var applicationContext: Context
    private val viewModel: ScavengeViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScavengeLobbyFragmentBinding.inflate(
            inflater, container, false
        )
        val r = resources.getIdentifier(viewModel.loggedInUser.value!!.selectedAvatar,
            "drawable",
            "com.example.piraterun"
        )
        binding.player1Image.setImageResource(r)


        currentGameSessionId = (activity as ScavengeActivity).gameSessionId
        isLobbyOwner = (activity as ScavengeActivity).isLobbyOwner
        val db = FirebaseFirestore.getInstance()
        gameRef = db.collection("game_sessions").document(currentGameSessionId)

        // invite link generation
        if (isLobbyOwner) {
            val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                link = Uri.parse("https://www.example.com?game_id=$currentGameSessionId")
                domainUriPrefix = "https://piraterun.page.link"
                // Open links with this app on Android
                androidParameters {}
                // Open links with com.example.ios on iOS
                iosParameters("com.example.ios") { }
            }

            val dynamicLinkUri = dynamicLink.uri
            applicationContext = (activity as ScavengeActivity?)?.getContextOfApplication()!!
            val clipboard =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData =
                ClipData.newUri(applicationContext.contentResolver, "URI", dynamicLinkUri)
            clipboard.setPrimaryClip(clip)


            // create copy button
            val constraintLayout = binding.copyButtonContainer
            val copyButton = Button((activity as ScavengeActivity).applicationContext)

            copyButton.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            copyButton.text = "Share link"
            copyButton.setOnClickListener {
                clipboard.setPrimaryClip(clip)
                val toast = Toast.makeText(applicationContext, "Link copied", Toast.LENGTH_SHORT)
                toast.show()
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, dynamicLinkUri)
                    type = "text/text"
                }
                startActivity(sendIntent)
            }
            constraintLayout.addView(copyButton)

            // TODO: set avatars and player names

        } else {

            binding.startGameSessionButton.text = "JOIN"
            // TODO: set avatars and player names
            val player2Id = viewModel.loggedInUser.value!!.id
            gameRef.update(
                mapOf(
                    "player2_id" to player2Id,
                    "player2_ready" to true
                )
            )
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
                    if (snapshot.getBoolean("game_ready") == true) {
                        startGame()
                    }
                } else {
                    Log.d("yeet", "$source data: null")
                }
            }
        }

        binding.startGameSessionButton.setOnClickListener {
            if (isLobbyOwner) {
                gameRef.get().addOnSuccessListener {
                    if(it.get("player2_id") != null) {
                        gameRef.update("game_ready", true)
                        viewModel.setFriend(it.get("player2_id") as String)
                        startGame()
                    } else {
                        val toast = Toast.makeText(applicationContext, "Waiting for another player", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }

            }
        }
        return binding.root
    }

    fun startGame() {
        gameRef.get().addOnSuccessListener {
            if (!gameStarted) {
                gameStarted = true
                val startIntent = Intent(activity, ScavengeMapActivity::class.java)

                startIntent.putExtra("GAME_SESSION_ID", currentGameSessionId)

                val chestNumber = it.getLong("chest_goal")!!.toInt()
                startIntent.putExtra("CHEST_NUMBER", chestNumber)

                val radius = it.getLong("radius")!!.toInt()
                startIntent.putExtra("RADIUS", radius.toDouble())

                val coinNumber = it.getLong("coin_number")!!.toInt()
                startIntent.putExtra("COIN_NUMBER", coinNumber)

                if (isLobbyOwner) {
                    gameRef.update("game_started", true)
                    startIntent.putExtra("LOBBY_OWNER", true)
                } else {
                    startIntent.putExtra("LOBBY_OWNER", false)
                }

                startActivity(startIntent)
                (activity as ScavengeActivity).finish()
            }
        }
    }
}