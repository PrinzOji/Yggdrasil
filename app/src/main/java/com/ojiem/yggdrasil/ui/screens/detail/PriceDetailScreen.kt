package com.ojiem.yggdrasil.ui.screens.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ojiem.yggdrasil.data.model.PriceReport
import com.ojiem.yggdrasil.ui.components.HubButton
import com.ojiem.yggdrasil.ui.viewmodel.ProductViewModel
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.glassmorphism
import com.ojiem.yggdrasil.ui.theme.shiftingGradient
import com.ojiem.yggdrasil.ui.theme.ButtonGradientColors

@Composable
fun PriceDetailScreen(reportId: String, onBack: () -> Unit) {
    val productViewModel: ProductViewModel = viewModel()
    val report by productViewModel.selectedReport.collectAsState()

    LaunchedEffect(reportId) {
        productViewModel.fetchReportById(reportId)
    }

    if (report == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NatureMint)
        }
        return
    }

    val currentReport = report!!
    var isVouching by remember { mutableStateOf(false) }

    val progressAnimation by animateFloatAsState(
        targetValue = currentReport.vouchCount / 3f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "vouchProgress"
    )

    NatureBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(56.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.glassmorphism(CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Text("Node Analysis", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text(currentReport.itemName, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "KES ${"%.2f".format(currentReport.priceKes)} / ${currentReport.unit}",
                        color = NatureMint,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    DossierItem("Market Node", currentReport.marketName)
                    DossierItem("Agent Origin", currentReport.reporterName)
                    val timeAgo = remember(currentReport.createdAtMillis) {
                        val diff = System.currentTimeMillis() - currentReport.createdAtMillis
                        val hours = diff / (1000 * 60 * 60)
                        val mins = (diff / (1000 * 60)) % 60
                        if (hours > 0) "${hours}h ${mins}m ago" else "${mins}m ago"
                    }
                    DossierItem("Timestamp", timeAgo)
                }
            }

            Spacer(Modifier.height(32.dp))

            Text("Network Verification", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    if (currentReport.bloomed || currentReport.vouchCount >= 3) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = NatureMint, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("NODE BLOOMED", color = NatureMint, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("This node is community verified.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    } else {
                        LinearProgressIndicator(
                            progress = { progressAnimation },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = NatureMint,
                            trackColor = Color.White.copy(alpha = 0.1f),
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${currentReport.vouchCount} of 3 Vouches", color = Color.White, fontSize = 14.sp)
                            Text("${(progressAnimation * 100).toInt()}% Sync", color = NatureMint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(24.dp))

                        val brush = shiftingGradient(ButtonGradientColors)
                        Button(
                            onClick = { 
                                isVouching = true
                                productViewModel.vouchForReport(currentReport.id) {
                                    isVouching = false
                                }
                            },
                            enabled = !isVouching,
                            modifier = Modifier.fillMaxWidth().height(56.dp).background(brush, RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isVouching) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Filled.Shield, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Vouch for Accuracy", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DossierItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
