package com.example.piraterun

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.contentcapture.ContentCaptureSessionId
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.piraterun.ui.scavenge.ScavengeCreateGameFragment
import com.example.piraterun.ui.scavenge.ScavengeLobbyFragment
import com.example.piraterun.ui.scavenge.ScavengeSelectFragment
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase


class ScavengeActivity : AppCompatActivity() {

    lateinit var gameSessionId: String
    var isLobbyOwner = false
    var sharedRadius = false

    fun getContextOfApplication(): Context? {
        return applicationContext
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scavenge_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val gameSessionId = intent.getStringExtra("game_id")
        val newFragment: Fragment


        /** if game session id is given via intent (= dynamic link invite), then you join the lobby
        otherwise create a game lobby
         **/

        if (gameSessionId != null) {
            newFragment = ScavengeLobbyFragment()
            isLobbyOwner = false
            this.gameSessionId = gameSessionId
        } else {
            newFragment = ScavengeCreateGameFragment()
            isLobbyOwner = true
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer2, newFragment).commit()
    }


    // this event will enable the back
    // function to the button on press
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

}