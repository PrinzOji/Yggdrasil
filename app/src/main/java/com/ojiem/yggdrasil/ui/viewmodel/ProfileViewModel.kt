package com.ojiem.yggdrasil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojiem.yggdrasil.data.model.PriceReport
import com.ojiem.yggdrasil.data.model.User
import com.ojiem.yggdrasil.data.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import android.content.Context
import android.net.Uri
import com.ojiem.yggdrasil.data.model.Channel
import com.ojiem.yggdrasil.data.model.ChannelUpdate
import com.ojiem.yggdrasil.data.model.Status
import com.ojiem.yggdrasil.data.model.UserActivity

class ProfileViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    // Cloudinary config
    private val cloudinaryUrl = "https://api.cloudinary.com/v1_1/dxk6p6k6x/image/upload"
    private val uploadPreset = "yggdrasil_preset"

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    private val _userPosts = MutableStateFlow<List<PriceReport>>(emptyList())
    val userPosts: StateFlow<List<PriceReport>> = _userPosts

    private val _statuses = MutableStateFlow<List<Status>>(emptyList())
    val statuses: StateFlow<List<Status>> = _statuses

    private val _activities = MutableStateFlow<List<UserActivity>>(emptyList())
    val activities: StateFlow<List<UserActivity>> = _activities

    private val _userChannel = MutableStateFlow<Channel?>(null)
    val userChannel: StateFlow<Channel?> = _userChannel

    private var _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        val currentUserId = repository.getCurrentUser()?.uid
        if (currentUserId != null) {
            viewModelScope.launch {
                repository.getUserData(currentUserId).collect { user ->
                    _userData.value = user
                }
            }
            viewModelScope.launch {
                repository.getReportsByUser(currentUserId).collect { posts ->
                    _userPosts.value = posts
                }
            }
            viewModelScope.launch {
                repository.getStatuses().collect { statuses ->
                    _statuses.value = statuses
                }
            }
            viewModelScope.launch {
                repository.getActivities(currentUserId).collect { activities ->
                    _activities.value = activities
                }
            }
            viewModelScope.launch {
                repository.getChannels().collect { channels ->
                    _userChannel.value = channels.find { it.ownerId == currentUserId }
                }
            }
        }
    }

    fun updateNote(note: String?) {
        val userId = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            repository.setUserNote(userId, note)
        }
    }

    fun updateProfilePicture(context: Context, uri: Uri) {
        val userId = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _isUpdating.value = true
            try {
                val url = uploadToCloudinary(context, uri)
                repository.updateProfile(userId, mapOf("profilePicUrl" to url))
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun updateProfessionalDetails(
        job: String,
        talents: String,
        portfolio: String,
        skills: String,
        qualifications: String,
        bio: String
    ) {
        val userId = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            val updates = mapOf(
                "job" to job,
                "talents" to talents,
                "portfolio" to portfolio,
                "skills" to skills,
                "qualifications" to qualifications,
                "bio" to bio
            )
            repository.updateProfile(userId, updates)
        }
    }

    fun updatePrivacy(isPrivate: Boolean? = null, allowComments: Boolean? = null) {
        val userId = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            val updates = mutableMapOf<String, Any>()
            isPrivate?.let { updates["isPrivate"] = it }
            allowComments?.let { updates["allowComments"] = it }
            if (updates.isNotEmpty()) {
                repository.updateProfile(userId, updates)
            }
        }
    }

    // Channel Logic
    fun createChannel(name: String, description: String) {
        val userId = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            val channel = Channel(
                ownerId = userId,
                name = name,
                description = description
            )
            repository.createChannel(channel)
        }
    }

    fun postToChannel(text: String) {
        val channel = _userChannel.value ?: return
        viewModelScope.launch {
            val update = ChannelUpdate(
                channelId = channel.id,
                text = text
            )
            repository.postToChannel(update)
        }
    }

    // Status Logic
    fun postTextStatus(text: String, color: Long, musicTitle: String? = null, audience: String = "EVERYONE") {
        val user = _userData.value ?: return
        viewModelScope.launch {
            val status = Status(
                userId = user.uid,
                username = user.username,
                userProfilePic = user.profilePicUrl,
                textContent = text,
                backgroundColor = color,
                musicTitle = musicTitle,
                audience = audience
            )
            repository.createStatus(status)
        }
    }

    fun postImageStatus(context: Context, uri: Uri) {
        val user = _userData.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = uploadToCloudinary(context, uri)
                val status = Status(
                    userId = user.uid,
                    username = user.username,
                    userProfilePic = user.profilePicUrl,
                    imageUrl = url
                )
                repository.createStatus(status)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postVideoStatus(context: Context, uri: Uri) {
        val user = _userData.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Cloudinary handles video uploads if the resource_type is set to video
                val url = uploadVideoToCloudinary(context, uri)
                val status = Status(
                    userId = user.uid,
                    username = user.username,
                    userProfilePic = user.profilePicUrl,
                    videoUrl = url
                )
                repository.createStatus(status)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadVideoToCloudinary(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val fileByte = inputStream?.readBytes() ?: throw Exception("Video failed to read")
        
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", "video.mp4", fileByte.toRequestBody("video/*".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", uploadPreset)
            .addFormDataPart("resource_type", "video")
            .build()

        val request = Request.Builder().url(cloudinaryUrl.replace("/image/", "/video/")).post(requestBody).build()
        val response = OkHttpClient().newCall(request).execute()
        
        if (!response.isSuccessful) throw Exception("Cloudinary video upload failed: ${response.message}")
        
        val responseBody = response.body?.string()
        return Regex("\"secure_url\":\"(.*?)\"").find(responseBody ?: "")?.groupValues?.get(1)
            ?: throw Exception("Failed to extract video URL")
    }

    private fun uploadToCloudinary(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val fileByte = inputStream?.readBytes() ?: throw Exception("Image failed to read")
        
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", "image.jpg", fileByte.toRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder().url(cloudinaryUrl).post(requestBody).build()
        val response = OkHttpClient().newCall(request).execute()
        
        if (!response.isSuccessful) throw Exception("Cloudinary upload failed: ${response.message}")
        
        val responseBody = response.body?.string()
        return Regex("\"secure_url\":\"(.*?)\"").find(responseBody ?: "")?.groupValues?.get(1)
            ?: throw Exception("Failed to extract image URL")
    }
}
