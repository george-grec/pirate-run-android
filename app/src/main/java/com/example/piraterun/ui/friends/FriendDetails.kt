package com.example.piraterun.ui.friends

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.example.piraterun.R
import com.example.piraterun.databinding.FriendDetailFragmentBinding
import com.example.piraterun.util.getViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FriendDetails : Fragment() {
    private val viewModel: FriendsViewModel by activityViewModels { getViewModelFactory() }
    private lateinit var binding: FriendDetailFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FriendDetailFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.profileAvatar.setImageResource(resources.getIdentifier(
            viewModel.selectedFriend.value!!.selectedAvatar, "drawable",
            "com.example.piraterun"))
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.friend_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.friend_menu_remove -> {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.remove_friend))
                .setMessage(
                    requireContext().getString(
                        R.string.remove_friend_text,
                        viewModel.selectedFriend.value!!.username
                    )
                )
                .setNegativeButton(requireContext().getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(requireContext().getString(R.string.yes)) { dialog, _ ->
                    // remove friend from friend list
                    viewModel.removeFriend()
                    Toast.makeText(
                        requireContext(),
                        "${viewModel.selectedFriend.value!!.username} removed!",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                    findNavController().popBackStack()
                }
                .show()
            true
        }
        else -> false
    }
}