/*
 * Tempo - Seasonal Calendar App
 * Copyright (C) 2026 Tempo Contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.tempo.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.tempo.app.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val SoraFontName = GoogleFont("Sora")

val SoraFontFamily = FontFamily(
    Font(googleFont = SoraFontName, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = SoraFontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = SoraFontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = SoraFontName, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = SoraFontName, fontProvider = provider, weight = FontWeight.Bold),
)

@Immutable
data class TempoTypography(
    val screenTitle: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.03).em
    ),
    val sectionHeader: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.03).em
    ),
    val eventTitle: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    ),
    val eventTime: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    ),
    val locationChip: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    ),
    val label: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.05.em
    ),
    val sectionLabel: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.08.em
    ),
    val weekDayLetter: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.02.em
    ),
    val greeting: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    ),
    val seasonTag: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em
    ),
    val bottomNavLabel: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.01.em
    ),
    val nowBadge: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    ),
    val body: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    ),
    val bodyMedium: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    ),
    val bodySemiBold: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    ),
    val button: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    ),
    val sheetTitle: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.02).em
    ),
    val detailTitle: TextStyle = TextStyle(
        fontFamily = SoraFontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.02).em
    )
)

val LocalTempoTypography = staticCompositionLocalOf { TempoTypography() }
