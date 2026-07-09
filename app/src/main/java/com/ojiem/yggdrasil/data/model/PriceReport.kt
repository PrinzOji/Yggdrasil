package com.ojiem.yggdrasil.data.model

data class PriceReport(
    val id: String = "",
    val itemName: String = "",
    val category: String = "",
    val priceKes: Double = 0.0,
    val unit: String = "",
    val marketName: String = "",
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val photoUrl: String? = null,
    val reporterUid: String = "",
    val reporterName: String = "",
    val vouchCount: Int = 0,
    val bloomed: Boolean = false,
    val likes: List<String> = emptyList(),
    val commentCount: Int = 0,
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    companion object {
        const val BLOOM_THRESHOLD = 3
    }
}

data class Comment(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfilePic: String? = null,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Vouch(
    val reportId: String = "",
    val voucherUid: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class Category(
    val id: String = "",
    val label: String = "",
    val emoji: String = "🌱"
)

val defaultCategories = listOf(
    Category("staples", "Staples & Grains", "🌾"),
    Category("produce", "Fresh Produce", "🥬"),
    Category("dairy", "Dairy & Eggs", "🥚"),
    Category("meat", "Meat & Fish", "🍖"),
    Category("fruits", "Fruits", "🍎"),
    Category("assortment", "Assortment", "🍭"),
    Category("household", "Household", "🧺"),
)
