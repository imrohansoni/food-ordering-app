package com.project.foodorderingapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.project.foodorderingapp.R
import com.project.foodorderingapp.adapters.CategoriesAdapter
import com.project.foodorderingapp.adapters.ImageSliderAdapter
import com.project.foodorderingapp.databinding.FragmentHomeBinding
import com.project.foodorderingapp.models.Category
import com.project.foodorderingapp.models.Product
import com.project.foodorderingapp.models.Products
import com.project.foodorderingapp.ui.activities.ProductActivity
import com.project.foodorderingapp.utils.loadJsonFile
import java.util.Timer
import java.util.TimerTask

class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val images = listOf(
            R.drawable.banner_first,
            R.drawable.banner_second,
            R.drawable.banner_third
        )

        val adapter = ImageSliderAdapter(images)
        binding.viewPager.adapter = adapter
        startAutoSlide()

        val categories = loadJsonFile<Category>(requireContext(), fileName = "categories.json")
        val products = loadJsonFile<Product>(requireContext(), fileName = "products.json")

        binding.categoriesList.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.categoriesList.adapter = CategoriesAdapter(categories) {
            val filteredProducts = products.filter { product -> product.categoryId == it }
            Intent(requireContext(), ProductActivity::class.java).apply {
                putExtra("PRODUCT_LIST", Products(filteredProducts))
                startActivity(this)
            }
        }

//        binding.restaurantOne.setOnClickListener {
//            val filteredProducts = products.subList(0, 3)
////            val filteredProducts = products.filter { product -> product.categoryId == it }
//            Intent(requireContext(), ProductActivity::class.java).apply {
//                putExtra("PRODUCT_LIST", Products(filteredProducts))
//                startActivity(this)
//            }
//        }

//        binding.restaurantTwo.setOnClickListener {
//            val filteredProducts = products.subList(0, 6)
//
////            val filteredProducts = products.filter { product -> product.categoryId == it }
//            Intent(requireContext(), ProductActivity::class.java).apply {
//                putExtra("PRODUCT_LIST", Products(filteredProducts))
//                startActivity(this)
//            }
//        }
        return binding.root
    }

    private fun startAutoSlide() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    binding.viewPager.currentItem = currentPage
                    currentPage++
                    if (currentPage == 3) {
                        currentPage = 0
                    }
                }
            }
        }, 2000, 3000)
    }
}