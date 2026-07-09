package com.ojiem.yggdrasil.ui.screens.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.NatureSeaBlue
import com.ojiem.yggdrasil.ui.theme.glassmorphism

@Composable
fun FinanceScreen() {
    NatureBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(56.dp))
            
            Text("Ecosystem Finance", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Harvesting rewards from your contributions", color = NatureMint.copy(alpha = 0.7f), fontSize = 14.sp)

            Spacer(Modifier.height(32.dp))

            // Wallet Summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = NatureMint)
                        Spacer(Modifier.width(12.dp))
                        Text("Total Harvested", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                    Text("1,250 ROOTS", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        FinanceStat("From Posts", "850")
                        FinanceStat("From Vouches", "400")
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text("Growth History", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            repeat(5) {
                FinanceHistoryItem("Broadcast Node Bonus", "+50 Roots", "2h ago")
                Spacer(Modifier.height(12.dp))
            }
            
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
fun FinanceStat(label: String, value: String) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        Text(value, color = NatureMint, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FinanceHistoryItem(title: String, amount: String, time: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = NatureMint, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(time, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                }
            }
            Text(amount, color = NatureMint, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
    }
}
