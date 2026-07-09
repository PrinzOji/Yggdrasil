package com.ojiem.yggdrasil.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ojiem.yggdrasil.data.model.UserActivity
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.glassmorphism
import com.ojiem.yggdrasil.ui.viewmodel.ProfileViewModel
import com.ojiem.yggdrasil.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = remember { SettingsViewModel(context) }
    val profileViewModel: ProfileViewModel = viewModel()
    val isMusicEnabled by viewModel.isMusicEnabled
    val userData by profileViewModel.userData.collectAsState()
    val activities by profileViewModel.activities.collectAsState()

    var showActivityDialog by remember { mutableStateOf(false) }

    NatureBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.glassmorphism(CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Control Center", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Privacy & Interaction Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Privacy & Safety", color = NatureMint, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    
                    SettingToggle(
                        title = "Private Account",
                        subtitle = "Only seers you approve can see your reports",
                        icon = Icons.Default.Lock,
                        checked = userData?.isPrivate ?: false,
                        onCheckedChange = { profileViewModel.updatePrivacy(isPrivate = it) }
                    )
                    
                    Spacer(Modifier.height(20.dp))
                    
                    SettingToggle(
                        title = "Allow Comments",
                        subtitle = "Allow others to provide aid in your reports",
                        icon = Icons.Default.Comment,
                        checked = userData?.allowComments ?: true,
                        onCheckedChange = { profileViewModel.updatePrivacy(allowComments = it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // System Settings Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("System Hub", color = NatureMint, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    
                    SettingToggle(
                        title = "Ambient Nature Music",
                        subtitle = "Continuous ecosystem soundscape",
                        icon = if (isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                        checked = isMusicEnabled,
                        onCheckedChange = { viewModel.toggleMusic(it) }
                    )
                    
                    Spacer(Modifier.height(20.dp))
                    
                    // Activity History Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showActivityDialog = true },
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = NatureMint)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Echoes of Activity", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("View your path through the ecosystem", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showActivityDialog) {
        ActivityHistoryDialog(
            activities = activities,
            onDismiss = { showActivityDialog = false }
        )
    }
}

@Composable
fun SettingToggle(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = NatureMint)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NatureMint,
                checkedTrackColor = NatureMint.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun ActivityHistoryDialog(
    activities: List<UserActivity>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Echoes of Activity") },
        text = {
            if (activities.isEmpty()) {
                Text("No activities found in your path.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(activities) { activity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                when(activity.type) {
                                    "LIKE" -> Icons.Default.Favorite
                                    "COMMENT" -> Icons.Default.ChatBubble
                                    "VIEW_POST" -> Icons.Default.Visibility
                                    else -> Icons.Default.Bolt
                                },
                                contentDescription = null,
                                tint = NatureMint,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    when(activity.type) {
                                        "LIKE" -> "You liked ${activity.targetTitle}"
                                        "COMMENT" -> "You commented on ${activity.targetTitle}"
                                        "VIEW_POST" -> "You viewed ${activity.targetTitle}"
                                        else -> "Action on ${activity.targetTitle}"
                                    },
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text("Just now", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}
