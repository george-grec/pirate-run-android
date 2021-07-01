package com.example.piraterun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.piraterun.ui.gamemenu.GameMenuFragment

class GameMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_menu_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GameMenuFragment.newInstance())
                .commitNow()
        }

        val gameSessionId = intent.getStringExtra("game_id")

        if(gameSessionId != null) {
            val intent = Intent(this, ScavengeActivity::class.java)
            intent.putExtra("game_id", gameSessionId)

            startActivity(intent)
        }
    }
}