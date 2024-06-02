package com.project.foodorderingapp.adapters


import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.foodorderingapp.databinding.OrderItemBinding
import com.project.foodorderingapp.models.Order

class OrderAdapter(
    private val orders: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private lateinit var binding: OrderItemBinding

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(order: Order) {
//            Glide.with(itemView.context).load(category.imageUrl).into(binding.categoryImage)
            binding.productName.text = order.name
            val productImage = itemView.context.assets.open(order.image)
            val d = Drawable.createFromStream(productImage, null)
            binding.productImage.setImageDrawable(d)
            binding.productPrice.text = "₹ ${order.price}"
            binding.productQuantity.text = order.quantity.toString()

            binding.productRating.text = order.rating.toString()
            binding.productDiscount.text = order.discount

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = OrderItemBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }
}