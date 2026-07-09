package com.ojiem.yggdrasil.ui.screens.report

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.ojiem.yggdrasil.data.model.defaultCategories
import com.ojiem.yggdrasil.ui.components.HubButton
import com.ojiem.yggdrasil.ui.components.HubTextField
import com.ojiem.yggdrasil.ui.theme.*
import com.ojiem.yggdrasil.ui.viewmodel.ProductViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPriceScreen(onSubmitted: () -> Unit) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel()
    
    var itemName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(defaultCategories.first().id) }
    var price by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var marketName by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    NatureBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))
            
            Text(
                "Plant Price Node", 
                color = Color.White, 
                fontSize = 32.sp, 
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Expand the branches of the network", 
                color = NatureMint.copy(alpha = 0.7f), 
                fontSize = 14.sp
            )

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(RoundedCornerShape(32.dp))
                    .padding(20.dp)
            ) {
                Column {
                    // Image Picker
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri == null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = NatureMint.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                                Text("Add Photo", color = NatureMint.copy(alpha = 0.5f))
                            }
                        } else {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    HubTextField(value = itemName, onValueChange = { itemName = it }, label = "Item Name", icon = Icons.Default.Sell)
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Text("Category Realm", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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

                    Spacer(Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1.5f)) {
                            HubTextField(value = price, onValueChange = { price = it }, label = "Price (KES)", icon = Icons.Default.PriceChange)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            HubTextField(value = unit, onValueChange = { unit = it }, label = "Unit", icon = Icons.Default.AddBusiness)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    HubTextField(value = marketName, onValueChange = { marketName = it }, label = "Retail Location", icon = Icons.Default.Storefront)
                }
            }

            Spacer(Modifier.height(40.dp))

            HubButton(
                onClick = {
                    productViewModel.reportPrice(
                        context = context,
                        itemName = itemName,
                        category = selectedCategory,
                        price = price,
                        unit = unit,
                        marketName = marketName,
                        imageUri = imageUri,
                        onSuccess = onSubmitted
                    )
                },
                enabled = !productViewModel.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (productViewModel.isSubmitting) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text(productViewModel.uploadStatus, color = Color.White, fontSize = 14.sp)
                    }
                } else {
                    Text("Broadcast Price Node 🌱", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))
            
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Decentralized Verification: This node will bloom and earn you Roots once 3 other agents vouch for its accuracy.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = NatureMint.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
