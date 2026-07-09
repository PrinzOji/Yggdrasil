package com.ojiem.yggdrasil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ojiem.yggdrasil.data.model.User
import com.ojiem.yggdrasil.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _topUsers = MutableStateFlow<List<User>>(emptyList())
    val topUsers: StateFlow<List<User>> = _topUsers

    init {
        fetchTopUsers()
    }

    private fun fetchTopUsers() {
        viewModelScope.launch {
            repository.getTopUsers()
                .catch { e ->
                    // Handle error
                }
                .collect { users ->
                    _topUsers.value = users
                }
        }
    }
}
