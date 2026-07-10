package com.ojiem.yggdrasil.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileStatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
    }
}

@Composable
fun TabItem(
    icon: ImageVector, 
    isSelected: Boolean, 
    modifier: Modifier = Modifier, 
    onClick: () -> Unit
) {
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
