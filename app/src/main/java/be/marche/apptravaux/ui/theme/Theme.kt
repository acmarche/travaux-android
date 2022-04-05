package be.marche.apptravaux.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    //primary = Purple500,
    primary = Pink500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun AppTravaux6Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val configuration = LocalConfiguration.current
    val dimensions =
        if (configuration.screenWidthDp <= 420) smartphoneDimension else tabletDimension
    val typography =
        if (configuration.screenWidthDp <= 420) textSmallDimension else textTabletDimensions

    ProvideDimens(dimensions = dimensions) {
        ProvideAppTypography(typography = typography) {
            MaterialTheme(
                colors = colors,
                typography = Typography2,
                shapes = Shapes,
                content = content
            )
        }
    }
}


object ScreenSizeTheme {
    val dimens: Dimensions
        @Composable
        get() = LocalAppDimens.current
    val textStyle: Typography
        @Composable
        get() = LocalAppTypography.current
}


@Composable
fun ProvideDimens(dimensions: Dimensions, content: @Composable () -> Unit) {
    val dimensionSet = remember { dimensions }
    CompositionLocalProvider(LocalAppDimens provides dimensionSet, content = content)
}

private val LocalAppDimens = staticCompositionLocalOf { smartphoneDimension }

@Composable
fun ProvideAppTypography(typography: Typography, content: @Composable () -> Unit) {
    val typographySet = remember { typography }
    CompositionLocalProvider(LocalAppTypography provides typographySet, content = content)
}

private val LocalAppTypography = staticCompositionLocalOf { textSmallDimension }