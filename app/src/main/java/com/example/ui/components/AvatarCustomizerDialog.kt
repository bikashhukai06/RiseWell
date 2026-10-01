package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserAvatar

@Composable
fun AvatarCustomizerDialog(
    initialAvatar: UserAvatar,
    currentLevel: Int,
    onDismiss: () -> Unit,
    onSaveAvatar: (UserAvatar) -> Unit
) {
    var avatarState by remember { mutableStateOf(initialAvatar) }

    val skinTones = listOf(
        "#FFDBAC" to "Fair",
        "#F1C27D" to "Peach",
        "#E0AC69" to "Golden",
        "#C68642" to "Caramel",
        "#8D5524" to "Bronze",
        "#3B2219" to "Espresso"
    )

    val hairStyles = listOf("Sporty", "Spiky", "Ponytail", "Beanie")
    val hairColors = listOf(
        "#2D3748" to "Onyx",
        "#78350F" to "Chestnut",
        "#F59E0B" to "Golden",
        "#3B82F6" to "Electric Blue",
        "#EC4899" to "Neon Pink"
    )

    val accessories = listOf("Headphones", "Crown", "Sport Glasses", "Sweatband", "Halo", "None")

    val bgGradients = listOf(
        Pair("#6366F1", "#8B5CF6") to "Cosmic Indigo",
        Pair("#F59E0B", "#EF4444") to "Sunset Blaze",
        Pair("#10B981", "#06B6D4") to "Emerald Aurora",
        Pair("#EC4899", "#8B5CF6") to "Neon Twilight",
        Pair("#0F172A", "#334155") to "Midnight Stealth"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 16.dp)
                .testTag("avatar_customizer_dialog"),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Customize Avatar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_avatar_customizer_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Live Avatar Preview Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AvatarView(
                                avatar = avatarState,
                                size = 110.dp,
                                showLevelBadge = true,
                                level = currentLevel
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Live Preview",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Preset Quick Pick
                    SectionTitle(title = "Hero Presets")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(
                            Triple("runner", "Runner", Triple("#FFDBAC", "Sporty", "Headphones")),
                            Triple("yogi", "Yogi", Triple("#E0AC69", "Ponytail", "Halo")),
                            Triple("champion", "Champion", Triple("#F1C27D", "Spiky", "Crown")),
                            Triple("cyber", "Cyber", Triple("#C68642", "Beanie", "Sport Glasses"))
                        )

                        presets.forEach { (presetId, label, attrs) ->
                            val isSelected = avatarState.presetId == presetId
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    avatarState = avatarState.copy(
                                        presetId = presetId,
                                        skinToneHex = attrs.first,
                                        hairStyle = attrs.second,
                                        accessory = attrs.third
                                    )
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier.testTag("preset_chip_$presetId")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Skin Tone Selector
                    SectionTitle(title = "Skin Tone")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        skinTones.forEach { (hex, _) ->
                            val isSelected = avatarState.skinToneHex.equals(hex, ignoreCase = true)
                            val color = parseColor(hex, Color.LightGray)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        avatarState = avatarState.copy(skinToneHex = hex)
                                    }
                                    .testTag("skin_tone_$hex"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hair Style Selector
                    SectionTitle(title = "Hair Style")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        hairStyles.forEach { style ->
                            val isSelected = avatarState.hairStyle == style
                            FilterChip(
                                selected = isSelected,
                                onClick = { avatarState = avatarState.copy(hairStyle = style) },
                                label = { Text(style, fontSize = 12.sp) },
                                modifier = Modifier.testTag("hair_style_$style")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hair Color Selector
                    SectionTitle(title = "Hair Color")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        hairColors.forEach { (hex, label) ->
                            val isSelected = avatarState.hairColorHex.equals(hex, ignoreCase = true)
                            val color = parseColor(hex, Color.DarkGray)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        avatarState = avatarState.copy(hairColorHex = hex)
                                    }
                                    .testTag("hair_color_$label"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Accessories
                    SectionTitle(title = "Wellness Gear & Accessories")
                    accessories.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { acc ->
                                val isSelected = avatarState.accessory == acc
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { avatarState = avatarState.copy(accessory = acc) },
                                    label = { Text(acc, fontSize = 12.sp) },
                                    modifier = Modifier.testTag("accessory_$acc")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Background Gradient Theme
                    SectionTitle(title = "Aura Background")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        bgGradients.forEach { (colors, name) ->
                            val (startHex, endHex) = colors
                            val isSelected = avatarState.bgGradientStart.equals(startHex, ignoreCase = true)
                            val start = parseColor(startHex, Color.Blue)
                            val end = parseColor(endHex, Color.Magenta)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(start, end)))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        avatarState = avatarState.copy(
                                            bgGradientStart = startHex,
                                            bgGradientEnd = endHex
                                        )
                                    }
                                    .testTag("bg_gradient_$name"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("cancel_avatar_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onSaveAvatar(avatarState)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("save_avatar_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Save Avatar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
