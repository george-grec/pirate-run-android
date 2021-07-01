package com.example.piraterun.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.piraterun.databinding.ProfileEditFragmentBinding
import com.example.piraterun.ui.login.afterTextChanged
import com.example.piraterun.util.getViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

class ProfileEditFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels { getViewModelFactory() }
    private lateinit var binding: ProfileEditFragmentBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileEditFragmentBinding.inflate(
            inflater, container, false
        ).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.profileAvatar.setImageResource(resources.getIdentifier(
            viewModel.loggedInUser.value!!.selectedAvatar, "drawable",
            "com.example.piraterun"))
        Log.i("ranks", viewModel.ranks.value.toString())
        Log.i("avatars", viewModel.avatars.value.toString())
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.ranks.value!!
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.rankSpinner.adapter = it
        }
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.avatars.value!!
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.avatarSpinner.adapter = it
        }

        // spinner only visible when there are ranks to choose from
        if (viewModel.ranks.value.isNullOrEmpty()) binding.rankSpinner.visibility = View.GONE
        if (viewModel.avatars.value.isNullOrEmpty()) binding.avatarSpinner.visibility = View.GONE

        binding.saveButton.isEnabled = false

        viewModel.profileFormState.observe(viewLifecycleOwner) {
            binding.saveButton.isEnabled = it.isDataValid

            if (it.usernameError != null) {
                binding.usernameEditText.error = getString(it.usernameError)
            }
            if (it.emailError != null) {
                binding.emailEditText.error = getString(it.emailError)
            }
        }

        binding.usernameEditText.afterTextChanged {
            viewModel.profileDataChanged(
                binding.usernameEditText.text.toString(),
                binding.emailEditText.text.toString()
            )
        }

        binding.emailEditText.afterTextChanged {
            viewModel.profileDataChanged(
                binding.usernameEditText.text.toString(),
                binding.emailEditText.text.toString()
            )
        }

        binding.saveButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val rank =
                if (binding.rankSpinner.selectedItem != null) binding.rankSpinner.selectedItem.toString()
                else ""
            val avatar =
                if (binding.avatarSpinner.selectedItem != null) binding.avatarSpinner.selectedItem.toString()
                else ""

            if (viewModel.loggedInUser.value!!.email == email) {
                viewModel.saveUser(username, email, rank, avatar)
                changeImage()
                Toast.makeText(context, "Changes saved!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                firestore.collection("users").whereEqualTo("email", email).get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) {
                            viewModel.saveUser(username, email, rank, avatar)
                            changeImage()
                            Toast.makeText(context, "Changes saved!", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        } else {
                            Toast.makeText(context, "Email already used!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun changeImage() {
        binding.profileAvatar.setImageResource(resources.getIdentifier(
            viewModel.loggedInUser.value!!.selectedAvatar, "drawable",
            "com.example.piraterun"))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }
}