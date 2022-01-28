package be.marche.apptravaux.networking

import be.marche.apptravaux.networking.models.TravauxApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkingService {

    @GET("actus.php")
    suspend fun fetchWeather(
    ): List<TravauxApiResponse>

}