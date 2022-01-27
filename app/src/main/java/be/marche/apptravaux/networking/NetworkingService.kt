package be.marche.apptravaux.networking

import be.marche.apptravaux.networking.models.TravauxApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkingService {

    @GET("/agenda.php")
    suspend fun fetchWeather(
    ): TravauxApiResponse

}