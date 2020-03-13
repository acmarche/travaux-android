package be.marche.apptravaux.api

import be.marche.apptravaux.avaloir.entity.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface TravauxService {
    @GET("all")
    suspend fun getAllAvaloirs(
    ): List<Avaloir>

    @POST("xx")
    suspend fun updateAllAvaloirs(
    ): List<Avaloir>

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

    @Multipart
    @POST("photo/{id}")
    suspend fun uploadPhoto(
        @Path("id") avaloirId: Int,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ): Response<DataResponse>

    @GET("dates")
    suspend fun getAllDates(
    ): List<DateNettoyage>

    @POST("search")
    suspend fun searchAvaloir(
        @Body params: SearchRequest
    ): Response<SearchResponse>

}