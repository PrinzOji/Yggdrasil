package com.ojiem.yggdrasil.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.ojiem.yggdrasil.data.model.User
import com.ojiem.yggdrasil.ui.viewmodel.LeaderboardViewModel
import com.ojiem.yggdrasil.ui.viewmodel.ProfileViewModel
import com.ojiem.yggdrasil.ui.theme.*

@Composable
fun LeaderboardScreen() {
    val viewModel: LeaderboardViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val topUsers by viewModel.topUsers.collectAsState()
    val statuses by profileViewModel.statuses.collectAsState()

    NatureBackground {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(56.dp))
            
            Text(
                "Hall of Seers", 
                color = Color.White, 
                fontSize = 40.sp, 
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "High-integrity network nodes", 
                color = NatureMint.copy(alpha = 0.7f), 
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                itemsIndexed(topUsers) { index, user ->
                    val hasStatus = statuses.any { it.userId == user.uid }
                    SeerCard(index + 1, user, hasStatus)
                }
            }
        }
    }
}

@Composable
fun SeerCard(rank: Int, user: User, hasStatus: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(RoundedCornerShape(24.dp))
            .background(GlassContainer, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank Circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            when(rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> NatureMint.copy(alpha = 0.1f)
                            }.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "#$rank", 
                        color = when(rank) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> Color.White
                        }, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Black
                    )
                }
                
                Spacer(Modifier.width(12.dp))

                // Profile Image with Status Ring
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .then(
                            if (hasStatus) Modifier.border(2.dp, NatureMint, CircleShape).padding(3.dp)
                            else Modifier
                        )
                ) {
                    if (user.profilePicUrl != null) {
                        AsyncImage(
                            model = user.profilePicUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Text(user.username.take(1).uppercase(), color = Color.White)
                        }
                    }
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.username, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            if (user.isPrivate) Icons.Default.Lock else Icons.Default.Public,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        "${user.followersCount} Followers • ${user.followingCount} Following",
                        color = GlassTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("${user.rootsBalance}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("ROOTS", color = GlassTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
