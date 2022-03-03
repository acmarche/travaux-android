package be.marche.apptravaux.di

import android.content.Context
import androidx.room.Room
import be.marche.apptravaux.database.AppDatabase
import be.marche.apptravaux.database.AvaloirDao
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import be.marche.apptravaux.utils.FileHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * App scoped module for dependency injections
 * Using it to allow for better testability of the app since Hilt is so fast to stand up
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideBaseUrl() = "https://apptravaux.marche.be/"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        val applicationScope = CoroutineScope(SupervisorJob())
        return AppDatabase.getDatabase(context = appContext)
    }

    @Provides
    fun provideAvaloirDao(appDatabase: AppDatabase): AvaloirDao {
        return appDatabase.avaloirDao()
    }

    @Provides
    fun provideFileHelper(): FileHelper {
        return FileHelper()
    }

    @Provides
    fun provideRetrofit(): AvaloirService =
        Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AvaloirService::class.java)

    private fun getOkHttpClient() =
        OkHttpClient.Builder()
            .connectTimeout(NETWORK_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NETWORK_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    fun provideCoroutineDispatcher() = CoroutineDispatcherProvider()

    companion object {
        const val NETWORK_REQUEST_TIMEOUT_SECONDS = 15L
        const val BASE_URL = "https://apptravaux.marche.be/"
    }

}