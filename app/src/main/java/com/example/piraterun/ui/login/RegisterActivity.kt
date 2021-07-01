package com.example.piraterun.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.piraterun.PirateApplication
import com.example.piraterun.data.entities.User
import com.example.piraterun.databinding.RegisterActivityBinding
import com.example.piraterun.util.EspressoIdlingResource
import com.google.firebase.firestore.FirebaseFirestore
import com.toxicbakery.bcrypt.Bcrypt

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: RegisterActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            LoginViewModelFactory((application as PirateApplication).userRepository).create(
                LoginViewModel::class.java
            )
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // disable create account button in the beginning
        var isDataValid = false
        binding.createAcc.isEnabled = isDataValid

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pwMatch =
                    binding.passwordReg.text.toString() == binding.confirmPassword.text.toString() &&
                            viewModel.isPasswordValid(binding.passwordReg.text.toString()) &&
                            binding.passwordReg.text.isNotBlank()

                if (binding.passwordReg.text.toString() != binding.confirmPassword.text.toString()) {
                    binding.confirmPassword.error = "Passwords do not match"
                } else if (!viewModel.isPasswordValid(binding.passwordReg.text.toString())) {
                    binding.passwordReg.error = "Passwords have to be at least 6 characters long"
                } else {
                    binding.passwordReg.error = null
                    binding.confirmPassword.error = null
                }

                isDataValid =
                    binding.usernameReg.text.isNotBlank() && binding.firstName.text.isNotBlank()
                            && binding.lastName.text.isNotBlank() &&
                            viewModel.isEmailValid(binding.email.text.toString()) && pwMatch

                if (!viewModel.isEmailValid(binding.email.text.toString())) {
                    binding.email.error = "Not a valid email"
                } else {
                    binding.email.error = null
                }

                binding.createAcc.isEnabled = isDataValid
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        // set Listeners for EditTexts
        binding.email.addTextChangedListener(watcher)
        binding.usernameReg.addTextChangedListener(watcher)
        binding.firstName.addTextChangedListener(watcher)
        binding.lastName.addTextChangedListener(watcher)
        binding.passwordReg.addTextChangedListener(watcher)
        binding.confirmPassword.addTextChangedListener(watcher)

        // set onClickListener for creating account button
        binding.createAcc.setOnClickListener {
            val email = binding.email.text.toString()
            val username = binding.usernameReg.text.toString()
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val password = binding.passwordReg.text.toString()

            // set new highest userId
            val db = FirebaseFirestore.getInstance()
            // check if user with email already exists
            EspressoIdlingResource.increment()
            db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        binding.email.error = "Email already in use"
                        Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT)
                            .show()
                        EspressoIdlingResource.decrement()
                        return@addOnSuccessListener
                    }
                    // create new user
                    val newUser = db.collection("users").document()
                    val id = newUser.id
                    val hashedPw = String(Bcrypt.hash(password, 8))
                    val user = User(id, username, email, hashedPw, firstName, lastName,
                        ArrayList(), "", ArrayList(), "ic_cook", 1000)
                    newUser.set(user)

                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                    EspressoIdlingResource.decrement()
                    finish()
                }
        }
    }
}