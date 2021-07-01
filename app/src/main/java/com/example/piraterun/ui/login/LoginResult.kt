package com.example.piraterun.ui.login

import com.example.piraterun.data.entities.User

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val success: User? = null,
        val error: Int? = null
)