package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class FragmentProducts(private val headerController: HeaderController) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_products, container, false)
        fetchProducts(rootView)
        return rootView
    }
    override fun onResume() {
        super.onResume()
        headerController.updateActivityTitle("Products")
    }
    private fun createView(rootView: View, product: Product) {
        val slotHolder: LinearLayout = rootView.findViewById(R.id.SlotHolder)
        val inflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.product_slot, slotHolder, false)
        val productNameTextView: TextView = view.findViewById(R.id.productName)
        productNameTextView.text = product.name

        val productImage: ImageView = view.findViewById(R.id.productImage)
        Glide.with(view.context)
            .load(product.imageUrl)
            .into(productImage)
        slotHolder.addView(view)
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchProducts(rootView: View) {
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)
        val woocommerceService = WooCommerceService(getString(R.string.consumerKey), getString(R.string.consumerSecret))
        GlobalScope.launch(Dispatchers.Main) {
            val productsJson = withContext(Dispatchers.IO) {
                woocommerceService.getProducts()
            }
            val jsonArray = JSONArray(productsJson)
            val productList = mutableListOf<Product>()

            for (i in 0 until jsonArray.length()) {
                val productObject: JSONObject = jsonArray.getJSONObject(i)

                val productName = productObject.getString("name")
                val imageUrl = productObject.getJSONArray("images").getJSONObject(0).getString("src")
                val price = productObject.getString("price")
                val productUrl = productObject.getString("permalink")

                val product = Product(productName, imageUrl, price, productUrl)
                productList.add(product)
                createView(rootView, product)
            }
            progressBar.visibility = View.GONE
        }
    }
}