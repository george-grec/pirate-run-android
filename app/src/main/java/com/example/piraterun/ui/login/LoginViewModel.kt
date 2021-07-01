package com.example.piraterun.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piraterun.R
import com.example.piraterun.data.UserRepository
import com.example.piraterun.data.entities.User
import com.example.piraterun.util.EspressoIdlingResource
import com.google.firebase.firestore.FirebaseFirestore
import com.toxicbakery.bcrypt.Bcrypt
import kotlinx.coroutines.launch

class LoginViewModel(val userRepository: UserRepository) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(email: String, password: String) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            EspressoIdlingResource.increment()
            firestore.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val user = querySnapshot.documents[0].toObject(User::class.java)
                        val hashedPw = user!!.password.encodeToByteArray()
                        if (Bcrypt.verify(password, hashedPw)) {
                            Log.i("logged in user", user.toString())
                            _loginResult.value = LoginResult(
                                success = user
                            )
                            userRepository.setLoggedInUser(user)
                            userRepository.setLoggedInUserId(querySnapshot.documents[0].id)
                            userRepository.loadFriends(querySnapshot.documents[0].id)
                        } else {
                            _loginResult.value = LoginResult(
                                error = R.string.login_failed
                            )
                        }
                        EspressoIdlingResource.decrement()
                    } else {
                        _loginResult.value = LoginResult(
                            error = R.string.login_failed
                        )
                        EspressoIdlingResource.decrement()
                    }
                }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isEmailValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // username validation check
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // password validation check
    fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}