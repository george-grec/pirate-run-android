package com.example.piraterun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.piraterun.ui.shop.ShopFragment

class ShopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ShopFragment.newInstance())
                .commitNow()
        }
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}