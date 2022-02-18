package be.marche.apptravaux.ui.entities

import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage

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

data class NewsUiState(
    val isSignedIn: Boolean = false,
    val isPremium: Boolean = false,
    val newsItems: List<NewsItemUiState> = listOf(),
)

data class NewsItemUiState(
    val title: String,
    val body: String,
    val bookmarked: Boolean = false,
)
