package com.ojiem.yggdrasil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojiem.yggdrasil.data.model.User
import com.ojiem.yggdrasil.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val userId = repository.getCurrentUser()?.uid
            if (userId != null) {
                repository.getUserData(userId).collect { user ->
                    _userData.value = user
                }
            }
        }
    }

    fun seedData() {
        viewModelScope.launch {
            repository.seedPresentationData()
        }
    }
}
