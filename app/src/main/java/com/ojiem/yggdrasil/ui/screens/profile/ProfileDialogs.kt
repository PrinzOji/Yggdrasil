package com.ojiem.yggdrasil.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojiem.yggdrasil.data.model.User
import com.ojiem.yggdrasil.ui.components.HubButton
import com.ojiem.yggdrasil.ui.components.HubTextField
import com.ojiem.yggdrasil.ui.theme.NatureMint

@Composable
fun CreateChannelDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Channel") },
        text = {
            Column {
                HubTextField(value = name, onValueChange = { name = it }, label = "Channel Name", icon = Icons.Default.Groups)
                Spacer(Modifier.height(12.dp))
                HubTextField(value = desc, onValueChange = { desc = it }, label = "Description", icon = Icons.Default.Description)
            }
        },
        confirmButton = {
            HubButton(onClick = { onSave(name, desc) }, modifier = Modifier.padding(8.dp)) {
                Text("Create", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PostUpdateDialog(
    channelName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update $channelName") },
        text = {
            HubTextField(value = text, onValueChange = { text = it }, label = "Channel Update", icon = Icons.Default.Campaign)
        },
        confirmButton = {
            HubButton(onClick = { onSave(text) }, modifier = Modifier.padding(8.dp), enabled = text.isNotBlank()) {
                Text("Post", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddStatusDialog(
    onDismiss: () -> Unit,
    onPickImage: () -> Unit,
    onPickVideo: () -> Unit,
    onPostText: (String, Long, String?, String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var musicTitle by remember { mutableStateOf("") }
    val colors = listOf(0xFF2E7D32, 0xFF1565C0, 0xFFC62828, 0xFF6A1B9A)
    var selectedColor by remember { mutableStateOf(colors[0]) }
    
    val audiences = listOf("EVERYONE", "BRANCH_MEMBERS")
    var selectedAudience by remember { mutableStateOf(audiences[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Status") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onPickImage,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = NatureMint),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Image", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onPickVideo,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = NatureMint),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Video", fontSize = 12.sp)
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                Text("Privacy:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    audiences.forEach { aud ->
                        FilterChip(
                            selected = selectedAudience == aud,
                            onClick = { selectedAudience = aud },
                            label = { Text(aud.replace("_", " "), fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("Music Tool:", style = MaterialTheme.typography.labelMedium)
                HubTextField(value = musicTitle, onValueChange = { musicTitle = it }, label = "Add Music (e.g. Artist - Song)", icon = Icons.Default.MusicNote)
                
                Spacer(Modifier.height(16.dp))
                Text("Text Status:", style = MaterialTheme.typography.labelMedium)
                HubTextField(value = text, onValueChange = { text = it }, label = "Type something...", icon = Icons.Default.Edit)
                
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(color), CircleShape)
                                .clickable { selectedColor = color }
                                .padding(4.dp)
                        ) {
                            if (selectedColor == color) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            HubButton(
                onClick = { if (text.isNotBlank()) onPostText(text, selectedColor, if(musicTitle.isBlank()) null else musicTitle, selectedAudience) },
                modifier = Modifier.padding(8.dp),
                enabled = text.isNotBlank()
            ) {
                Text("Post Status", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var job by remember { mutableStateOf(user.job) }
    var talents by remember { mutableStateOf(user.talents) }
    var portfolio by remember { mutableStateOf(user.portfolio) }
    var skills by remember { mutableStateOf(user.skills) }
    var qual by remember { mutableStateOf(user.qualifications) }
    var bio by remember { mutableStateOf(user.bio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Professional Profile") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                HubTextField(value = bio, onValueChange = { bio = it }, label = "Bio", icon = Icons.Default.Info)
                Spacer(Modifier.height(12.dp))
                HubTextField(value = job, onValueChange = { job = it }, label = "Job/Role", icon = Icons.Default.Work)
                Spacer(Modifier.height(12.dp))
                HubTextField(value = talents, onValueChange = { talents = it }, label = "Talents", icon = Icons.Default.Star)
                Spacer(Modifier.height(12.dp))
                HubTextField(value = skills, onValueChange = { skills = it }, label = "Skills", icon = Icons.Default.Build)
                Spacer(Modifier.height(12.dp))
                HubTextField(value = portfolio, onValueChange = { portfolio = it }, label = "Portfolio Link", icon = Icons.Default.Link)
                Spacer(Modifier.height(12.dp))
                HubTextField(value = qual, onValueChange = { qual = it }, label = "Qualifications", icon = Icons.Default.School)
            }
        },
        confirmButton = {
            HubButton(onClick = { onSave(job, talents, portfolio, skills, qual, bio) }, modifier = Modifier.padding(8.dp)) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun NoteDialog(
    currentNote: String,
    onDismiss: () -> Unit,
    onSave: (String?) -> Unit
) {
    var note by remember { mutableStateOf(currentNote) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Note") },
        text = {
            HubTextField(
                value = note,
                onValueChange = { if (it.length <= 60) note = it },
                label = "What's on your mind? (60 chars)",
                icon = Icons.Default.ChatBubble
            )
        },
        confirmButton = {
            HubButton(onClick = { onSave(if (note.isBlank()) null else note) }, modifier = Modifier.padding(8.dp)) {
                Text("Update", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
