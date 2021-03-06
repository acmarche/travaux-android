package be.marche.apptravaux.avaloir.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class AvaloirInterceptor() : Interceptor {

    var context: Context? = null

    constructor(context: Context) : this() {
        Timber.w("zeze ici init")
        this.context = context
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 500) {
            request.newBuilder()
            response.close()
            return chain.proceed(request)
            //Toast.makeText(this.context, "Coucou", Toast.LENGTH_LONG).show()
            // handleForbiddenResponse()
        }

        return response

    }
}