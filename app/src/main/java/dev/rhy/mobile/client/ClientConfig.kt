package dev.rhy.mobile.client

import dev.rhy.mobile.utils.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ClientConfig {
    companion object {
        private var clientConfig: ClientConfig? = null
        fun instance(): Retrofit {
            if ( clientConfig == null) {
                clientConfig = ClientConfig()
            }
            return clientConfig!!.retrofit
        }
    }

    private var logInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private var httpClient = OkHttpClient.Builder()
        .addInterceptor(MyInterceptor())
        .addInterceptor(logInterceptor)
        .build()

    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8181/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(httpClient)
        .build()
}

class MyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originRequest = chain.request()
        val newRequest = originRequest.newBuilder()
            .header("Authorization", "Bearer ${Cache.instance().getToken()}")
            .header("x-ads-id", "b873dcb5-d500-4f83-a5d6-4301edc0cafe")
            .header("x-user-id", "199534")
            .build()
        return chain.proceed(newRequest)
    }
}