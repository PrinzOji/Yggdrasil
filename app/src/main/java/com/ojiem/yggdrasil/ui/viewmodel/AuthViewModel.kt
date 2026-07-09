package com.ojiem.yggdrasil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojiem.yggdrasil.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class AuthEvent {
    object Success : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

class AuthViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    var isLoading by mutableStateOf(false)
        private set

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    fun signup(username: String, fullName: String, email: String, password: String, confirmPassword: String) {
        if (username.isBlank() || fullName.isBlank() || email.isBlank() || password.isBlank()) {
            emitError("Please fill required fields")
            return
        } 
        
        if (password != confirmPassword) {
            emitError("Passwords do not match")
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                repository.signUp(email, password, username, fullName)
                emitSuccess()
            } catch (e: Exception) {
                emitError(e.localizedMessage ?: "Registration Failed")
            } finally {
                isLoading = false
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            emitError("Please fill all fields")
            return
        }
        
        viewModelScope.launch {
            isLoading = true
            try {
                repository.signIn(email, password)
                
                // Fetch dynamic profile data from Google/Firebase Auth
                val fbUser = repository.getCurrentUser()
                if (fbUser != null) {
                    val updates = mutableMapOf<String, Any>()
                    
                    // Extract Profile Pic from the email account
                    fbUser.photoUrl?.let { updates["profilePicUrl"] = it.toString() }
                    
                    // Extract Display Name if available
                    fbUser.displayName?.let { updates["fullName"] = it }
                    
                    if (updates.isNotEmpty()) {
                        repository.updateProfile(fbUser.uid, updates)
                    }
                }

                emitSuccess()
            } catch (e: Exception) {
                emitError(e.localizedMessage ?: "Authentication Failed")
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        repository.signOut()
    }

    private fun emitSuccess() {
        viewModelScope.launch {
            _authEvent.emit(AuthEvent.Success)
        }
    }

    private fun emitError(message: String) {
        viewModelScope.launch {
            _authEvent.emit(AuthEvent.Error(message))
        }
    }

    fun isLogged(): Boolean {
        return repository.isUserSignedIn()
    }
}
