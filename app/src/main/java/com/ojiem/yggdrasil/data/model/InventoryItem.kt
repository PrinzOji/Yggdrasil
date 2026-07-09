package com.ojiem.yggdrasil.data.model

data class InventoryItem(
    val id: String = "",
    val itemName: String = "",
    val stockCount: Int = 0,
    val ordersCount: Int = 0,
    val expiryDate: String = "",
    val needByUsersCount: Int = 0,
    val unit: String = "pcs"
)
