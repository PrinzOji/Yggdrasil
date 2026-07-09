package com.ojiem.yggdrasil.ui.screens.realms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.ojiem.yggdrasil.data.model.Status
import com.ojiem.yggdrasil.ui.screens.profile.StatusViewer
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.glassmorphism
import com.ojiem.yggdrasil.ui.viewmodel.ProfileViewModel

@Composable
fun RealmsScreen() {
    val viewModel: ProfileViewModel = viewModel()
    val statuses by viewModel.statuses.collectAsState()
    val userData by viewModel.userData.collectAsState()
    
    var activeStatusList by remember { mutableStateOf<List<Status>?>(null) }
    var showCallDialog by remember { mutableStateOf<RealmSection?>(null) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }

    val sections = listOf(
        RealmSection("Ecosystem Status", "Overall health of all nodes", Icons.Default.Hub, NatureMint),
        RealmSection("Channels", "Connect with other seers", Icons.Default.Chat, Color.Yellow),
        RealmSection("Search Hub", "Find seers, posts, and lore", Icons.Default.Search, Color.White),
        RealmSection("Node Calls", "Immediate node support", Icons.Default.Call, Color.Red),
        RealmSection("Oracle Help", "Aid for your inquiries", Icons.Default.Help, Color.Cyan)
    )

    NatureBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(56.dp))
            
            Text("The Realms", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Communication and status hub of Yggdrasil", color = NatureMint.copy(alpha = 0.7f), fontSize = 14.sp)

            Spacer(Modifier.height(24.dp))

            // Realm Statuses (The requested part of Realms)
            Text("Citizen Statuses", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                statuses.groupBy { it.userId }.forEach { (userId, userStatusList) ->
                    val statusUser = userStatusList.first()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(NatureMint, CircleShape)
                                .padding(2.dp)
                                .background(Color.Black, CircleShape)
                                .clickable { activeStatusList = userStatusList },
                            contentAlignment = Alignment.Center
                        ) {
                            if (statusUser.userProfilePic != null) {
                                AsyncImage(
                                    model = statusUser.userProfilePic,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            }
                        }
                        Text(statusUser.username, color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Realm Cards
            sections.forEach { section ->
                RealmCard(section) {
                    when (section.title) {
                        "Node Calls" -> showCallDialog = section
                        "Oracle Help" -> showHelpDialog = true
                        "Search Hub" -> showSearchDialog = true
                        else -> { /* Navigate to other sections */ }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
            
            Spacer(Modifier.height(100.dp))
        }
    }

    if (activeStatusList != null) {
        StatusViewer(
            statuses = activeStatusList!!,
            onDismiss = { activeStatusList = null }
        )
    }

    if (showCallDialog != null) {
        CallSelectionDialog(
            section = showCallDialog!!,
            onDismiss = { showCallDialog = null }
        )
    }
    
    if (showHelpDialog) {
        OracleHelpDialog(onDismiss = { showHelpDialog = false })
    }

    if (showSearchDialog) {
        EcosystemSearchDialog(onDismiss = { showSearchDialog = false })
    }
}

data class RealmSection(val title: String, val desc: String, val icon: ImageVector, val color: Color)

@Composable
fun RealmCard(section: RealmSection, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(section.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(section.icon, contentDescription = null, tint = section.color, modifier = Modifier.size(28.dp))
            }
            
            Spacer(Modifier.width(20.dp))
            
            Column {
                Text(section.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(section.desc, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun CallSelectionDialog(section: RealmSection, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Communicate with Nodes") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Select your preferred communication protocol:")
                
                Button(
                    onClick = { /* Start Audio Call Logic */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NatureMint)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("Internet Audio Call")
                }
                
                Button(
                    onClick = { /* Start Video Call Logic */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = section.color)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("Internet Video Call")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun OracleHelpDialog(onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var response by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Oracle of Yggdrasil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("The Oracle provides aid to your inquiries about the ecosystem.")
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Your Inquiry") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., How do I earn roots?") }
                )
                
                if (response != null) {
                    Surface(
                        color = NatureMint.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = response!!,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    response = when {
                        query.contains("root", ignoreCase = true) -> "You earn roots by submitting price reports and getting vouches from other seers."
                        query.contains("status", ignoreCase = true) -> "Statuses allow you to share your node's current state. They last for 24 hours."
                        query.contains("call", ignoreCase = true) -> "You can use Node Calls in the Realms to communicate directly with other nodes via audio or video."
                        else -> "I am processing your request. Please ensure your inquiry relates to the Yggdrasil ecosystem."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NatureMint)
            ) {
                Text("Consult")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun EcosystemSearchDialog(onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ecosystem Search") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search Seers, Lore, or Updates") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                
                Text(
                    "Searching for: $query",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
                
                Text("Search results will appear here...", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
