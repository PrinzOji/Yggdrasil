package com.ojiem.yggdrasil.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ojiem.yggdrasil.ui.components.HubButton
import com.ojiem.yggdrasil.ui.viewmodel.DashViewModel
import com.ojiem.yggdrasil.ui.viewmodel.AuthViewModel
import com.ojiem.yggdrasil.ui.navigation.*
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureSeaBlue
import com.ojiem.yggdrasil.ui.theme.NatureForestGreen
import com.ojiem.yggdrasil.ui.theme.NatureSkyBlue
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.NatureTeal
import com.ojiem.yggdrasil.ui.theme.glassmorphism
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DashScreen(navController: NavController) {
    val dashViewModel: DashViewModel = viewModel()
    val userData by dashViewModel.userData.collectAsState()
    
    var visible by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }
    var showCriticalData by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val userEmail = userData?.email ?: "User"
    val rootsBalance = userData?.rootsBalance ?: 0

    val maskedEmail = remember(userEmail) {
        if (userEmail.contains("@")) {
            val parts = userEmail.split("@")
            val name = parts[0]
            if (name.length > 2) {
                "${name[0]}***${name.last()}@${parts[1]}"
            } else {
                "***@${parts[1]}"
            }
        } else userEmail
    }

    LaunchedEffect(Unit) { visible = true }

    NatureBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = visible, enter = fadeIn() + slideInVertically()) {
                    Column(
                        modifier = Modifier.clickable { navController.navigate(ROUTE_PROFILE) }
                    ) {
                        Text("Yggdrasil Hub", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Account: ${if (showCriticalData) userEmail else maskedEmail}",
                                color = NatureMint.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (showCriticalData) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Privacy",
                                tint = NatureMint.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { showCriticalData = !showCriticalData }
                            )
                        }
                    }
                }
                
                Row {
                    IconButton(
                        onClick = { navController.navigate(ROUTE_SETTINGS) },
                        modifier = Modifier.glassmorphism(CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = { 
                            authViewModel.logout()
                            navController.navigate(com.ojiem.yggdrasil.ui.navigation.ROUTE_AUTH) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.glassmorphism(CircleShape, containerColor = Color.Red.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Red.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = if (isSyncing) "SYSTEM STATUS: UPDATING..." else "SYSTEM STATUS: OPTIMAL",
                        color = if (isSyncing) NatureMint else NatureForestGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ecosystem Vitality", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            Text("$rootsBalance Roots", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        HubButton(
                            onClick = { /* Support Logic */ },
                            modifier = Modifier.height(44.dp).width(120.dp)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Support", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val nodes = listOf(
                DashNode("Library", Icons.Default.MenuBook, NatureSkyBlue) { navController.navigate(ROUTE_LIBRARY) },
                DashNode("Inventory", Icons.Default.Inventory, NatureForestGreen) { navController.navigate(ROUTE_INVENTORY) },
                DashNode("New Entry", Icons.Default.AddBusiness, NatureSeaBlue) { navController.navigate(ROUTE_REPORT) },
                DashNode("Profile", Icons.Default.Person, NatureMint) { navController.navigate(ROUTE_PROFILE) },
                DashNode("Analytics", Icons.Default.BarChart, NatureTeal) { navController.navigate(ROUTE_ANALYTICS) },
                DashNode("Finance", Icons.Default.AccountBalanceWallet, NatureMint) { navController.navigate(ROUTE_FINANCE) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in nodes.indices step 2) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        NodeCard(nodes[i], Modifier.weight(1f))
                        if (i + 1 < nodes.size) {
                            NodeCard(nodes[i + 1], Modifier.weight(1f))
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Presentation Seed Button
            HubButton(
                onClick = { 
                    dashViewModel.seedData()
                    scope.launch {
                        isSyncing = true
                        delay(2000)
                        isSyncing = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Seed Presentation Data", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

data class DashNode(val title: String, val icon: ImageVector, val color: Color, val action: () -> Unit)

@Composable
fun NodeCard(node: DashNode, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(160.dp)
            .glassmorphism(RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .clickable { node.action() }
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(node.color.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .border(1.dp, node.color.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(node.icon, contentDescription = null, tint = node.color)
            }
            Text(node.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
