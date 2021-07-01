package com.example.piraterun.ui.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.piraterun.FriendsActivity
import com.example.piraterun.R
import com.example.piraterun.data.entities.User
import com.example.piraterun.databinding.AddFriendItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddFriendAdapter(private val viewModel: FriendsViewModel) :
    RecyclerView.Adapter<AddFriendAdapter.ViewHolder>(), Filterable {
    private var searchResult = ArrayList<User>(viewModel.allUsers.value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = searchResult[position]

        holder.bind(viewModel, item)
    }

    override fun getItemCount(): Int = searchResult.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchTerm = constraint.toString()
                searchResult = if (searchTerm.isEmpty()) {
                    (viewModel.allUsers.value as ArrayList<User>?)!!
                } else {
                    val resultList = viewModel.allUsers.value!!.filter {
                        it.username.contains(searchTerm, true)
                    }
                    resultList as ArrayList<User>
                }
                val filterResult = FilterResults()
                filterResult.values = searchResult
                return filterResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                searchResult = results?.values as ArrayList<User>
                notifyDataSetChanged()
            }

        }
    }

    class ViewHolder(
        private val binding: AddFriendItemBinding,
        private val viewModel: FriendsViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.friendItem.setOnClickListener {
                if (viewModel.friends.value!!.contains(binding.friend!!)) {
                    Toast.makeText(it.context, "Already in your friend list!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                MaterialAlertDialogBuilder(it.context)
                    .setTitle(it.context.getString(R.string.friend_box_title))
                    .setMessage(it.context.getString(R.string.friend_box_text, binding.friend!!.username))
                    .setNegativeButton(it.context.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(it.context.getString(R.string.add_friend)) { dialog, _ ->
                        // mocked friend request is sent
                        viewModel.addFriend(binding.friend!!)
                        Toast.makeText(it.context, "Added ${binding.friend!!.username} to your friends!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        fun bind(viewModel: FriendsViewModel, item: User) {
            binding.viewmodel = viewModel
            binding.friend = item
            binding.avatarImage.setImageResource(
                FriendsActivity.RESOURCES.getIdentifier(
                item.selectedAvatar, "drawable",
                "com.example.piraterun"))
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, viewModel: FriendsViewModel): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AddFriendItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, viewModel)
            }
        }
    }
}