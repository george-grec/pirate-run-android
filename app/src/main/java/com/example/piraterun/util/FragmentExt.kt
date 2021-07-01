package com.example.piraterun.util

import androidx.fragment.app.Fragment
import com.example.piraterun.PirateApplication
import com.example.piraterun.ViewModelFactory

/**
 * Extension function for Fragment.
 */
fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as PirateApplication).userRepository
    return ViewModelFactory(repository, this)
}