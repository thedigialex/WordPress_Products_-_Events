package thedigialex.wordpressproductandevents

import okhttp3.OkHttpClient
import okhttp3.Request

class WooCommerceService(private val consumerKey: String, private val consumerSecret: String) {

    private val client = OkHttpClient()
    fun getProducts(): String {
        val request = Request.Builder()
            .url("https://honey.thedigialex.net/wp-json/wc/v3/products")
            .header("Authorization", "Basic " + android.util.Base64.encodeToString("$consumerKey:$consumerSecret".toByteArray(), android.util.Base64.NO_WRAP))
            .build()
        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}