package com.ojiem.yggdrasil.ui.theme

import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import com.ojiem.yggdrasil.R
import kotlinx.coroutines.delay

val ButtonGradientColors = listOf(NatureForestGreen, NatureSkyBlue)

@Composable
fun shiftingGradient(colors: List<Color>, durationMillis: Int = 8000): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "progress",
    )

    val animatedColors = colors.mapIndexed { index, color ->
        val targetIndex = (index + 1) % colors.size
        infiniteTransition.animateColor(
            initialValue = color,
            targetValue = colors[targetIndex],
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "color_$index",
        ).value
    }

    return Brush.linearGradient(
        colors = animatedColors,
        start = Offset(progress * 1000f, 0f),
        end = Offset((1f - progress) * 1000f, 1000f),
        tileMode = TileMode.Mirror,
    )
}

@Composable
fun Modifier.glassmorphism(
    shape: Shape,
    containerColor: Color = GlassContainer,
    borderColor: Color = GlassBorder
) = this
    .background(containerColor, shape)
    .border(1.dp, borderColor, shape)

@Composable
fun TheHubBackground(content: @Composable () -> Unit) {
    NatureBackground(content)
}

@Composable
fun NatureBackground(content: @Composable () -> Unit) {
    // Background Music
    BackgroundMusicPlayer()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        ImageSlideshow()
        content()
    }
}

@Composable
fun BackgroundMusicPlayer() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yggdrasil_prefs", android.content.Context.MODE_PRIVATE) }
    var isEnabled by remember { mutableStateOf(prefs.getBoolean("music_enabled", true)) }

    val musicTracks = remember {
        listOf(
            R.raw.fassounds_lofi_study_calm_peaceful_chill_hop_112191,
            R.raw.joackkenny_sunset_next_to_you_464865,
            R.raw.moodmode_sakura_meditate_beat_138298,
            R.raw.music_for_videos_coffee_lounge_145030,
            R.raw.penguinmusic_deep_lo_fi_hip_hop_143255
        )
    }

    var currentTrackIndex by remember { mutableIntStateOf(musicTracks.indices.random()) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Update isEnabled from SharedPreferences
    DisposableEffect(context) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { p, key ->
            if (key == "music_enabled") {
                isEnabled = p.getBoolean(key, true)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    // Effect to switch song every 60 seconds
    LaunchedEffect(isEnabled) {
        while (isEnabled) {
            delay(60000) // 1 minute
            currentTrackIndex = (currentTrackIndex + 1) % musicTracks.size
        }
    }

    // Effect to handle MediaPlayer lifecycle
    LaunchedEffect(currentTrackIndex, isEnabled) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        if (isEnabled) {
            try {
                mediaPlayer = MediaPlayer.create(context, musicTracks[currentTrackIndex]).apply {
                    isLooping = true
                    setVolume(0.3f, 0.3f)
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }
}

@Composable
fun ImageSlideshow() {
    val images = listOf(
        R.drawable.yggdrasil3,
        R.drawable.yggdrasil4,
        R.drawable.partnership,
        R.drawable.co_operation,
        R.drawable.business_growth,
        R.drawable.app_of_development,
        R.drawable.growth,
        R.drawable.develop,
        R.drawable.knowledge,
        R.drawable.knowledge2,
        R.drawable.socialize
    )
    
    var currentImageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(8000)
            currentImageIndex = (currentImageIndex + 1) % images.size
        }
    }

    AnimatedContent(
        targetState = images[currentImageIndex],
        transitionSpec = {
            fadeIn(animationSpec = tween(3000)) togetherWith fadeOut(animationSpec = tween(3000))
        },
        label = "slideshow",
        modifier = Modifier.fillMaxSize()
    ) { targetImage ->
        Image(
            painter = painterResource(id = targetImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.15f),
            contentScale = ContentScale.Crop,
            alpha = 0.65f
        )
    }
}
