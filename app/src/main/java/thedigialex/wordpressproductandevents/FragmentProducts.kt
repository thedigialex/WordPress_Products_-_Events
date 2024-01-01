package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class FragmentProducts(private val headerController: HeaderController, private val  cart: MutableList<Product>) : Fragment() {
    private var rootView: View? = null
    private var slotHolder: LinearLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_products, container, false)
        fetchProducts()
        return rootView
    }
    override fun onResume() {
        super.onResume()
        headerController.updateActivityTitle("Products")
    }
    private fun createView(product: Product) {
        val inflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.product_slot, slotHolder, false)
        val productNameTextView: TextView = view.findViewById(R.id.productName)
        productNameTextView.text = product.name
        val productPriceTextView: TextView = view.findViewById(R.id.productPrice)
        productPriceTextView.text = product.price.toString()

        val addToCartButton = view.findViewById<Button>(R.id.addToCartButton)
        addToCartButton.setOnClickListener {
            addProductToCart(product)
            headerController.updateActivityTitle("Products")
        }

        val productImage: ImageView = view.findViewById(R.id.productImage)
        Glide.with(view.context)
            .load(product.imageUrl)
            .into(productImage)
        slotHolder?.addView(view)
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchProducts() {
        slotHolder = rootView?.findViewById(R.id.SlotHolder)
        val progressBar: ProgressBar = rootView!!.findViewById(R.id.progressBar)
        val woocommerceService = WooCommerceService(requireContext(), getString(R.string.consumerKey), getString(R.string.consumerSecret))
        GlobalScope.launch(Dispatchers.Main) {
            val productsJson = withContext(Dispatchers.IO) {
                woocommerceService.getProducts()
            }
            val jsonArray = JSONArray(productsJson)
            val productList = mutableListOf<Product>()

            for (i in 0 until jsonArray.length()) {
                val productObject: JSONObject = jsonArray.getJSONObject(i)
                Log.d("ProductJSON", productObject.toString())
                val id = productObject.getInt("id")
                val name = productObject.getString("name")
                val permalink = productObject.getString("permalink")
                val priceString = productObject.getString("price")
                val price = priceString.toDoubleOrNull() ?: 0.0
                val imageUrl = productObject.getJSONArray("images").getJSONObject(0).getString("src")
                val stockStatus = productObject.getString("stock_status")
                val type = productObject.getString("type")
                val product = Product(id, name, permalink, price, imageUrl, stockStatus, type)
                productList.add(product)
                if(product.stockStatus == "instock"){
                    createView(product)
                }
            }
            progressBar.visibility = View.GONE
        }
    }
    private fun addProductToCart(newProduct: Product) {
        val existingProduct = cart.find { it.id == newProduct.id }
        if (existingProduct != null) {
            existingProduct.quantity++
        } else {
            cart.add(newProduct)
        }
    }
}