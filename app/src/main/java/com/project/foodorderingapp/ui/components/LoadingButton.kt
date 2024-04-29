package com.project.foodorderingapp.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.project.foodorderingapp.R
import com.project.foodorderingapp.databinding.LoadingButtonBinding

class LoadingButton(context: Context, attr: AttributeSet) :
    LinearLayout(context, attr) {
    private var buttonText: String
    private var binding: LoadingButtonBinding =
        LoadingButtonBinding.inflate(LayoutInflater.from(context), this, true)

    var disabled: Boolean = false
        set(value) {
            binding.root.isEnabled = !value
            binding.root.isClickable = value
            binding.loadingButtonText.isEnabled = !value
            field = value
        }

    init {
        val attributes = context.obtainStyledAttributes(attr, R.styleable.LoadingButton)
        buttonText = attributes.getString(R.styleable.LoadingButton_text).toString()
        binding.loadingButtonText.text = buttonText

        attributes.recycle()
    }


    fun startLoading(loadingText: String) {
        binding.progressBar.visibility = VISIBLE
        binding.loadingButtonText.text = loadingText
        binding.loadingButtonText.isAllCaps = false
    }

    fun stopLoading() {
        binding.progressBar.visibility = GONE
        binding.loadingButtonText.text = buttonText
        binding.loadingButtonText.isAllCaps = true
    }
}