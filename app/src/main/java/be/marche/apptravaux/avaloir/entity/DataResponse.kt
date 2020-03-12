package be.marche.apptravaux.avaloir.entity

data class DataResponse(
    val error: Int,
    val message: String,
    val avaloir: Avaloir,
    val date: DateNettoyage
)