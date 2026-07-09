package com.ojiem.yggdrasil.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ojiem.yggdrasil.data.model.LibraryItem
import com.ojiem.yggdrasil.data.model.LibraryItemType
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.glassmorphism

@Composable
fun LibraryScreen() {
    val items = remember {
        listOf(
            LibraryItem("1", "Organic Farming 101", "Learn the basics of organic farming.", LibraryItemType.VIDEO, "url", "Nature Agent", "Farming"),
            LibraryItem("2", "Soil Health Guide", "A comprehensive book on soil nutrition.", LibraryItemType.BOOK, "url", "Earth Guardian", "Soil"),
            LibraryItem("3", "Water Conservation Tip", "Mulching saves water by up to 30%.", LibraryItemType.TIP, "url", "Aqua Sage", "Water")
        )
    }

    NatureBackground {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(56.dp))
            
            Text("Knowledge Library", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Expand your wisdom in the Yggdrasil ecosystem", color = NatureMint.copy(alpha = 0.7f), fontSize = 14.sp)

            Spacer(Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(items) { item ->
                    LibraryCard(item)
                }
            }
        }
    }
}

@Composable
fun LibraryCard(item: LibraryItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(NatureMint.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(item.type) {
                        LibraryItemType.VIDEO -> Icons.Default.PlayCircle
                        LibraryItemType.BOOK -> Icons.Default.Book
                        LibraryItemType.TIP -> Icons.Default.Lightbulb
                    },
                    contentDescription = null,
                    tint = NatureMint,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column {
                Text(item.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(item.description, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(item.type.name, color = NatureMint, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("by ${item.author}", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                }
            }
        }
    }
}
