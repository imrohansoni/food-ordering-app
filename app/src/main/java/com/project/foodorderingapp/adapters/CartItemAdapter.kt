package com.project.foodorderingapp.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.foodorderingapp.databinding.CartItemBinding
import com.project.foodorderingapp.models.Cart

class CartItemAdapter(
    private var carts: MutableList<Cart>,
    private val addToCart: (cart: Cart, sum: Double) -> Unit,
    private val removeFromCart: (cart: Cart, sum: Double) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            cart: Cart,
            addToCart: (cart: Cart, sum: Double) -> Unit,
            removeFromCart: (cart: Cart, sum: Double) -> Unit,
            position: Int
        ) {
            binding.productName.text = cart.name
            val productImage = itemView.context.assets.open(cart.image)
            val d = Drawable.createFromStream(productImage, null)
            binding.productImage.setImageDrawable(d)
            binding.productPrice.text = "₹ ${cart.price}"
            binding.productRating.text = cart.rating.toString()
            binding.productDiscount.text = cart.discount
            binding.cartItemQuantity.text = cart.quantity.toString()

            binding.addCartButton.setOnClickListener {
                cart.quantity++
                notifyItemChanged(position)
                var sum = 0.0
                carts.forEach {
                    sum += it.quantity * it.price
                }
                addToCart(cart, sum)

            }

            binding.removeCartButton.setOnClickListener {

                cart.quantity--
                if (cart.quantity == 0) {
                    removeCartItem(cart)
                }
                notifyItemChanged(position)

                var sum = 0.0
                carts.forEach {
                    sum += it.quantity * it.price
                }
                removeFromCart(cart, sum)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartItemBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(carts[position], addToCart, removeFromCart, position)
    }

    fun removeCartItem(cart: Cart) {
        val index = carts.indexOfFirst { it.id == cart.id }
        if (index != -1) {
            carts.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
