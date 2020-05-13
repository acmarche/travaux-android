package be.marche.apptravaux.avaloir.entity

data class DataResponse(
    val error: Int,
    val message: String,
    val avaloir: Avaloir,
    val date: DateNettoyage
)

data class DataResponseCommentaire(
    val error: Int,
    val message: String,
    val avaloir: Avaloir,
    val commentaire: Commentaire
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

data class Coordinates
    (
    val latitude: Double,
    val longitude: Double
)
 class Result() {
     fun loading() {

     }

 }