package com.ojiem.yggdrasil.data.model

data class User(
    val uid: String = "",
    val username: String = "",
    val fullName: String = "",
    val bio: String = "Eco-Agent at Yggdrasil",
    val email: String = "",
    val rootsBalance: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val branchesCount: Int = 0,
    val postCount: Int = 0,
    val profilePicUrl: String? = null,
    val currentNote: String? = null,
    val job: String = "",
    val talents: String = "",
    val portfolio: String = "",
    val skills: String = "",
    val qualifications: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val isPrivate: Boolean = false,
    val allowComments: Boolean = true
)
