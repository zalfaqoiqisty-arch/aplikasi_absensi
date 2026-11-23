package com.example.ui.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AuroraIndigo
import com.example.ui.theme.AuroraMint
import com.example.ui.theme.AuroraViolet
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.CoralError
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDeep
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassFillBottom
import com.example.ui.theme.GlassFillTop
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.MidnightElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun AuroraGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val blurMod = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.blur(70.dp)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MidnightCanvas,
                        Color(0xFF0A1128),
                        Color(0xFF0B1433),
                        MidnightCanvas
                    )
                )
            )
    ) {
        // Dedicated ambient orb layer (blur never touches foreground text/inputs)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(blurMod)
        ) {
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .offset(x = (-70).dp, y = (-50).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ElectricCyan.copy(alpha = 0.34f),
                                ElectricCyan.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 90.dp, y = 140.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AuroraIndigo.copy(alpha = 0.32f),
                                AuroraViolet.copy(alpha = 0.09f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-50).dp, y = 70.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AuroraMint.copy(alpha = 0.22f),
                                ElectricCyanDeep.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassFillTop,
                        GlassFillBottom
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlassBorderHighlight,
                        GlassBorderSubtle,
                        ElectricCyan.copy(alpha = 0.28f)
                    )
                ),
                shape = shape
            )
            .padding(contentPadding)
    ) {
        Column(content = content)
    }
}

@Composable
fun GlassSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerShape = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, GlassBorderSubtle, containerShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val bgColor by animateColorAsState(
                targetValue = if (selected) ElectricCyan.copy(alpha = 0.24f) else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "segmentBg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (selected) ElectricCyan.copy(alpha = 0.65f) else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "segmentBorder"
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) GlassTextPrimary else GlassTextMuted,
                label = "segmentText"
            )
            val itemShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clip(itemShape)
                    .background(bgColor)
                    .border(1.dp, borderColor, itemShape)
                    .clickable { onSelect(index) }
                    .testTag("segment_option_$index"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            placeholder = if (placeholder.isNotBlank()) {
                {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GlassTextMuted.copy(alpha = 0.7f)
                    )
                }
            } else null,
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isError) CoralError else ElectricCyan.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon,
            isError = isError,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                disabledContainerColor = Color.White.copy(alpha = 0.03f),
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.22f),
                errorBorderColor = CoralError,
                focusedLabelColor = ElectricCyan,
                unfocusedLabelColor = GlassTextSecondary,
                focusedTextColor = GlassTextPrimary,
                unfocusedTextColor = GlassTextPrimary,
                cursorColor = ElectricCyan
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && !errorText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = CoralError,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun GlassPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.White.copy(alpha = 0.10f),
            contentColor = GlassTextPrimary,
            disabledContentColor = GlassTextMuted
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp)
                .background(
                    brush = if (enabled && !isLoading) {
                        Brush.horizontalGradient(
                            colors = listOf(ElectricCyanDeep, Color(0xFF4F46E5))
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.12f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        )
                    },
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color = if (enabled) ElectricCyan.copy(alpha = 0.65f) else GlassBorderSubtle,
                    shape = shape
                )
                .padding(horizontal = 20.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = GlassTextPrimary,
                    strokeWidth = 2.2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = GlassTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = GlassTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun GlassSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tint: Color = GlassTextPrimary
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White.copy(alpha = 0.07f),
            contentColor = tint
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun GlassStatusBanner(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isError) CoralContainer.copy(alpha = 0.85f) else Color(0xFF064E3B).copy(alpha = 0.82f)
    val borderColor = if (isError) CoralError.copy(alpha = 0.7f) else AuroraMint.copy(alpha = 0.7f)
    val icon = if (isError) Icons.Rounded.ErrorOutline else Icons.Rounded.CheckCircle
    val iconTint = if (isError) CoralError else AuroraMint

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = GlassTextPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Tutup pesan",
                    tint = GlassTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassBirthDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                        formatter.timeZone = TimeZone.getTimeZone("UTC")
                        onDateSelected(formatter.format(Date(millis)))
                    }
                    onDismiss()
                }
            ) {
                Text("Pilih", color = ElectricCyan)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = GlassTextSecondary)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
