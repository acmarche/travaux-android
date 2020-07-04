package be.marche.apptravaux.avaloir.di

import be.marche.apptravaux.BuildConfig
import be.marche.apptravaux.avaloir.api.AvaloirInterceptor
import be.marche.apptravaux.avaloir.api.AvaloirService
import be.marche.apptravaux.avaloir.database.AppDatabase
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.avaloir.repository.AvaloirRepository
import be.marche.apptravaux.geofence.GeofenceManager
import be.marche.apptravaux.location.LocationViewModel
import be.marche.apptravaux.permission.PermissionUtil
import be.marche.apptravaux.stock.SyncViewModel
import be.marche.apptravaux.stock.api.StockService
import be.marche.apptravaux.stock.categorie.CategorieRepository
import be.marche.apptravaux.stock.categorie.CategorieViewModel
import be.marche.apptravaux.stock.produit.ProduitRepository
import be.marche.apptravaux.stock.produit.ProduitViewModel
import be.marche.apptravaux.utils.FileHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    single { createOkHttpClient<OkHttpClient>() }
    single {
        createWebService<AvaloirService>(
            get(),
            BuildConfig.API_URL
        )
    }

    single { AppDatabase.buildDatabase(androidApplication()) }
    single { GeofenceManager(androidApplication()) }
    single { get<AppDatabase>().avaloirDao() }
    single { get<AppDatabase>().stockDao() }
    single { AvaloirRepository(get(), get()) }
    single { PermissionUtil(get()) }
    single { FileHelper() }
    single { AvaloirInterceptor(get()) }

    viewModel { LocationViewModel(get()) }
    viewModel { AvaloirViewModel(get(), get(), get()) }

    /**
     * Stock
     */
    single {
        createWebService<StockService>(
            get(),
            BuildConfig.API_URL
        )
    }
    single { ProduitRepository(get()) }
    single { CategorieRepository(get()) }

    viewModel { ProduitViewModel(get(), get()) }
    viewModel { CategorieViewModel(get()) }
    viewModel { SyncViewModel(get(), get(), get()) }
}

inline fun <reified T> createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .addInterceptor(AvaloirInterceptor())
        //  .addInterceptor(BasicAuthInterceptor("**", "**"))
        .build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}