package be.marche.apptravaux.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class DetailModel(
    val icon: ImageVector,
    val iconColor: Color,
    val name: String,
    val result: Boolean,
    val detail: String? = null
)
class Results(
    val isCompatible: Boolean,
    val detailModel: List<DetailModel>
)