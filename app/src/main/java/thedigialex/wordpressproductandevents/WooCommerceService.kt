package thedigialex.wordpressproductandevents

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request

class WooCommerceService(private val context: Context, private val consumerKey: String, private val consumerSecret: String) {

    private val client = OkHttpClient()
    fun getProducts(): String {
        val websiteUrl = context.getString(R.string.website_url)
        val fullUrl = "$websiteUrl/wp-json/wc/v3/products"
        val request = Request.Builder()
            .url(fullUrl)
            .header("Authorization", "Basic " + android.util.Base64.encodeToString("$consumerKey:$consumerSecret".toByteArray(), android.util.Base64.NO_WRAP))
            .build()
        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}