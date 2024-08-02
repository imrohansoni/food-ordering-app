package com.project.foodorderingapp.adapters


import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.foodorderingapp.databinding.CategoryBinding
import com.project.foodorderingapp.models.Category

class CategoriesAdapter(
    private val categories: List<Category>, private val clickListener: (categoryName: Int) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {
    private lateinit var binding: CategoryBinding

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(category: Category, clickListener: (categoryId: Int) -> Unit) {
//            Glide.with(itemView.context).load(category.imageUrl).into(binding.categoryImage)
            binding.categoryName.text = category.categoryName

            val categoryImage = itemView.context.assets.open(category.imageUrl)
            val d = Drawable.createFromStream(categoryImage, null)
            binding.productImage.setImageDrawable(d)

            binding.categoryConstraintLayout.setOnClickListener {
                clickListener.invoke(category.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = CategoryBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], clickListener)
    }
}