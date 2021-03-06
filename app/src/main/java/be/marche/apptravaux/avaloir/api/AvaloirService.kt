package be.marche.apptravaux.avaloir.api

import be.marche.apptravaux.avaloir.entity.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AvaloirService {
    @GET("avaloirs/api/all")
    suspend fun getAllAvaloirs(
    ): List<Avaloir>

    @POST("avaloirs/api/xx")
    suspend fun updateAllAvaloirs(
    ): List<Avaloir>

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
    suspend fun getAllDates(
    ): List<DateNettoyage>

    @GET("avaloirs/api/commentaires")
    suspend fun getAllCommentaires(
    ): List<Commentaire>

    @POST("avaloirs/api/search")
    suspend fun searchAvaloir(
        @Body params: SearchRequest
    ): Response<SearchResponse>

}