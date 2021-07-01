package com.example.piraterun

import android.app.Application
import com.example.piraterun.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PirateApplication : Application() {

    val userRepository by lazy { UserRepository() }
}