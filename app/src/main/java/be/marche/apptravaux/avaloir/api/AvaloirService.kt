package be.marche.apptravaux.avaloir.api

import be.marche.apptravaux.avaloir.entity.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AvaloirService {
    @GET("all")
    suspend fun getAllAvaloirs(
    ): List<Avaloir>

    @POST("xx")
    suspend fun updateAllAvaloirs(
    ): List<Avaloir>

    @Multipart
    @POST("insert")
    suspend fun insertAvaloir(
        @Part("coordinates") coordinates: Coordinates,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ): Response<DataResponse>

    @POST("update/{id}")
    suspend fun updateAvaloir(
        @Path("id") avaloirId: Int,
        @Body avaloir: Avaloir
    ): Response<DataResponse>

    @POST("clean/{id}/{date}")
    suspend fun cleanAvaloir(
        @Path("id") avaloirId: Int,
        @Path("date") date: String,
        @Body avaloir: Avaloir
    ): Response<DataResponse>

    @POST("commentaire/{id}/{content}")
    suspend fun addCommentaireAvaloir(
        @Path("id") avaloirId: Int,
        @Path("content") comment: CharSequence,
        @Body avaloir: Avaloir
    ): Response<DataResponseCommentaire>

    @GET("dates")
    suspend fun getAllDates(
    ): List<DateNettoyage>

    @GET("commentaires")
    suspend fun getAllCommentaires(
    ): List<Commentaire>

    @POST("search")
    suspend fun searchAvaloir(
        @Body params: SearchRequest
    ): Response<SearchResponse>

}