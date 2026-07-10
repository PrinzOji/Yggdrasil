package com.ojiem.yggdrasil.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojiem.yggdrasil.data.model.PriceReport
import com.ojiem.yggdrasil.data.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ProductViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    var isSubmitting by mutableStateOf(false)
    var uploadStatus by mutableStateOf("")

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _reports = MutableStateFlow<List<PriceReport>>(emptyList())
    val reports: StateFlow<List<PriceReport>> = _reports

    private val _selectedReport = MutableStateFlow<PriceReport?>(null)
    val selectedReport: StateFlow<PriceReport?> = _selectedReport

    // Cloudinary config
    private val cloudinaryUrl = "https://api.cloudinary.com/v1_1/dxk6p6k6x/image/upload"
    private val uploadPreset = "yggdrasil_preset"

    init {
        fetchReports()
    }

    private fun fetchReports() {
        viewModelScope.launch {
            repository.getReports()
                .catch { e ->
                    // Handle error
                }
                .collect { fetchedReports ->
                    _reports.value = fetchedReports
                }
        }
    }

    fun refreshReports() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchReports()
            kotlinx.coroutines.delay(1000)
            _isRefreshing.value = false
        }
    }

    fun fetchReportById(reportId: String) {
        viewModelScope.launch {
            try {
                _selectedReport.value = repository.getReportById(reportId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun reportPrice(
        context: Context,
        itemName: String,
        category: String,
        price: String,
        unit: String,
        marketName: String,
        imageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        if (itemName.isBlank() || price.isBlank() || marketName.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            isSubmitting = true
            try {
                var photoUrl: String? = null
                if (imageUri != null) {
                    uploadStatus = "Uploading image..."
                    photoUrl = uploadToCloudinary(context, imageUri)
                }

                val currentUser = repository.getCurrentUser()
                val reporterUid = currentUser?.uid ?: "anonymous"
                val reporterName = currentUser?.displayName ?: "Agent"
                
                val report = PriceReport(
                    itemName = itemName,
                    category = category,
                    priceKes = price.toDoubleOrNull() ?: 0.0,
                    unit = unit,
                    marketName = marketName,
                    photoUrl = photoUrl,
                    reporterUid = reporterUid,
                    reporterName = reporterName
                )

                uploadStatus = "broadcasting node..."
                repository.submitReport(report)
                
                withContext(Dispatchers.Main) {
                    isSubmitting = false
                    Toast.makeText(context, "Node Broadcasted 🌱", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isSubmitting = false
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
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
        val secureUrl = Regex("\"secure_url\":\"(.*?)\"")
            .find(responseBody ?: "")?.groupValues?.get(1)
            
        return secureUrl ?: throw Exception("Failed to extract image URL")
    }

    fun vouchForReport(reportId: String, onVouched: () -> Unit) {
        viewModelScope.launch {
            try {
                val userId = repository.getCurrentUser()?.uid ?: "anonymous"
                repository.vouchForReport(reportId, userId)
                onVouched()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
