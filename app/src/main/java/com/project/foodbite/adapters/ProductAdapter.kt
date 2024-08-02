package com.project.foodorderingapp.adapters


import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.foodorderingapp.databinding.ProductBinding
import com.project.foodorderingapp.models.Product
import com.project.foodorderingapp.models.Products

class ProductAdapter(
    private val products: Products, private val clickListener: (product: Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private lateinit var binding: ProductBinding

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(product: Product, clickListener: (product: Product) -> Unit) {
//            Glide.with(itemView.context).load(category.imageUrl).into(binding.categoryImage)
            binding.productName.text = product.name
            val productImage = itemView.context.assets.open(product.image)
            val d = Drawable.createFromStream(productImage, null)
            binding.productImage.setImageDrawable(d)
            binding.productPrice.text = "₹ ${product.price}"

            binding.productRating.text = product.rating.toString()
            binding.deliveryTime.text = "${product.deliveryTime} min"
            binding.productDiscount.text = product.discount
            binding.productDescription.text = product.description

            binding.addCartButton.setOnClickListener {
                clickListener(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ProductBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return products.products.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products.products[position], clickListener)
    }
}