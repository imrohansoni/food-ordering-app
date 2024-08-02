package com.project.foodorderingapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.project.foodorderingapp.database.FoodbiteDatabase
import com.project.foodorderingapp.databinding.FragmentAccountBinding
import com.project.foodorderingapp.ui.activities.AboutActivity
import com.project.foodorderingapp.ui.activities.CartActivity
import com.project.foodorderingapp.ui.activities.LoginActivity
import com.project.foodorderingapp.ui.activities.OrderActivity
import com.project.foodorderingapp.ui.activities.PrivacyPolicyActivity
import com.project.foodorderingapp.ui.activities.UpdateEmailActivity
import com.project.foodorderingapp.ui.activities.UpdateNameActivity
import com.project.foodorderingapp.utils.AuthTokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Account : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.editNameButton.setOnClickListener {
            Intent(requireContext(), UpdateNameActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.yourEmailCardView.setOnClickListener {
            Intent(context, UpdateEmailActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.privacyPolicyContainer.setOnClickListener {
            Intent(context, PrivacyPolicyActivity::class.java).apply {
                startActivity(this)
            }
        }


        binding.aboutCardView.setOnClickListener {
            Intent(requireContext(), AboutActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.ordersCardView.setOnClickListener {
            Intent(requireContext(), OrderActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.cartCardView.setOnClickListener {
            Intent(requireContext(), CartActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.logoutCardView.setOnClickListener {
            AuthTokenManager.removeAuthToken(requireContext())
            Intent(requireContext(), LoginActivity::class.java).apply {
                startActivity(this)
            }
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            val user = FoodbiteDatabase.getDatabase(requireContext()).userDao().getUser()[0]
            if (user.firstName != null && user.lastName != null) {
                withContext(Dispatchers.Main) {
                    binding.username.visibility = View.VISIBLE
                    binding.username.text = "${user.firstName} ${user.lastName}"
                }
            }

            if (user.email != null) {
                withContext(Dispatchers.Main) {
                    binding.email.visibility = View.VISIBLE
                    binding.email.text = user.email.toString()
                }
            }

            withContext(Dispatchers.Main) {
                binding.mobileNumber.text = user.mobileNumber
            }
        }
    }
}