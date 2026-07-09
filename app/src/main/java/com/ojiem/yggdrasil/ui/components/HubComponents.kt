package com.ojiem.yggdrasil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.GlassTextSecondary
import com.ojiem.yggdrasil.ui.theme.shiftingGradient
import com.ojiem.yggdrasil.ui.theme.ButtonGradientColors

@Composable
fun HubTextField(
    value: String, 
    onValueChange: (String) -> Unit, 
    label: String, 
    icon: ImageVector, 
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val brush = shiftingGradient(ButtonGradientColors)
    val shape = RoundedCornerShape(16.dp)
    
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                text = label, 
                color = if (isFocused) Color.White else GlassTextSecondary 
            ) 
        },
        leadingIcon = { 
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (isFocused) Color.White else Color.White.copy(alpha = 0.6f) 
            ) 
        },
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                brush = if (isFocused) brush else Brush.linearGradient(listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.15f))),
                shape = shape
            ),
        shape = shape,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = GlassTextSecondary
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        interactionSource = interactionSource,
        singleLine = true
    )
}

@Composable
fun HubButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val brush = shiftingGradient(ButtonGradientColors)
    val shape = RoundedCornerShape(16.dp)
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .background(brush, shape),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = shape,
        content = content
    )
}
