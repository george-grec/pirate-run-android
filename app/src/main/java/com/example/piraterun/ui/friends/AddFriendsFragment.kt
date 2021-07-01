package com.example.piraterun.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piraterun.databinding.AddFriendsFragmentBinding
import com.example.piraterun.util.getViewModelFactory

class AddFriendsFragment : Fragment() {
    private val viewModel: FriendsViewModel by activityViewModels { getViewModelFactory() }
    private lateinit var binding: AddFriendsFragmentBinding
    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendAdapter: AddFriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddFriendsFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)

        // setup recycler view
        friendRecyclerView = binding.addFriendList
        friendRecyclerView.layoutManager = LinearLayoutManager(context)
        friendAdapter = AddFriendAdapter(viewModel)
        friendRecyclerView.adapter = friendAdapter

        performSearch()
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    private fun performSearch() {
        binding.addFriendSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                friendAdapter.filter.filter(newText)
                return false
            }
        })
    }
}