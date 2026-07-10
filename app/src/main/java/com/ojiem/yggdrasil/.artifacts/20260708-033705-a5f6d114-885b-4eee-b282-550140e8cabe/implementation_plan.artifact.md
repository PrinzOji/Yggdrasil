# Forgot Password Implementation Plan

Add the "Forgot Password" functionality to the authentication flow, allowing users to receive a password reset email via Firebase.

## Proposed Changes

### Repository

#### [FirebaseRepository.kt](file:///C:/Users/zanda/AndroidStudioProjects/Prototype2/app/src/main/java/com/ojiem/yggdrasil/data/repository/FirebaseRepository.kt)

- Add a `sendPasswordResetEmail(email: String)` function.

```kotlin
    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
```

---

### ViewModel

#### [AuthViewModel.kt](file:///C:/Users/zanda/AndroidStudioProjects/Prototype2/app/src/main/java/com/ojiem/yggdrasil/ui/viewmodel/AuthViewModel.kt)

- Add a `resetPassword(email: String)` function.
- Update `AuthEvent` to include a success message for password reset.

```kotlin
sealed class AuthEvent {
    object Success : AuthEvent()
    data class Info(val message: String) : AuthEvent() // New event for non-navigation success
    data class Error(val message: String) : AuthEvent()
}

// In AuthViewModel
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            emitError("Please enter your email address")
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                repository.sendPasswordResetEmail(email)
                _authEvent.emit(AuthEvent.Info("Password reset email sent! Check your inbox."))
            } catch (e: Exception) {
                emitError(e.localizedMessage ?: "Failed to send reset email")
            } finally {
                isLoading = false
            }
        }
    }
```

---

### Auth Screen

#### [AuthScreen.kt](file:///C:/Users/zanda/AndroidStudioProjects/Prototype2/app/src/main/java/com/ojiem/yggdrasil/ui/screens/auth/AuthScreen.kt)

- Add a "Forgot Password?" button in Login mode.
- Implement a simple `AlertDialog` to collect the user's email for reset.

```kotlin
            if (!isSignUpMode) {
                TextButton(
                    onClick = { showResetDialog = true },
                    enabled = !authViewModel.isLoading,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?", color = NatureMint, fontSize = 12.sp)
                }
            }
```

## Verification Plan

### Manual Verification
- **Reset Flow**:
    - Open the App to the Auth screen.
    - Click "Forgot Password?".
    - Enter a valid email address registered in Firebase.
    - Verify that a success message is shown.
    - Check the email inbox for the reset link (if using a real account).
    - Enter an invalid/empty email and verify error handling.
