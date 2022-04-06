package be.marche.apptravaux.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LARGEST_PADDING = 24.dp
val LARGE_PADDING = 12.dp
val MEDIUM_PADDING = 8.dp
val SMALL_PADDING = 6.dp
val PRIORITY_INDICATOR_SIZE = 16.dp
val TOP_APP_BAR_HEIGHT = 56.dp
val PRIORITY_DROP_DOWN_HEIGHT = 60.dp
val TASK_ITEM_ELEVATION = 2.dp

class Dimensions(
  val  width70: Dp,
  val  height70: Dp,
    val width: Dp,
    val height: Dp,
    val carte: Dp,
    val imageH: Dp,
    val imageW: Dp
)

val smartphoneDimension = Dimensions(
    width70 = 110.dp,
    height70 = 170.dp,
    width = 150.dp,
    height = 150.dp,
    carte = 350.dp,
    imageH = 350.dp,
    imageW = 200.dp
)

val tabletDimension = Dimensions(
    width70 = 190.dp,
    height70 = 250.dp,
    width = 220.dp,
    height = 220.dp,
    carte = 550.dp,
    imageH = 450.dp,
    imageW = 300.dp
)

class Typography(
    val fontStyle_1: TextStyle, val fontStyle_11: TextStyle,
    val fontStyle_2: TextStyle,
    val fontStyleSearch: TextStyle,
    val fontWidth_1: TextUnit,
    val fontWidth_2: TextUnit,
    val fontTitle_1: TextUnit,
)

val textSmallDimension = Typography(
    fontStyle_1 = TextStyle.Default.copy(fontSize = 15.sp),
    fontStyle_11 = TextStyle.Default.copy(fontSize = 13.sp),
    fontStyleSearch = TextStyle.Default.copy(fontSize = 15.sp),
    fontStyle_2 = TextStyle.Default.copy(fontSize = 22.sp),
    fontWidth_1 = 15.sp,
    fontWidth_2 = 22.sp,
    fontTitle_1 = 18.sp,
)

val textTabletDimensions = Typography(
    fontStyle_1 = TextStyle.Default.copy(fontSize = 25.sp),
    fontStyle_11 = TextStyle.Default.copy(fontSize = 20.sp),
    fontStyleSearch = TextStyle.Default.copy(fontSize = 32.sp),
    fontStyle_2 = TextStyle.Default.copy(fontSize = 32.sp),
    fontWidth_1 = 25.sp,
    fontWidth_2 = 25.sp,
    fontTitle_1 = 30.sp,
)

