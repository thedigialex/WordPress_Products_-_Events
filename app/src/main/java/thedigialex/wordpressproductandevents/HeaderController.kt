package thedigialex.wordpressproductandevents

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide

class HeaderController(rootView: View, private val cartLayout: View, private val cart: MutableList<Product>, private val context: Context) {

    private val activityTitleTextView: TextView = rootView.findViewById(R.id.activityTitle)
    private val cartItemCount: TextView = rootView.findViewById(R.id.cartItemCount)
    private val cartButton: Button = rootView.findViewById(R.id.cartButton)
    private val cartTextView: TextView = cartLayout.findViewById(R.id.cartTotalView)
    private val slotHolder: LinearLayout = cartLayout.findViewById(R.id.cartSlotHolder)
    private var currentTitle: String = ""

    fun updateActivityTitle(newTitle: String) {
        currentTitle = newTitle
        activityTitleTextView.text = currentTitle
        var cartCount = 0
        for (product in cart) {
            cartCount += product.quantity
        }
        cartItemCount.apply {
            visibility = if (cartCount <= 0) View.GONE else View.VISIBLE
            text = if (cartCount <= 0) "" else cartCount.toString()
        }
        cartButton.setOnClickListener {
            displayCartView(true)
        }
    }
    private fun displayCartView(toggle: Boolean){
        slotHolder.removeAllViews()
        if(toggle){
            cartLayout.visibility = if (cartLayout.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        }
        if (cartLayout.visibility == View.VISIBLE) {
            var cartTotal = 0.0
            for (product in cart) {
                cartTotal += product.price * product.quantity
                createView(product)
            }
            val totalString = "$${String.format("%.2f", cartTotal)}"
            cartTextView.text = totalString
        }
    }

    private fun createView(product: Product) {
        val inflater = LayoutInflater.from(context)

        val view: View = inflater.inflate(R.layout.product_slot, slotHolder, false)
        val productNameTextView: TextView = view.findViewById(R.id.productName)
        productNameTextView.text = product.name
        val productPriceTextView: TextView = view.findViewById(R.id.productPrice)
        productPriceTextView.text = "Qty: " + product.quantity.toString()
        val removeFromCartButton = view.findViewById<Button>(R.id.removeFromCartButton)
        removeFromCartButton.visibility = View.VISIBLE
        removeFromCartButton.setOnClickListener {
            removeProductFromCart(product)
        }
        val addFromCartButton = view.findViewById<Button>(R.id.addFromCartButton)
        addFromCartButton.setOnClickListener {
            addProductFromCart(product)
        }
        val productImage: ImageView = view.findViewById(R.id.productImage)
        Glide.with(view.context)
            .load(product.imageUrl)
            .into(productImage)
        slotHolder.addView(view)
        addDividerToSlotHolder()
    }

    private fun addDividerToSlotHolder() {
        val divider = View(context)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        )
        layoutParams.setMargins(0, 8, 0, 8)
        divider.layoutParams = layoutParams
        slotHolder.addView(divider)
    }
    private fun removeProductFromCart(product: Product){
        val productToRemove = cart.find { it.id == product.id }
        if (productToRemove != null) {
            productToRemove.quantity--
            if(productToRemove.quantity <= 0){
                productToRemove.quantity = 1
                cart.remove(productToRemove)
            }
            updateActivityTitle(currentTitle)
            displayCartView(false)
        }
    }
    private fun addProductFromCart(product: Product){
        product.quantity++
        updateActivityTitle(currentTitle)
        displayCartView(false)
    }
}