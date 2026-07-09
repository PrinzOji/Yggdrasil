# Auth Improvements & Gradient Border Implementation Plan

Address the "Internal Error" during authentication by improving error handling and state management, and implement the color-changing animated gradient border for `HubTextField`.

## User Review Required

- **Firebase Configuration**: "Internal Error" is often caused by Firebase settings outside the code. Please ensure:
    - **Email/Password** is enabled in the Firebase Console (Authentication -> Sign-in method).
    - If using **Realtime Database**, ensure the rules allow writing to `/Users/{uid}`.
    - The device has a stable internet connection.

## Proposed Changes

### Auth & Navigation

#### [AuthViewModel.kt](file:///C:/Users/zanda/AndroidStudioProjects/Prototype2/app/src/main/java/com/ojiem/yggdrasil/ui/viewmodel/AuthViewModel.kt)

- Refactor `AuthViewModel` to inherit from `androidx.lifecycle.ViewModel` for better lifecycle management.
- Remove `navController` and `context` from the constructor to avoid memory leaks.
- Use a `StateFlow` or similar for `isLoading` (or keep `mutableStateOf` if preferred for Compose, but managed correctly).
- Add success/error callbacks or events for navigation to be handled by the UI layer.
- Enhance error messages to help debug the "Internal Error".

#### [AuthScreen.kt](file:///C:/Users/zanda/AndroidStudioProjects/Prototype2/app/src/main/java/com/ojiem/yggdrasil/ui/screens/auth/AuthScreen.kt)

- Use `viewModel()` or `remember { AuthViewModel(...) }` (with proper factory if needed) to avoid recreating the ViewModel on every recomposition.
- Handle navigation to `ROUTE_HOME` in a `LaunchedEffect` or via callbacks from the ViewModel.

---

### UI Components

#### [HubComponents.kt](file:///C:/Users/zanda/AndroidStudioProjects/Prototype2/app/src/main/java/com/ojiem/yggdrasil/ui/components/HubComponents.kt)

- Update `HubTextField` to use an animated gradient border when focused.
- Switch from `OutlinedTextField` to `TextField` with custom styling to avoid label cutout issues with the gradient.

```kotlin
@Composable
fun HubTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val brush = shiftingGradient(ButtonGradientColors)

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = GlassTextSecondary) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color.White) },
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                brush = if (isFocused) brush else Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.2f))),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = NatureMint,
            focusedLabelColor = NatureMint,
            unfocusedLabelColor = GlassTextSecondary
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        interactionSource = interactionSource,
        singleLine = true
    )
}
```

## Verification Plan

### Manual Verification
- **Auth Flow**:
    - Attempt Login/Signup and verify that "Internal Error" is either resolved by better state management or produces a more descriptive error.
    - Verify redirection to Home screen on success.
- **UI**:
    - Verify the animated gradient border on focused `HubTextField` in both `AuthScreen` and `ReportPriceScreen`.
    - Ensure the label positioning and appearance are correct during and after focus.
