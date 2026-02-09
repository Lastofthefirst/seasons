/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.tempo.app.ui.theme.TempoDesign

enum class NavTab(val label: String) {
    TODAY("Today"),
    WEEK("Week"),
    MONTH("Month"),
    SETTINGS("Settings")
}

@Composable
fun BottomNavBar(
    activeTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = TempoDesign.palette
    val typography = TempoDesign.typography

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect = RenderEffect
                            .createBlurEffect(24f, 24f, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
                } else Modifier
            )
            .background(palette.glass.copy(alpha = 0.85f))
            .drawBehind {
                drawLine(
                    color = palette.border,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(top = 6.dp, bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavTab.entries.forEach { tab ->
                val isActive = activeTab == tab
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    NavIcon(tab = tab, isActive = isActive, palette = palette)
                    BasicText(
                        text = tab.label,
                        style = typography.bottomNavLabel.copy(
                            fontWeight = if (isActive) androidx.compose.ui.text.font.FontWeight.SemiBold
                            else androidx.compose.ui.text.font.FontWeight.Normal,
                            color = if (isActive) palette.accent else palette.textMuted
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun NavIcon(tab: NavTab, isActive: Boolean, palette: com.tempo.app.ui.theme.TempoPalette) {
    val color = if (isActive) palette.accent else palette.textMuted
    val size = 24.dp

    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(size)
    ) {
        val s = this.size
        val strokeWidth = 1.8.dp.toPx()

        when (tab) {
            NavTab.TODAY -> {
                // Calendar with dot
                drawRoundRect(
                    color = color,
                    topLeft = Offset(s.width * 0.125f, s.height * 0.167f),
                    size = androidx.compose.ui.geometry.Size(s.width * 0.75f, s.height * 0.708f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                    style = Stroke(strokeWidth)
                )
                drawLine(color, Offset(s.width * 0.125f, s.height * 0.375f), Offset(s.width * 0.875f, s.height * 0.375f), strokeWidth)
                drawLine(color, Offset(s.width * 0.333f, s.height * 0.083f), Offset(s.width * 0.333f, s.height * 0.208f), strokeWidth)
                drawLine(color, Offset(s.width * 0.667f, s.height * 0.083f), Offset(s.width * 0.667f, s.height * 0.208f), strokeWidth)
                if (isActive) {
                    drawCircle(color, 2.dp.toPx(), Offset(s.width * 0.5f, s.height * 0.625f))
                }
            }
            NavTab.WEEK -> {
                // Three lines
                drawLine(color, Offset(s.width * 0.167f, s.height * 0.25f), Offset(s.width * 0.833f, s.height * 0.25f), strokeWidth)
                drawLine(color, Offset(s.width * 0.167f, s.height * 0.5f), Offset(s.width * 0.833f, s.height * 0.5f), strokeWidth)
                drawLine(color, Offset(s.width * 0.167f, s.height * 0.75f), Offset(s.width * 0.583f, s.height * 0.75f), strokeWidth)
                if (isActive) {
                    drawCircle(color, 2.5.dp.toPx(), Offset(s.width * 0.833f, s.height * 0.75f))
                }
            }
            NavTab.MONTH -> {
                // Four squares
                val gap = 2.dp.toPx()
                val rectSize = (s.width - gap * 3) / 2
                val positions = listOf(
                    Offset(gap, gap),
                    Offset(gap * 2 + rectSize, gap),
                    Offset(gap, gap * 2 + rectSize),
                    Offset(gap * 2 + rectSize, gap * 2 + rectSize)
                )
                positions.forEach { pos ->
                    drawRoundRect(
                        color = color,
                        topLeft = pos,
                        size = androidx.compose.ui.geometry.Size(rectSize, rectSize),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()),
                        style = Stroke(strokeWidth)
                    )
                }
            }
            NavTab.SETTINGS -> {
                // Gear
                drawCircle(color, s.width * 0.125f, Offset(s.width * 0.5f, s.height * 0.5f), style = Stroke(strokeWidth))
                val spokes = 8
                for (i in 0 until spokes) {
                    val angle = Math.toRadians(i * 45.0)
                    val innerR = s.width * 0.2f
                    val outerR = s.width * 0.42f
                    drawLine(
                        color,
                        Offset(s.width / 2 + (innerR * Math.cos(angle)).toFloat(), s.height / 2 + (innerR * Math.sin(angle)).toFloat()),
                        Offset(s.width / 2 + (outerR * Math.cos(angle)).toFloat(), s.height / 2 + (outerR * Math.sin(angle)).toFloat()),
                        strokeWidth
                    )
                }
            }
        }
    }
}
