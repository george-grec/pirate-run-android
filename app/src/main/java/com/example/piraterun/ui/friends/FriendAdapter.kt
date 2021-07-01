package com.example.piraterun.ui.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.piraterun.FriendsActivity
import com.example.piraterun.data.entities.User
import com.example.piraterun.databinding.FriendItemBinding

class FriendAdapter(private val viewModel: FriendsViewModel) :
    RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = viewModel.friends.value!![position]

        holder.bind(viewModel, item)

        // on click listener for friend item, navigate to detail fragment
        holder.itemView.setOnClickListener {
            viewModel.selectFriend(item)
            val action = FriendsFragmentDirections.actionFriendsFragmentToFriendDetails()
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return viewModel.friends.value?.size!!
    }

    class ViewHolder(
        private val binding: FriendItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: FriendsViewModel, item: User) {
            binding.viewmodel = viewModel
            binding.friend = item
            binding.avatarImage.setImageResource(FriendsActivity.RESOURCES.getIdentifier(
                item.selectedAvatar, "drawable",
                "com.example.piraterun"))
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FriendItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}