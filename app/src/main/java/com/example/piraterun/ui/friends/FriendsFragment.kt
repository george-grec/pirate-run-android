package com.example.piraterun.ui.friends

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piraterun.R
import com.example.piraterun.databinding.FriendsFragmentBinding
import com.example.piraterun.util.getViewModelFactory

class FriendsFragment : Fragment() {
    private val viewModel: FriendsViewModel by activityViewModels { getViewModelFactory() }
    private lateinit var binding: FriendsFragmentBinding
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var friendAdapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FriendsFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        binding.lifecycleOwner = viewLifecycleOwner

        // setup recycler view
        friendsRecyclerView = binding.friendList
        friendsRecyclerView.layoutManager = LinearLayoutManager(context)
        friendAdapter = FriendAdapter(viewModel)
        friendsRecyclerView.adapter = FriendAdapter(viewModel)

        if (friendAdapter.itemCount == 0) {
            friendsRecyclerView.visibility = View.GONE
            binding.addFriendsButton.visibility = View.VISIBLE
            binding.friendsTextView.visibility = View.VISIBLE
        } else {
            binding.addFriendsButton.visibility = View.GONE
            binding.friendsTextView.visibility = View.GONE
            friendsRecyclerView.visibility = View.VISIBLE
        }

        setHasOptionsMenu(true)

        // setup back button to game menu
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.friends.observe(viewLifecycleOwner, {
            friendAdapter.notifyDataSetChanged()
        })
        binding.addFriendsButton.setOnClickListener {
            val action = FriendsFragmentDirections.actionFriendsFragmentToAddFriendsFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.friends_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.friend_menu_add -> {
            val action = FriendsFragmentDirections.actionFriendsFragmentToAddFriendsFragment()
            findNavController().navigate(action)
            true
        }
        else -> false
    }
}