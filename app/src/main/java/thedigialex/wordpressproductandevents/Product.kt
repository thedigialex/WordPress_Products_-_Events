package thedigialex.wordpressproductandevents

data class Product(
    val id: Int,
    val name: String,
    val permalink: String,
    var price: Double,
    val imageUrl: String,
    val stockStatus: String,
    //val attributes: List<Attribute>,
    //val variations: List<Int>,
    val type: String,
    var quantity: Int = 1
)