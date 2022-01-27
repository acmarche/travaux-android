package be.marche.apptravaux.networking

import be.marche.apptravaux.networking.models.WeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkingService {

    @GET("/agenda.php")
    suspend fun fetchWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String = "4596df18d7f3e4389a40371e52e0ef9c"
    ): WeatherApiResponse

}