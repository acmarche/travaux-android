package be.marche.apptravaux.repository

import be.marche.apptravaux.networking.NetworkingService
import be.marche.apptravaux.networking.models.TravauxApiResponse
import javax.inject.Inject

class TravauxRepository@Inject constructor(private val networkingService: NetworkingService) {

    suspend fun fetchWeather(long: String, lat: String): List<TravauxApiResponse> =
        networkingService.fetchWeather()
}