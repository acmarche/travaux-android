package be.marche.apptravaux.avaloir.entity

data class DataResponse(
    val error: Int,
    val message: String,
    val avaloir: Avaloir,
    val date: DateNettoyage
)

data class SearchResponse
    (
    val error: Int,
    val message: String,
    val avaloirs: List<Avaloir>
)

data class SearchRequest
    (
    val latitude: Double,
    val longitude: Double,
    val distance: String
)