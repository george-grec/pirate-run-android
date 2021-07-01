package com.example.piraterun.ui.shop

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piraterun.data.entities.User
import com.example.piraterun.databinding.ShopFragmentBinding
import com.example.piraterun.util.getViewModelFactory

class ShopFragment : Fragment() {

    companion object {
        fun newInstance() = ShopFragment()
        lateinit var RESOURCES: Resources
    }

    private val viewModel: ShopViewModel by viewModels { getViewModelFactory() }
    private lateinit var binding: ShopFragmentBinding
    private lateinit var rankRecyclerView: RecyclerView
    private lateinit var avatarRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RESOURCES = resources
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // setup binding
        binding = ShopFragmentBinding.inflate(
            inflater, container, false).apply {
            viewmodel = viewModel
        }
        binding.lifecycleOwner = viewLifecycleOwner

        // setup recycler view
        rankRecyclerView = binding.rankList
        rankRecyclerView.layoutManager = LinearLayoutManager(context)
        rankRecyclerView.adapter = RankAdapter(viewModel)
        avatarRecyclerView = binding.avatarList
        avatarRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        avatarRecyclerView.adapter = AvatarAdapter(viewModel)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup observer
        val coinObserver = Observer<User?> {
            binding.coins.text = String.format("%d", it.coins)
        }
        viewModel.loggedInUser.observe(viewLifecycleOwner, coinObserver)
    }

}