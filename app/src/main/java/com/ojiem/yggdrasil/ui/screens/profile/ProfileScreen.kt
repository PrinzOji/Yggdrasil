package com.ojiem.yggdrasil.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import androidx.annotation.OptIn
import kotlinx.coroutines.delay
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import com.ojiem.yggdrasil.data.model.User
import com.ojiem.yggdrasil.data.model.Status
import com.ojiem.yggdrasil.ui.components.HubButton
import com.ojiem.yggdrasil.ui.components.HubTextField
import com.ojiem.yggdrasil.ui.viewmodel.ProfileViewModel
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.glassmorphism

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel()
    val userData by viewModel.userData.collectAsState()
    val userPosts by viewModel.userPosts.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val statuses by viewModel.statuses.collectAsState()
    val userChannel by viewModel.userChannel.collectAsState()
    
    val user = userData ?: User(username = "Agent", fullName = "Loading...")
    var showEditDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var showChannelDialog by remember { mutableStateOf(false) }
    var showPostUpdateDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showPicOptions by remember { mutableStateOf(false) }
    var activeStatusList by remember { mutableStateOf<List<Status>?>(null) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateProfilePicture(context, it) }
    }

    val statusImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.postImageStatus(context, it) }
    }

    val statusVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.postVideoStatus(context, it) }
    }

    NatureBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(user.username, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { /* Settings */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Row
            val myStatus = statuses.filter { it.userId == user.uid }
            val otherStatuses = statuses.filter { it.userId != user.uid }.groupBy { it.userId }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // My Status entry
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val hasStatus = myStatus.isNotEmpty()
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .then(
                                if (hasStatus) Modifier
                                    .background(NatureMint, CircleShape)
                                    .padding(2.dp)
                                else Modifier
                            )
                            .background(Color.Black, CircleShape)
                            .clip(CircleShape)
                            .clickable {
                                if (hasStatus) {
                                    activeStatusList = myStatus
                                } else {
                                    showStatusDialog = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val displayImage = if (hasStatus) {
                            myStatus.first().imageUrl ?: myStatus.first().userProfilePic ?: user.profilePicUrl
                        } else {
                            user.profilePicUrl
                        }

                        if (displayImage != null) {
                            AsyncImage(
                                model = displayImage,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }

                        // Small plus icon if no status
                        if (!hasStatus) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .background(NatureMint, CircleShape)
                                    .border(2.dp, Color.Black, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    Text("My Status", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                }

                // Other Statuses
                otherStatuses.forEach { (userId, userStatusList) ->
                    val statusUser = userStatusList.first()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(NatureMint, CircleShape)
                                .padding(2.dp)
                                .background(Color.Black, CircleShape)
                                .clip(CircleShape)
                                .clickable { activeStatusList = userStatusList },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = statusUser.userProfilePic ?: statusUser.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(
                            statusUser.username,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Info with Note bubble
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.TopCenter) {
                        // Note Bubble
                        val note = user.currentNote
                        if (note != null) {
                            Surface(
                                modifier = Modifier
                                    .offset(y = (-20).dp)
                                    .clickable { showNoteDialog = true },
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)
                            ) {
                                Text(
                                    text = note,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showNoteDialog = true },
                                modifier = Modifier.offset(y = (-20).dp).size(24.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Note", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }

                        // Profile Picture
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .glassmorphism(CircleShape)
                                .padding(4.dp)
                                .clickable { showPicOptions = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(color = NatureMint, modifier = Modifier.size(24.dp))
                            } else if (user.profilePicUrl != null) {
                                AsyncImage(
                                    model = user.profilePicUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = NatureMint.copy(alpha = 0.6f),
                                    modifier = Modifier.size(74.dp)
                                )
                            }
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatColumn("Posts", user.postCount.toString())
                    ProfileStatColumn("Followers", user.followersCount.toString())
                    ProfileStatColumn("Following", user.followingCount.toString())
                    ProfileStatColumn("Branches", user.branchesCount.toString())
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name, Bio, and Professional Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(user.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (user.job.isNotBlank()) {
                    Text(user.job, color = NatureMint, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(user.bio, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                
                if (user.skills.isNotBlank() || user.portfolio.isNotBlank()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.White.copy(alpha = 0.1f)
                    )
                }

                if (user.skills.isNotBlank()) {
                    Text("Skills: ${user.skills}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                if (user.portfolio.isNotBlank()) {
                    Text(
                        "Portfolio: ${user.portfolio}", 
                        color = NatureMint, 
                        fontSize = 12.sp, 
                        modifier = Modifier.clickable { /* Open Link */ }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Roots Balance: ${user.rootsBalance} 🌱", 
                    color = NatureMint, 
                    fontWeight = FontWeight.Black, 
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HubButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit Profile", color = Color.White)
                }
                HubButton(
                    onClick = { if (userChannel == null) showChannelDialog = true else showPostUpdateDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (userChannel == null) "Create Channel" else "My Channel", color = Color.White)
                }
                IconButton(
                    onClick = { showStatusDialog = true },
                    modifier = Modifier.background(NatureMint.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Status", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs and Post Grid
            var selectedTab by remember { mutableIntStateOf(0) }
            Row(modifier = Modifier.fillMaxWidth()) {
                TabItem(Icons.Default.GridView, selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                TabItem(Icons.Default.FavoriteBorder, selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
                TabItem(Icons.Default.PersonPin, selectedTab == 2, Modifier.weight(1f)) { selectedTab = 2 }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of posts
            Column {
                if (userPosts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .glassmorphism(RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No posts yet", color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    userPosts.chunked(3).forEach { rowPosts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            rowPosts.forEach { post ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(Color.White.copy(alpha = 0.05f))
                                ) {
                                    if (post.photoUrl != null) {
                                        AsyncImage(
                                            model = post.photoUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                            repeat(3 - rowPosts.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = { job, talents, portfolio, skills, qual, bio ->
                viewModel.updateProfessionalDetails(job, talents, portfolio, skills, qual, bio)
                showEditDialog = false
            }
        )
    }

    if (showNoteDialog) {
        NoteDialog(
            currentNote = user.currentNote ?: "",
            onDismiss = { showNoteDialog = false },
            onSave = { note ->
                viewModel.updateNote(note)
                showNoteDialog = false
            }
        )
    }

    if (showPicOptions) {
        AlertDialog(
            onDismissRequest = { showPicOptions = false },
            title = { Text("Profile Picture") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            imageLauncher.launch("image/*")
                            showPicOptions = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = NatureMint)
                            Spacer(Modifier.width(12.dp))
                            Text("Change Photo", color = Color.White)
                        }
                    }
                    if (user.profilePicUrl != null) {
                        TextButton(
                            onClick = {
                                viewModel.deleteProfilePicture()
                                showPicOptions = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                Spacer(Modifier.width(12.dp))
                                Text("Delete Photo", color = Color.Red)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPicOptions = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showChannelDialog) {
        CreateChannelDialog(
            onDismiss = { showChannelDialog = false },
            onSave = { name, desc ->
                viewModel.createChannel(name, desc)
                showChannelDialog = false
            }
        )
    }

    if (showPostUpdateDialog) {
        PostUpdateDialog(
            channelName = userChannel?.name ?: "Channel",
            onDismiss = { showPostUpdateDialog = false },
            onSave = { text ->
                viewModel.postToChannel(text)
                showPostUpdateDialog = false
            }
        )
    }

    if (showStatusDialog) {
        AddStatusDialog(
            onDismiss = { showStatusDialog = false },
            onPickImage = {
                statusImageLauncher.launch("image/*")
                showStatusDialog = false
            },
            onPickVideo = {
                statusVideoLauncher.launch("video/*")
                showStatusDialog = false
            },
            onPostText = { text, color, music, audience ->
                viewModel.postTextStatus(text, color, music, audience)
                showStatusDialog = false
            }
        )
    }

    if (activeStatusList != null) {
        StatusViewer(
            statuses = activeStatusList!!,
            onDismiss = { activeStatusList = null }
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
fun StatusViewer(
    statuses: List<Status>,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentStatus = statuses[currentIndex]
    val context = LocalContext.current

    // Auto-advance logic
    var isPaused by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    LaunchedEffect(currentStatus) {
        progress = 0f
        if (currentStatus.videoUrl != null) {
            val mediaItem = MediaItem.fromUri(currentStatus.videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.stop()
        }
    }

    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused) {
            val duration = if (currentStatus.videoUrl != null) 15000L else 5000L
            val startTime = System.currentTimeMillis()
            val startProgress = progress
            
            while (progress < 1f && !isPaused) {
                val elapsed = System.currentTimeMillis() - startTime
                progress = (startProgress + (elapsed.toFloat() / duration)).coerceIn(0f, 1f)
                delay(50)
            }
            
            if (progress >= 1f && !isPaused) {
                if (currentIndex < statuses.size - 1) {
                    currentIndex++
                } else {
                    onDismiss()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        if (offset.x < size.width / 3) {
                            if (currentIndex > 0) currentIndex-- else onDismiss()
                        } else {
                            if (currentIndex < statuses.size - 1) currentIndex++ else onDismiss()
                        }
                    }
                )
            }
    ) {
        // Content
        if (currentStatus.videoUrl != null) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (currentStatus.imageUrl != null) {
            AsyncImage(
                model = currentStatus.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else if (currentStatus.textContent != null) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(currentStatus.backgroundColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentStatus.textContent,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }

        // Music Overlay
        if (currentStatus.musicTitle != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = NatureMint, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(currentStatus.musicTitle, color = Color.White, fontSize = 12.sp)
            }
        }

        // Header with user info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStatus.userProfilePic != null) {
                AsyncImage(
                    model = currentStatus.userProfilePic,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
            } else {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(currentStatus.username, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Just now", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        // Progress indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            statuses.forEachIndexed { index, _ ->
                val stepProgress = when {
                    index < currentIndex -> 1f
                    index == currentIndex -> progress
                    else -> 0f
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(stepProgress)
                            .fillMaxHeight()
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

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

@Composable
fun ProfileStatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
    }
}

@Composable
fun TabItem(icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
            )
            if (isSelected) {
                Spacer(Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.White))
            }
        }
    }
}
