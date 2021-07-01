package com.example.piraterun.ui.treasure_hunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.piraterun.TreasureHuntMapActivity
import com.example.piraterun.databinding.TreasureHuntSelectFragmentBinding


class TreasureHuntSelectFragment : Fragment() {

    private lateinit var binding: TreasureHuntSelectFragmentBinding






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // setup binding
        binding = TreasureHuntSelectFragmentBinding.inflate(
            inflater, container, false
        )
        binding.textViewKm.text = "1.0"



        binding.radiusSelector.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.textViewKm.text = (progress/2.0).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.startButton.setOnClickListener {

            val startIntent = Intent(activity, TreasureHuntMapActivity::class.java)
            startIntent.putExtra("RADIUS", binding.textViewKm.text.toString().toDouble())

            startActivity(startIntent)
        }


        return binding.root
    }



}