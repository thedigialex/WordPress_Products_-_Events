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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
        productPriceTextView.text = "$" + product.price.toString()
        val productRating: TextView = view.findViewById(R.id.productRating)
        productRating.text = product.averageRating.toString()

        val extraDetailsText: TextView = view.findViewById(R.id.extraDetails)
        extraDetailsText.text = product.shortDescription + "\n" + product.permalink
        val topProductLayout: LinearLayout = view.findViewById(R.id.topProductLayout)

        val addToCartButton = view.findViewById<Button>(R.id.addFromCartButton)
        addToCartButton.setOnClickListener {
            addProductToCart(product)
            headerController.updateActivityTitle("Products")
        }
        val showExtraDetailsButton = view.findViewById<Button>(R.id.showExtraDetails)
        showExtraDetailsButton.setOnClickListener {
            topProductLayout.visibility = if (topProductLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            extraDetailsText.visibility = if (extraDetailsText.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        val density = view.context.resources.displayMetrics.density
        val radiusInPixels = (10 * density).toInt()
        val productImage: ImageView = view.findViewById(R.id.productImage)
        Glide.with(view.context)
            .load(product.imageUrl)
            .transform(RoundedCorners(radiusInPixels))
            .into(productImage)
        slotHolder?.addView(view)
        val divider = View(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        )
        layoutParams.setMargins(0, 16, 0, 16)
        divider.layoutParams = layoutParams
        slotHolder?.addView(divider)
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
                val shortDescription = productObject.getString("short_description")
                val averageRatingString = productObject.getString("average_rating")
                val averageRating = averageRatingString.toDoubleOrNull() ?: 0.0

                val categoriesArray = productObject.getJSONArray("categories")
                val categoriesList = mutableListOf<Product.Category>()
                for (j in 0 until categoriesArray.length()) {
                    val categoryObject = categoriesArray.getJSONObject(j)
                    val categoryId = categoryObject.getInt("id")
                    val categoryName = categoryObject.getString("name")
                    val categorySlug = categoryObject.getString("slug")
                    val category = Product.Category(categoryId, categoryName, categorySlug)
                    categoriesList.add(category)
                }

                val priceString = productObject.getString("price")
                val price = priceString.toDoubleOrNull() ?: 0.0
                val imageUrl = productObject.getJSONArray("images").getJSONObject(0).getString("src")
                val stockStatus = productObject.getString("stock_status")
                val type = productObject.getString("type")
                val product = Product(id, name, permalink, shortDescription, averageRating, categoriesList, price, imageUrl, stockStatus, type)
                productList.add(product)

                if (product.stockStatus == "instock") {
                    // Do something with in-stock products
                }

                createView(product)

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