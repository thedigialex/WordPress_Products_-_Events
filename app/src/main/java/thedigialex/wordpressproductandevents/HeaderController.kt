package thedigialex.wordpressproductandevents

import android.view.View
import android.widget.Button
import android.widget.TextView

class HeaderController(rootView: View, private val cartLayout: View, private val cart: MutableList<Product>) {

    private val activityTitleTextView: TextView = rootView.findViewById(R.id.activityTitle)
    private val cartItemCount: TextView = rootView.findViewById(R.id.cartItemCount)
    private val cartButton: Button = rootView.findViewById(R.id.cartButton)
    private val cartTextView: TextView = cartLayout.findViewById(R.id.cartTotalView)

    fun updateActivityTitle(newTitle: String) {
        activityTitleTextView.text = newTitle
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
    }
}
