package com.ojiem.yggdrasil.data.model

data class Status(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfilePic: String? = null,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val musicUrl: String? = null,
    val musicTitle: String? = null,
    val textContent: String? = null,
    val backgroundColor: Long = 0xFF2E7D32, // Default green
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // 24 hours
    val viewers: List<String> = emptyList(),
    val audience: String = "EVERYONE", // EVERYONE, BRANCH_MEMBERS, SELECTIVE
    val allowedViewers: List<String> = emptyList()
)
