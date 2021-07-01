package com.example.piraterun.ui.profile

import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.piraterun.R
import com.example.piraterun.databinding.ProfileFragmentBinding
import com.example.piraterun.util.getViewModelFactory

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels { getViewModelFactory() }
    private lateinit var binding: ProfileFragmentBinding

    companion object {
        lateinit var RESOURCES: Resources
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RESOURCES = resources
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // setup binding
        binding = ProfileFragmentBinding.inflate(
            inflater, container, false).apply {
            viewmodel = viewModel
        }
        binding.profileAvatar.setImageResource(resources.getIdentifier(
            viewModel.loggedInUser.value!!.selectedAvatar, "drawable",
            "com.example.piraterun"))

        setHasOptionsMenu(true)
        // setup back button to game menu
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.profile_menu_edit -> {
                val action = ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment()
                findNavController().navigate(action)
                true
            }
            else -> false
        }
}