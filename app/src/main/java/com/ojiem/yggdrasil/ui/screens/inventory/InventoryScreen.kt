package com.ojiem.yggdrasil.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojiem.yggdrasil.data.model.InventoryItem
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.glassmorphism

@Composable
fun InventoryScreen() {
    val items = remember {
        listOf(
            InventoryItem("1", "Organic Honey", 45, 12, "2025-12-01", 8),
            InventoryItem("2", "Seer Seeds", 120, 5, "2026-06-15", 25),
            InventoryItem("3", "Ecosystem Guide", 15, 2, "N/A", 1)
        )
    }

    NatureBackground {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(56.dp))
            
            Text("Node Inventory", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Manage your stock and fulfill ecosystem needs", color = NatureMint.copy(alpha = 0.7f), fontSize = 14.sp)

            Spacer(Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(items) { item ->
                    InventoryCard(item)
                }
            }
        }
    }
}

@Composable
fun InventoryCard(item: InventoryItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(item.itemName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = NatureMint.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NatureMint.copy(alpha = 0.3f))
                ) {
                    Text(
                        "${item.stockCount} ${item.unit} left", 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = NatureMint, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InventoryStat(Icons.Default.ShoppingCart, "${item.ordersCount} Orders")
                InventoryStat(Icons.Default.Alarm, "Exp: ${item.expiryDate}")
                InventoryStat(Icons.Default.Inventory, "${item.needByUsersCount} Needs")
            }
        }
    }
}

@Composable
fun InventoryStat(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
    }
}
