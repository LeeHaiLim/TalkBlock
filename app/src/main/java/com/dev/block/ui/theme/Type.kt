package com.dev.block.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp
    )
)

object BlockTypo {
    @Composable
    fun warning() = TextStyle(
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp
    )

    val textButton = TextStyle(
        fontSize = 12.sp,
        textDecoration = TextDecoration.Underline
    )
}



