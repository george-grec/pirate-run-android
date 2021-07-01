package com.example.piraterun.ui.profile

data class ProfileFormState(val usernameError: Int? = null,
                          val emailError: Int? = null,
                          val isDataValid: Boolean = false)