package com.example.piraterun.ui.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.piraterun.R
import com.example.piraterun.data.entities.Rank
import com.example.piraterun.databinding.RankItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RankAdapter(private val viewModel: ShopViewModel) :
    RecyclerView.Adapter<RankAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = viewModel.ranks[position]

        holder.bind(viewModel, item)
    }

    override fun getItemCount(): Int {
        return viewModel.ranks.size
    }

    class ViewHolder(private val binding: RankItemBinding, private val viewModel: ShopViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // set on click listener for rank items
            binding.rankItem.setOnClickListener {
                if (viewModel.loggedInUser.value!!.ranks.contains(binding.rank!!.rank)) {
                    Toast.makeText(
                        it.context,
                        "Already bought!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                MaterialAlertDialogBuilder(it.context)
                    .setTitle(it.context.getString(R.string.buy_box_title))
                    .setMessage(it.context.getString(R.string.buy_box_rank_text, binding.rank!!.rank, binding.rank!!.price))
                    .setNegativeButton(it.context.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(it.context.getString(R.string.buy)) { dialog, _ ->
                        val price = binding.rank!!.price
                        val rank = binding.rank!!.rank

                        if (viewModel.buyRank(price, rank)) {
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

        fun bind(viewModel: ShopViewModel, item: Rank) {
            binding.viewmodel = viewModel
            binding.rank = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, viewModel: ShopViewModel): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RankItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, viewModel)
            }
        }
    }
}