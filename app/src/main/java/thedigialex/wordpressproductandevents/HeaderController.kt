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
        var cartTotal = 0.0
        for (product in cart) {
            cartTotal += product.price * product.quantity
            cartCount += product.quantity
        }
        cartItemCount.apply {
            visibility = if (cartCount <= 0) View.GONE else View.VISIBLE
            text = if (cartCount <= 0) "" else cartCount.toString()
        }
        val totalString = "$${String.format("%.2f", cartTotal)}"
        cartTextView.text = totalString
        cartButton.setOnClickListener {
            displayCartView()
        }
    }
    private fun displayCartView(){
        cartLayout.visibility = if (cartLayout.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        if (cartLayout.visibility == View.VISIBLE) {
            for (product in cart) {
                createView(product)
            }
        } else {
            slotHolder.removeAllViews()
        }
    }

    private fun createView(product: Product) {
        val inflater = LayoutInflater.from(context)

        val view: View = inflater.inflate(R.layout.product_slot, slotHolder, false)
        val productNameTextView: TextView = view.findViewById(R.id.productName)
        productNameTextView.text = product.name
        val productPriceTextView: TextView = view.findViewById(R.id.productPrice)
        productPriceTextView.text = "Qty: " + product.quantity.toString()
        val addToCartButton = view.findViewById<Button>(R.id.addToCartButton)
        addToCartButton.visibility = View.GONE
        val removeFromCartButton = view.findViewById<Button>(R.id.removeFromCartButton)
        removeFromCartButton.visibility = View.VISIBLE
        removeFromCartButton.setOnClickListener {
            removeProductFromCart(product)

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
            1 // 1dp height for the divider, you can adjust it as needed
        )
        layoutParams.setMargins(0, 8, 0, 8) // Add margins if needed (left, top, right, bottom)
        divider.layoutParams = layoutParams

        slotHolder?.addView(divider)
    }
    private fun removeProductFromCart(product: Product){
        val productToRemove = cart.find { it.id == product.id }
        if (productToRemove != null) {
            cart.remove(productToRemove)
            updateActivityTitle(currentTitle)
            displayCartView()
            displayCartView()
        }
    }
}
