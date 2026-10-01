package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAvatar

@Composable
fun AvatarView(
    avatar: UserAvatar,
    size: Dp = 96.dp,
    showLevelBadge: Boolean = false,
    level: Int = 1,
    modifier: Modifier = Modifier
) {
    val skinColor = parseColor(avatar.skinToneHex, Color(0xFFFFDBAC))
    val hairColor = parseColor(avatar.hairColorHex, Color(0xFF2D3748))
    val bgStart = parseColor(avatar.bgGradientStart, Color(0xFF6366F1))
    val bgEnd = parseColor(avatar.bgGradientEnd, Color(0xFF8B5CF6))

    Box(
        modifier = modifier
            .size(size)
            .testTag("avatar_view_${avatar.presetId}"),
        contentAlignment = Alignment.Center
    ) {
        // Gradient Background Circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(bgStart, bgEnd)))
                .border(2.5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )

        // Custom Canvas drawing for stylized avatar face, hair, and expression
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.85f)
                .clip(CircleShape)
        ) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            val centerX = canvasW / 2f
            val centerY = canvasH / 2f

            // Torso / Neck
            drawRoundRect(
                color = skinColor,
                topLeft = Offset(centerX - canvasW * 0.16f, centerY + canvasH * 0.18f),
                size = Size(canvasW * 0.32f, canvasH * 0.28f),
                cornerRadius = CornerRadius(canvasW * 0.08f, canvasW * 0.08f)
            )

            // Shoulders / Shirt
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(centerX - canvasW * 0.42f, centerY + canvasH * 0.36f),
                size = Size(canvasW * 0.84f, canvasH * 0.40f),
                cornerRadius = CornerRadius(canvasW * 0.16f, canvasW * 0.16f)
            )

            // Head (Oval)
            val headRadiusX = canvasW * 0.26f
            val headRadiusY = canvasH * 0.30f
            val headCenterY = centerY - canvasH * 0.02f
            drawOval(
                color = skinColor,
                topLeft = Offset(centerX - headRadiusX, headCenterY - headRadiusY),
                size = Size(headRadiusX * 2, headRadiusY * 2)
            )

            // Hair
            when (avatar.hairStyle) {
                "Spiky" -> {
                    drawArc(
                        color = hairColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(centerX - headRadiusX * 1.1f, headCenterY - headRadiusY * 1.35f),
                        size = Size(headRadiusX * 2.2f, headRadiusY * 1.5f)
                    )
                }
                "Ponytail" -> {
                    // Main Hair
                    drawArc(
                        color = hairColor,
                        startAngle = 170f,
                        sweepAngle = 200f,
                        useCenter = true,
                        topLeft = Offset(centerX - headRadiusX * 1.05f, headCenterY - headRadiusY * 1.15f),
                        size = Size(headRadiusX * 2.1f, headRadiusY * 1.25f)
                    )
                    // High ponytail side tuft
                    drawCircle(
                        color = hairColor,
                        radius = canvasW * 0.12f,
                        center = Offset(centerX + headRadiusX * 0.9f, headCenterY - headRadiusY * 0.6f)
                    )
                }
                "Beanie" -> {
                    drawRoundRect(
                        color = Color(0xFFE11D48),
                        topLeft = Offset(centerX - headRadiusX * 1.1f, headCenterY - headRadiusY * 1.2f),
                        size = Size(headRadiusX * 2.2f, headRadiusY * 0.95f),
                        cornerRadius = CornerRadius(canvasW * 0.1f, canvasW * 0.1f)
                    )
                }
                else -> { // Sporty / Sleek
                    drawArc(
                        color = hairColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(centerX - headRadiusX * 1.05f, headCenterY - headRadiusY * 1.2f),
                        size = Size(headRadiusX * 2.1f, headRadiusY * 1.2f)
                    )
                }
            }

            // Eyes
            val eyeSpacing = headRadiusX * 0.45f
            val eyeCenterY = headCenterY - canvasH * 0.02f
            drawCircle(
                color = Color(0xFF1E293B),
                radius = canvasW * 0.032f,
                center = Offset(centerX - eyeSpacing, eyeCenterY)
            )
            drawCircle(
                color = Color(0xFF1E293B),
                radius = canvasW * 0.032f,
                center = Offset(centerX + eyeSpacing, eyeCenterY)
            )

            // Sparkle in eyes
            drawCircle(
                color = Color.White,
                radius = canvasW * 0.012f,
                center = Offset(centerX - eyeSpacing - 1.5f, eyeCenterY - 1.5f)
            )
            drawCircle(
                color = Color.White,
                radius = canvasW * 0.012f,
                center = Offset(centerX + eyeSpacing - 1.5f, eyeCenterY - 1.5f)
            )

            // Warm Smile
            drawArc(
                color = Color(0xFF991B1B),
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(centerX - canvasW * 0.10f, headCenterY + canvasH * 0.08f),
                size = Size(canvasW * 0.20f, canvasH * 0.10f),
                style = Stroke(width = canvasW * 0.025f)
            )

            // Cheeks blush
            drawCircle(
                color = Color(0x33FF6B6B),
                radius = canvasW * 0.045f,
                center = Offset(centerX - eyeSpacing * 1.25f, eyeCenterY + canvasH * 0.06f)
            )
            drawCircle(
                color = Color(0x33FF6B6B),
                radius = canvasW * 0.045f,
                center = Offset(centerX + eyeSpacing * 1.25f, eyeCenterY + canvasH * 0.06f)
            )

            // Optional accessory rendering on canvas
            if (avatar.accessory == "Sweatband") {
                drawRoundRect(
                    color = Color(0xFF06B6D4),
                    topLeft = Offset(centerX - headRadiusX * 0.95f, headCenterY - headRadiusY * 0.7f),
                    size = Size(headRadiusX * 1.9f, canvasH * 0.08f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            } else if (avatar.accessory == "Halo") {
                drawOval(
                    color = Color(0xFFFBBF24),
                    topLeft = Offset(centerX - headRadiusX * 0.8f, headCenterY - headRadiusY * 1.5f),
                    size = Size(headRadiusX * 1.6f, canvasH * 0.10f),
                    style = Stroke(width = canvasW * 0.035f)
                )
            }
        }

        // Overlay Icon Accessory if Headphones or Glasses or Crown
        when (avatar.accessory) {
            "Headphones" -> {
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = "Headphones",
                    tint = Color.White.copy(alpha = 0.92f),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = size * 0.06f)
                        .size(size * 0.32f)
                )
            }
            "Crown" -> {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Crown",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                        .size(size * 0.28f)
                )
            }
            "Sport Glasses" -> {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Glasses",
                    tint = Color(0xFF0F172A).copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = size * 0.02f)
                        .size(size * 0.26f)
                )
            }
        }

        // Level Badge Pill
        if (showLevelBadge) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .testTag("avatar_level_badge"),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Lv $level",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

fun parseColor(hex: String, default: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        default
    }
}
