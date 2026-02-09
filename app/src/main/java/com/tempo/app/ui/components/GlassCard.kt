/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tempo.app.ui.theme.TempoDesign

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = TempoDesign.palette
    val supportsBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .then(
                if (supportsBlur) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier.graphicsLayer {
                            renderEffect = RenderEffect
                                .createBlurEffect(14f, 14f, Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    } else Modifier
                } else {
                    Modifier.shadow(2.dp, shape)
                }
            )
            .clip(shape)
            .background(if (supportsBlur) palette.glass else palette.glassFallback)
            .border(BorderStroke(1.dp, palette.border), shape),
        content = content
    )
}
