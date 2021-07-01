package com.example.piraterun.ui.profile

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piraterun.R
import com.example.piraterun.data.UserRepository
import com.example.piraterun.data.entities.User
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val resources = ProfileFragment.RESOURCES
    private val firestore = FirebaseFirestore.getInstance()

    private val _loggedInUser: LiveData<User?> = userRepository.user
    val loggedInUser: LiveData<User?> = _loggedInUser

    private val _profileFormState: MutableLiveData<ProfileFormState> = MutableLiveData()
    val profileFormState: LiveData<ProfileFormState> = _profileFormState

    var ranks: MutableLiveData<ArrayList<String>> = MutableLiveData(
        userRepository.ranks
    )

    var avatars: MutableLiveData<ArrayList<String>> = MutableLiveData(
        userRepository.avatars
    )

    fun displayRank(): String {
        return if (loggedInUser.value!!.selectedRank == "") {
            "No rank acquired yet!"
        } else {
            "Rank: ${loggedInUser.value?.selectedRank}"
        }
    }

    fun displayUsername() = "Username: ${loggedInUser.value?.username}"

    fun displayEmail() = "Email: ${loggedInUser.value?.email}"

    fun saveUser(username: String, email: String, rank: String, avatar: String) {
        if (isUsernameValid(username)) {
            _loggedInUser.value?.username = username
        }
        if (isEmailValid(email)) {
            _loggedInUser.value?.email = email
        }
        _loggedInUser.value?.selectedRank = rank
        _loggedInUser.value?.selectedAvatar = avatar

        // set logged in user
        userRepository.setLoggedInUser(_loggedInUser.value!!)

        // save changes to Firestore
        firestore.collection("users").document(userRepository.userRef)
            .update("username", username, "email", email, "selectedRank", rank,
            "selectedAvatar", avatar)
    }

    fun profileDataChanged(username: String, email: String) {
        if (!isUsernameValid(username)) {
            _profileFormState.value = ProfileFormState(usernameError = R.string.invalid_username)
        } else if (!isEmailValid(email)) {
            _profileFormState.value = ProfileFormState(emailError = R.string.invalid_email)
        } else {
            _profileFormState.value = ProfileFormState(isDataValid = true)
        }
    }

    private fun isEmailValid(email: String) =
        email.isNotEmpty() && email.isNotBlank() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isUsernameValid(username: String) = username.isNotBlank() && username.isNotEmpty()
}