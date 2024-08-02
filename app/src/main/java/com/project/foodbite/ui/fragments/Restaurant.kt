package com.project.foodorderingapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.foodorderingapp.R
import com.project.foodorderingapp.databinding.FragmentRestaurantBinding

class Restaurant : Fragment() {
    private lateinit var binding: FragmentRestaurantBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.setTheme(R.style.DarkStatusBar)
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }
}