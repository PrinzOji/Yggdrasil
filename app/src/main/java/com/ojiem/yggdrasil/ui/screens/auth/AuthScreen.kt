package com.ojiem.yggdrasil.ui.screens.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ojiem.yggdrasil.R
import com.ojiem.yggdrasil.ui.components.HubButton
import com.ojiem.yggdrasil.ui.components.HubTextField
import com.ojiem.yggdrasil.ui.navigation.ROUTE_HOME
import com.ojiem.yggdrasil.ui.theme.*
import com.ojiem.yggdrasil.ui.viewmodel.AuthEvent
import com.ojiem.yggdrasil.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(navController: NavHostController) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()

    LaunchedEffect(Unit) {
        authViewModel.authEvent.collect { event ->
            when (event) {
                is AuthEvent.Success -> {
                    Toast.makeText(context, "Authentication Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is AuthEvent.Info -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is AuthEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    NatureBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.yggdrasil_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isSignUpMode) "Join Yggdrasil" else "Welcome Back",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (isSignUpMode) "Create your account to start growing" else "Sign in to access your dashboard",
                color = NatureMint.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    if (isSignUpMode) {
                        HubTextField(
                            value = username, 
                            onValueChange = { username = it }, 
                            label = "Username", 
                            icon = Icons.Default.Person
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HubTextField(
                            value = fullName, 
                            onValueChange = { fullName = it }, 
                            label = "Full Name", 
                            icon = Icons.Default.Person
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    HubTextField(
                        value = email, 
                        onValueChange = { email = it }, 
                        label = "Email Address", 
                        icon = Icons.Default.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                        HubTextField(
                            value = password, 
                            onValueChange = { password = it }, 
                            label = "Password", 
                            icon = Icons.Default.Lock, 
                            isPassword = true
                        )
                        
                        if (!isSignUpMode) {
                            TextButton(
                                onClick = { 
                                    resetEmail = email
                                    showResetDialog = true 
                                },
                                modifier = Modifier.align(Alignment.End),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Forgot Password?", color = NatureMint, fontSize = 12.sp)
                            }
                        }
                    }
                }

            Spacer(modifier = Modifier.height(32.dp))

            HubButton(
                onClick = {
                    if (isSignUpMode) {
                        authViewModel.signup(username, fullName, email, password, password)
                    } else {
                        authViewModel.login(email.trim(), password.trim())
                    }
                },
                enabled = !authViewModel.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (authViewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White, 
                        modifier = Modifier.size(24.dp), 
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isSignUpMode) "SIGN UP" else "LOG IN", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { isSignUpMode = !isSignUpMode },
                enabled = !authViewModel.isLoading
            ) {
                Text(
                    text = if (isSignUpMode) "Already have an account? Log In" else "Don't have an account? Sign Up",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your email to receive a password reset link.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    HubTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = "Email Address",
                        icon = Icons.Default.Email
                    )
                }
            },
            confirmButton = {
                HubButton(
                    onClick = {
                        authViewModel.resetPassword(resetEmail.trim())
                        showResetDialog = false
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("SEND RESET LINK", color = Color.White, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
