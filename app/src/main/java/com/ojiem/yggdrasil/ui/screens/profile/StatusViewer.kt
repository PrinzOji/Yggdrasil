package com.ojiem.yggdrasil.ui.screens.profile

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.ojiem.yggdrasil.data.model.Status
import com.ojiem.yggdrasil.ui.theme.NatureMint
import kotlinx.coroutines.delay

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
