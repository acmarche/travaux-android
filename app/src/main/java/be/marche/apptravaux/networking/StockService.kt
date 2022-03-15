package be.marche.apptravaux.networking

import be.marche.apptravaux.entities.Categorie
import be.marche.apptravaux.entities.Produit
import be.marche.apptravaux.entities.StockData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StockService {

    @GET("stock/api/all")
    suspend fun getAllData(
    ): Response<StockData>

    @GET("stock/api/categories")
    suspend fun getAllCategories(
    ): Response<List<Categorie>>

    @GET("stock/api/produits")
    suspend fun getAllProduits(
    ): List<Produit>

    @POST("stock/api/update/{id}/{quantite}")
    suspend fun updateProduit(
        @Path("id") produitId: Int,
        @Path("quantite") quantite: Int
    ): Response<Produit>
}