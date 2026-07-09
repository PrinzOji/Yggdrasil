package com.ojiem.yggdrasil.data.model

data class UserActivity(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // VIEW_POST, VIEW_BOOK, VIEW_VIDEO, LIKE, COMMENT, CREATE_REPORT
    val targetId: String = "",
    val targetTitle: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
