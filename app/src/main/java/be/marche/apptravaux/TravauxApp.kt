package be.marche.apptravaux

import android.app.Application
import be.marche.apptravaux.avaloir.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class TravauxApp : Application() {

    companion object {
        val galleryDir by lazy {
            be.marche.apptravaux.utils.galleryDir()
        }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin{
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@TravauxApp)

            modules(appModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        if (!galleryDir.exists()) {
            galleryDir.mkdirs()
        }
    }
}