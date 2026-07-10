package com.ojiem.yggdrasil.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.ojiem.yggdrasil.data.model.PriceReport
import com.ojiem.yggdrasil.data.model.defaultCategories
import com.ojiem.yggdrasil.ui.components.HubButton
import com.ojiem.yggdrasil.ui.viewmodel.ProductViewModel
import com.ojiem.yggdrasil.ui.navigation.*
import com.ojiem.yggdrasil.ui.theme.*

@Composable
fun HomeFeedScreen(
    onReportClick: (String) -> Unit,
    onRealmsClick: () -> Unit
) {
    val productViewModel: ProductViewModel = viewModel()
    val reports by productViewModel.reports.collectAsState()
    val isRefreshing by productViewModel.isRefreshing.collectAsState()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filteredReports = remember(reports, selectedCategory) {
        if (selectedCategory == null) reports else reports.filter { it.category == selectedCategory }
    }

    NatureBackground {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(56.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("The World Tree", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Cursive)
                    Text("Synchronizing Eco Nodes", color = NatureMint.copy(alpha = 0.7f), fontSize = 14.sp)
                }
                IconButton(
                    onClick = { productViewModel.refreshReports() },
                    modifier = Modifier.glassmorphism(RoundedCornerShape(12.dp))
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = onRealmsClick,
                        label = { Text("All Realms", color = if (selectedCategory == null) Color.Black else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NatureMint,
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedCategory == null, borderColor = NatureMint.copy(alpha = 0.5f))
                    )
                }
                items(defaultCategories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat.id,
                        onClick = { selectedCategory = cat.id },
                        label = { Text("${cat.emoji} ${cat.label}", color = if (selectedCategory == cat.id) Color.Black else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NatureMint,
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedCategory == cat.id, borderColor = NatureMint.copy(alpha = 0.5f))
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (isRefreshing && filteredReports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NatureMint)
                }
            } else if (filteredReports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No eco nodes detected 🌱", color = Color.White.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredReports) { report ->
                        PriceReportCard(report = report, onClick = { onReportClick(report.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun PriceReportCard(report: PriceReport, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            if (report.photoUrl != null) {
                AsyncImage(
                    model = report.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(report.itemName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "KES ${"%.2f".format(report.priceKes)}",
                    color = NatureSkyBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text("per ${report.unit}", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            
            Spacer(Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn, 
                    contentDescription = null,
                    tint = NatureMint.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(report.marketName, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "By ${report.reporterName}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
                if (report.bloomed) {
                    Surface(
                        color = NatureForestGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NatureForestGreen.copy(alpha = 0.5f))
                    ) {
                        Text(
                            "BLOOMED 🌿", 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = NatureForestGreen, 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        "${report.vouchCount}/3 Vouches",
                        color = Color.Yellow.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            HubButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Analyze Node", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFeedPreview() {
    HomeFeedScreen(onReportClick = {}, onRealmsClick = {})
}
