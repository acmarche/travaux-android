package be.marche.apptravaux.api

import be.marche.apptravaux.avaloir.entity.Avaloir
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
    ): Response<Avaloir>

    @Multipart
    @POST("api/update/enfant/photo/{id}")
    fun uploadImage(
        @Path("id") enfantId: Int,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ): Call<ResponseBody>
}