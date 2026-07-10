package com.ojiem.yggdrasil.ui.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ojiem.yggdrasil.R
import com.ojiem.yggdrasil.ui.navigation.ROUTE_HOME
import com.ojiem.yggdrasil.ui.navigation.ROUTE_AUTH
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo entrance animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = { OvershootInterpolator(2f).getInterpolation(it) })
        )
        // Text fade in
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000)
        )
        
        delay(3500L) // Wait for user to see the logo
        
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate(ROUTE_HOME) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate(ROUTE_AUTH) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Semi-transparent overlay to make text/logo pop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.yggdrasil_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(240.dp)
                        .scale(scale.value)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "YGGDRASIL",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp,
                    modifier = Modifier.alpha(alpha.value)
                )
                
                Text(
                    text = "THE WORLD TREE",
                    color = NatureMint,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    modifier = Modifier.alpha(alpha.value)
                )
            }
        }
    }
}
