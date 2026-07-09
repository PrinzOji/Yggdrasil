package com.ojiem.yggdrasil.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ojiem.yggdrasil.data.model.PriceReport
import com.ojiem.yggdrasil.data.model.Comment
import com.ojiem.yggdrasil.data.model.UserActivity
import com.ojiem.yggdrasil.data.model.User
import com.ojiem.yggdrasil.data.model.Status
import com.ojiem.yggdrasil.data.model.Channel
import com.ojiem.yggdrasil.data.model.ChannelUpdate
import com.ojiem.yggdrasil.data.model.LibraryItem
import com.ojiem.yggdrasil.data.model.LibraryItemType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseDatabase = Firebase.database

    // Auth
    fun getCurrentUser() = auth.currentUser
    fun isUserSignedIn() = auth.currentUser != null
    fun signOut() = auth.signOut()

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(email: String, password: String, username: String, fullName: String) {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("User ID is null")
        val userId = firebaseUser.uid
        
        val user = User(
            uid = userId,
            username = username,
            fullName = fullName,
            email = email,
            profilePicUrl = firebaseUser.photoUrl?.toString(),
            rootsBalance = 100
        )
        
        db.getReference("Users").child(userId).setValue(user).await()
    }

    // Reports
    suspend fun submitReport(report: PriceReport) {
        val reportId = if (report.id.isBlank()) {
            db.getReference("PriceReports").push().key ?: System.currentTimeMillis().toString()
        } else {
            report.id
        }
        val finalReport = report.copy(id = reportId)
        db.getReference("PriceReports").child(reportId).setValue(finalReport).await()
        
        // Increment post count for user
        val userId = report.reporterUid
        if (userId.isNotBlank()) {
            val userRef = db.getReference("Users").child(userId)
            val userSnap = userRef.get().await()
            val user = userSnap.getValue(User::class.java)
            if (user != null) {
                userRef.child("postCount").setValue(user.postCount + 1).await()
            }
        }
    }

    fun getReportsByUser(userId: String): Flow<List<PriceReport>> = callbackFlow {
        val ref = db.getReference("PriceReports").orderByChild("reporterUid").equalTo(userId)
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val reports = snapshot.children.mapNotNull { it.getValue(PriceReport::class.java) }
                trySend(reports.sortedByDescending { it.createdAtMillis })
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getReports(): Flow<List<PriceReport>> = callbackFlow {
        val ref = db.getReference("PriceReports")
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val reports = snapshot.children.mapNotNull { it.getValue(PriceReport::class.java) }
                trySend(reports.sortedByDescending { it.createdAtMillis })
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun getReportById(reportId: String): PriceReport? {
        val snapshot = db.getReference("PriceReports").child(reportId).get().await()
        return snapshot.getValue(PriceReport::class.java)
    }

    suspend fun vouchForReport(reportId: String, userId: String) {
        val reportRef = db.getReference("PriceReports").child(reportId)
        val snapshot = reportRef.get().await()
        val report = snapshot.getValue(PriceReport::class.java) ?: return
        
        // Basic vouch logic: increment vouchCount and check for bloom
        val newVouchCount = report.vouchCount + 1
        val newBloomed = newVouchCount >= PriceReport.BLOOM_THRESHOLD
        
        val updates = mapOf(
            "vouchCount" to newVouchCount,
            "bloomed" to newBloomed
        )
        
        reportRef.updateChildren(updates).await()

        // Reward the reporter with roots (contribution points)
        val reporterId = report.reporterUid
        if (reporterId.isNotBlank()) {
            val userRef = db.getReference("Users").child(reporterId)
            val userSnap = userRef.get().await()
            val user = userSnap.getValue(User::class.java)
            if (user != null) {
                // Award 10 roots per vouch
                userRef.child("rootsBalance").setValue(user.rootsBalance + 10).await()
            }
        }
    }

    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val ref = db.getReference("Users").orderByChild("username")
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                trySend(users)
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getUserData(userId: String): Flow<User?> = callbackFlow {
        val ref = db.getReference("Users").child(userId)
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                trySend(snapshot.getValue(User::class.java))
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateProfile(userId: String, updates: Map<String, Any?>) {
        db.getReference("Users").child(userId).updateChildren(updates).await()
    }

    suspend fun setUserNote(userId: String, note: String?) {
        db.getReference("Users").child(userId).child("currentNote").setValue(note).await()
    }

    // Status
    suspend fun createStatus(status: Status) {
        val id = db.getReference("Statuses").push().key ?: return
        db.getReference("Statuses").child(id).setValue(status.copy(id = id)).await()
    }

    fun getStatuses(): Flow<List<Status>> = callbackFlow {
        val ref = db.getReference("Statuses")
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val currentTime = System.currentTimeMillis()
                val statuses = snapshot.children.mapNotNull { it.getValue(Status::class.java) }
                    .filter { it.expiresAt > currentTime }
                trySend(statuses.sortedByDescending { it.timestamp })
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    // Channels
    suspend fun createChannel(channel: Channel) {
        val id = db.getReference("Channels").push().key ?: return
        db.getReference("Channels").child(id).setValue(channel.copy(id = id)).await()
    }

    fun getChannels(): Flow<List<Channel>> = callbackFlow {
        val ref = db.getReference("Channels")
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val channels = snapshot.children.mapNotNull { it.getValue(Channel::class.java) }
                trySend(channels)
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getChannelUpdates(channelId: String): Flow<List<ChannelUpdate>> = callbackFlow {
        val ref = db.getReference("ChannelUpdates").orderByChild("channelId").equalTo(channelId)
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val updates = snapshot.children.mapNotNull { it.getValue(ChannelUpdate::class.java) }
                trySend(updates.sortedByDescending { it.timestamp })
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun postToChannel(update: ChannelUpdate) {
        val id = db.getReference("ChannelUpdates").push().key ?: return
        db.getReference("ChannelUpdates").child(id).setValue(update.copy(id = id)).await()
    }

    // Social Actions
    suspend fun likePost(postId: String, userId: String) {
        val ref = db.getReference("PriceReports").child(postId)
        val snapshot = ref.get().await()
        val report = snapshot.getValue(PriceReport::class.java) ?: return
        val currentLikes = report.likes.toMutableList()
        if (currentLikes.contains(userId)) {
            currentLikes.remove(userId)
        } else {
            currentLikes.add(userId)
            logActivity(userId, "LIKE", postId, report.itemName)
        }
        ref.child("likes").setValue(currentLikes).await()
    }

    suspend fun addComment(comment: Comment) {
        val id = db.getReference("Comments").child(comment.postId).push().key ?: return
        db.getReference("Comments").child(comment.postId).child(id).setValue(comment.copy(id = id)).await()
        
        // Update comment count
        val postRef = db.getReference("PriceReports").child(comment.postId)
        val snapshot = postRef.get().await()
        val report = snapshot.getValue(PriceReport::class.java)
        if (report != null) {
            postRef.child("commentCount").setValue(report.commentCount + 1).await()
            logActivity(comment.userId, "COMMENT", comment.postId, report.itemName)
        }
    }

    fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val ref = db.getReference("Comments").child(postId)
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val comments = snapshot.children.mapNotNull { it.getValue(Comment::class.java) }
                trySend(comments.sortedBy { it.timestamp })
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    // Activity Tracking
    suspend fun logActivity(userId: String, type: String, targetId: String, title: String) {
        val id = db.getReference("Activities").child(userId).push().key ?: return
        val activity = UserActivity(id, userId, type, targetId, title)
        db.getReference("Activities").child(userId).child(id).setValue(activity).await()
    }

    fun getActivities(userId: String): Flow<List<UserActivity>> = callbackFlow {
        val ref = db.getReference("Activities").child(userId)
        val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val activities = snapshot.children.mapNotNull { it.getValue(UserActivity::class.java) }
                trySend(activities.sortedByDescending { it.timestamp })
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun seedPresentationData() {
        val dummyUsers = listOf(
            User(
                uid = "eco_pioneer",
                username = "EcoPioneer",
                fullName = "Grace Hopper",
                email = "grace@yggdrasil.hub",
                rootsBalance = 1500,
                followersCount = 120,
                followingCount = 45,
                bio = "Pioneering sustainable agriculture in the digital age.",
                profilePicUrl = "https://res.cloudinary.com/dxk6p6k6x/image/upload/v1/yggdrasil/yggdrasil3",
                isPrivate = false
            ),
            User(
                uid = "agri_seer",
                username = "AgriSeer",
                fullName = "Alan Turing",
                email = "alan@yggdrasil.hub",
                rootsBalance = 890,
                followersCount = 340,
                followingCount = 12,
                bio = "Decoding the secrets of the soil.",
                profilePicUrl = "https://res.cloudinary.com/dxk6p6k6x/image/upload/v1/yggdrasil/yggdrasil4",
                isPrivate = true
            )
        )
        
        val dummyReports = listOf(
            PriceReport("r1", "Heirloom Tomatoes", "produce", 350.0, "kg", "Organic Market", null, null, "https://res.cloudinary.com/dxk6p6k6x/image/upload/v1/yggdrasil/yggdrasil3", "eco_pioneer", "EcoPioneer", 8, true),
            PriceReport("r2", "Local Honey", "staples", 800.0, "jar", "Village Square", null, null, "https://res.cloudinary.com/dxk6p6k6x/image/upload/v1/yggdrasil/yggdrasil4", "agri_seer", "AgriSeer", 2, false)
        )

        val dummyLibraryItems = listOf(
            LibraryItem(
                id = "lib1",
                title = "The One-Straw Revolution",
                description = "An introduction to natural farming.",
                type = LibraryItemType.BOOK,
                url = "https://en.wikipedia.org/wiki/The_One-Straw_Revolution",
                author = "Masanobu Fukuoka",
                topic = "Natural Farming",
                thumbnail = "https://images.unsplash.com/photo-1589923188900-85dae523342b?auto=format&fit=crop&w=400"
            ),
            LibraryItem(
                id = "lib2",
                title = "Sustainable Irrigation",
                description = "Efficient water management for small farms.",
                type = LibraryItemType.VIDEO,
                url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                author = "Eco Hub",
                topic = "Water Conservation",
                thumbnail = "https://images.unsplash.com/photo-1563514227147-6d2ff665a6a0?auto=format&fit=crop&w=400"
            )
        )

        val dummyStatuses = listOf(
            Status(
                id = "s1",
                userId = "eco_pioneer",
                username = "EcoPioneer",
                userProfilePic = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&h=200",
                imageUrl = "https://images.unsplash.com/photo-1523348837708-15d4a09cfac2?auto=format&fit=crop&w=1080",
                musicTitle = "Forest Sounds - Nature",
                audience = "EVERYONE"
            ),
            Status(
                id = "s2",
                userId = "agri_seer",
                username = "AgriSeer",
                userProfilePic = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=200&h=200",
                textContent = "Protecting the ecosystem starts with the soil. 🌱",
                backgroundColor = 0xFF1B5E20,
                audience = "BRANCH_MEMBERS"
            )
        )

        dummyUsers.forEach { db.getReference("Users").child(it.uid).setValue(it).await() }
        dummyReports.forEach { db.getReference("PriceReports").child(it.id).setValue(it).await() }
        dummyLibraryItems.forEach { db.getReference("LibraryItems").child(it.id).setValue(it).await() }
        dummyStatuses.forEach { db.getReference("Statuses").child(it.id).setValue(it).await() }
    }
}
