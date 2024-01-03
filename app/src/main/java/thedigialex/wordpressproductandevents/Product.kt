package thedigialex.wordpressproductandevents

data class Product(
    val id: Int,
    val name: String,
    val permalink: String,
    val shortDescription: String,
    val averageRating: Double,
    val categories: List<Category>,
    var price: Double,
    val imageUrl: String,
    val stockStatus: String,
    val type: String,
    var quantity: Int = 1
) {
    data class Category(
        val id: Int,
        val name: String,
        val slug: String
    )
}
