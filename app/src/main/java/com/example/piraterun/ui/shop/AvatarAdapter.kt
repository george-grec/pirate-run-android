package com.example.piraterun.ui.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.piraterun.R
import com.example.piraterun.data.entities.Avatar
import com.example.piraterun.databinding.AvatarItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AvatarAdapter(private val viewModel: ShopViewModel) :
    RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = viewModel.avatars[position]

        holder.bind(viewModel, item)
    }

    override fun getItemCount(): Int {
        return viewModel.avatars.size
    }

    class ViewHolder(private val binding: AvatarItemBinding, private val viewModel: ShopViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // set on click listener for avatar items
            binding.avatarItem.setOnClickListener {
                if (viewModel.loggedInUser.value!!.avatars.contains(binding.avatar!!.resourceName)) {
                    Toast.makeText(
                        it.context,
                        "Already bought!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                MaterialAlertDialogBuilder(it.context)
                    .setTitle(it.context.getString(R.string.buy_box_title))
                    .setMessage(it.context.getString(R.string.buy_box_avatar_text, binding.avatar!!.name, binding.avatar!!.price))
                    .setNegativeButton(it.context.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(it.context.getString(R.string.buy)) { dialog, _ ->
                        val price = binding.avatar!!.price
                        val avatar = binding.avatar!!.resourceName

                        if (viewModel.buyAvatar(price, avatar)) {
                            Toast.makeText(
                                it.context,
                                "New Balance: ${viewModel.loggedInUser.value?.coins} coins!",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                it.context,
                                "Not enough coins!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    .show()
            }
        }

        fun bind(viewModel: ShopViewModel, item: Avatar) {
            binding.viewmodel = viewModel
            binding.avatar = item
            binding.avatarImg.setImageResource(item.uri)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, viewModel: ShopViewModel): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AvatarItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, viewModel)
            }
        }
    }
}