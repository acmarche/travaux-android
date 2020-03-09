package be.marche.apptravaux.api

import be.marche.apptravaux.avaloir.entity.Avaloir
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface TravauxService {
    @GET("all")
    suspend fun getAllAvaloirs(
    ): List<Avaloir>
    @POST("xx")
    suspend fun updateAllAvaloirs(
    ): List<Avaloir>
}