package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

// Custom Palettes Definitions
private val NordicForestDark = darkColorScheme(
    primary = Color(0xFFA3E2C9),
    secondary = Color(0xFFB5D4C4),
    tertiary = Color(0xFF90D2D8),
    surfaceVariant = Color(0xFF3F4943),
    onSurfaceVariant = Color(0xFFC0C9C1)
)
private val NordicForestLight = lightColorScheme(
    primary = Color(0xFF006C4C),
    secondary = Color(0xFF4C6356),
    tertiary = Color(0xFF3B6468),
    surfaceVariant = Color(0xFFDBE5DD),
    onSurfaceVariant = Color(0xFF404943)
)

private val CrimsonEclipseDark = darkColorScheme(
    primary = Color(0xFFFFB4AC),
    secondary = Color(0xFFE7BDB7),
    tertiary = Color(0xFFE5BC90),
    surfaceVariant = Color(0xFF534341),
    onSurfaceVariant = Color(0xFFD8C2BF)
)
private val CrimsonEclipseLight = lightColorScheme(
    primary = Color(0xFFBA1A1A),
    secondary = Color(0xFF775652),
    tertiary = Color(0xFF715B29),
    surfaceVariant = Color(0xFFF5DDDA),
    onSurfaceVariant = Color(0xFF534341)
)

private val OceanMistDark = darkColorScheme(
    primary = Color(0xFF94CCFF),
    secondary = Color(0xFFBAC8DB),
    tertiary = Color(0xFF8AD2E9),
    surfaceVariant = Color(0xFF41474D),
    onSurfaceVariant = Color(0xFFC1C7CE)
)
private val OceanMistLight = lightColorScheme(
    primary = Color(0xFF006399),
    secondary = Color(0xFF51606F),
    tertiary = Color(0xFF2E6575),
    surfaceVariant = Color(0xFFDDE3EA),
    onSurfaceVariant = Color(0xFF41474D)
)

private val AmberHorizonDark = darkColorScheme(
    primary = Color(0xFFEDC15B),
    secondary = Color(0xFFD6C4A1),
    tertiary = Color(0xFFE8BD8C),
    surfaceVariant = Color(0xFF4B4639),
    onSurfaceVariant = Color(0xFFCDC6B4)
)
private val AmberHorizonLight = lightColorScheme(
    primary = Color(0xFF735C00),
    secondary = Color(0xFF6A5E44),
    tertiary = Color(0xFF7E5429),
    surfaceVariant = Color(0xFFECE2C6),
    onSurfaceVariant = Color(0xFF4B4639)
)

private val DarkColorScheme =
  darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
  lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
  )

fun getDynamicTypography(fontPreset: Int): Typography {
    val fontFamily = when (fontPreset) {
        1 -> FontFamily.Monospace
        2 -> FontFamily.Serif
        else -> FontFamily.Default
    }
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.5.sp
        ),
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 52.sp,
            letterSpacing = (-0.25).sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  colorPreset: Int = 0,
  fontPreset: Int = 0,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> {
        when (colorPreset) {
          1 -> NordicForestDark
          2 -> CrimsonEclipseDark
          3 -> OceanMistDark
          4 -> AmberHorizonDark
          else -> DarkColorScheme
        }
      }
      else -> {
        when (colorPreset) {
          1 -> NordicForestLight
          2 -> CrimsonEclipseLight
          3 -> OceanMistLight
          4 -> AmberHorizonLight
          else -> LightColorScheme
        }
      }
    }

  MaterialTheme(
    colorScheme = colorScheme, 
    typography = getDynamicTypography(fontPreset), 
    content = content
  )
}
