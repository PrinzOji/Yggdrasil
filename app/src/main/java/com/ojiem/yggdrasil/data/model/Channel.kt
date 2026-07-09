package com.ojiem.yggdrasil.data.model

data class Channel(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val description: String = "",
    val profilePicUrl: String? = null,
    val followerCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class ChannelUpdate(
    val id: String = "",
    val channelId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val reactions: Map<String, Int> = emptyMap()
)
