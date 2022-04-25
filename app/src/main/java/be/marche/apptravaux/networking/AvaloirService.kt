package be.marche.apptravaux.networking

import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import be.marche.apptravaux.ui.entities.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AvaloirService {

    @GET("avaloirs/api/all")
    suspend fun fetchAllAvaloirs(
    ): List<Avaloir>

    @GET("avaloirs/api/all")
    fun fetchAllAvaloirsNotSuspend(
    ): Call<List<Avaloir>>

    @POST("avaloirs/api/xx")
    suspend fun updateAllAvaloirs(
    ): List<Avaloir>

    @Multipart
    @POST("avaloirs/api/insert")
    fun insertAvaloirNotSuspend(
        @Part("coordinates") coordinates: Coordinates,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ): Call<DataResponse>

    @POST("avaloirs/api/clean/{id}/{date}")
    fun insertDateNotSuspend(
        @Path("id") avaloirId: Int,
        @Path("date") date: String
    ): Call<DataResponse>

    @POST("avaloirs/api/commentaire/{id}/{content}")
    fun insertCommentaireNotSuspend(
        @Path("id") avaloirId: Int,
        @Path("content") comment: CharSequence
    ): Call<DataResponseCommentaire>

    @Multipart
    @POST("avaloirs/api/insert")
    suspend fun insertAvaloir(
        @Part("coordinates") coordinates: Coordinates,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ): Response<DataResponse>

    @POST("avaloirs/api/update/{id}")
    suspend fun updateAvaloir(
        @Path("id") avaloirId: Int,
        @Body avaloir: Avaloir
    ): Response<DataResponse>

    @POST("avaloirs/api/clean/{id}/{date}")
    suspend fun cleanAvaloir(
        @Path("id") avaloirId: Int,
        @Path("date") date: String,
        @Body avaloir: Avaloir
    ): Response<DataResponse>

    @POST("avaloirs/api/commentaire/{id}/{content}")
    suspend fun addCommentaireAvaloir(
        @Path("id") avaloirId: Int,
        @Path("content") comment: CharSequence,
        @Body avaloir: Avaloir
    ): Response<DataResponseCommentaire>

    @GET("avaloirs/api/dates")
    suspend fun fetchAllDates(
    ): List<DateNettoyage>

    @GET("avaloirs/api/dates")
    fun fetchAllDatesNotSuspend(
    ): Call<List<DateNettoyage>>

    @GET("avaloirs/api/commentaires")
    fun fetchAllCommentairesNotSuspend(
    ): Call<List<Commentaire>>

    @GET("avaloirs/api/commentaires")
    suspend fun fetchAllCommentaires(
    ): List<Commentaire>

    @POST("avaloirs/api/search")
    suspend fun searchAvaloir(
        @Body params: SearchRequest
    ): Response<SearchResponse>

}