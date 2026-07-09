package com.ojiem.yggdrasil.data.model

enum class LibraryItemType {
    VIDEO, BOOK, TIP
}

data class LibraryItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: LibraryItemType = LibraryItemType.TIP,
    val url: String = "", // Link to video or book
    val author: String = "",
    val topic: String = "",
    val thumbnail: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)
