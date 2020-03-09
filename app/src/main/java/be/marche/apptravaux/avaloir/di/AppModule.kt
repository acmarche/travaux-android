package be.marche.apptravaux.avaloir.di

import be.marche.apptravaux.api.TravauxService
import be.marche.apptravaux.avaloir.database.AppDatabase
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.avaloir.repository.AvaloirRepository
import be.marche.apptravaux.geofence.GeofenceManager
import be.marche.apptravaux.location.LocationViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import be.marche.apptravaux.BuildConfig

val appModule = module {

    single { createOkHttpClient<OkHttpClient>() }
    single {
        createWebService<TravauxService>(
            get(),
            BuildConfig.API_URL
        )
    }

    single { AppDatabase.buildDatabase(androidApplication()) }
    single { GeofenceManager(androidApplication()) }

    single { get<AppDatabase>().avaloirDao() }

    single { AvaloirRepository(get(), get()) }

    viewModel { LocationViewModel(get()) }
    viewModel { AvaloirViewModel(get(), get()) }

}

inline fun <reified T> createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
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