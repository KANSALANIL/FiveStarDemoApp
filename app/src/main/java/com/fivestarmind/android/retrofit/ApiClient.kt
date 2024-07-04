package com.fivestarmind.android.retrofit

import com.fivestarmind.android.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {

    // QA url's

    private const val BASE_URL_APIS = BuildConfig.BASE_URL
    const val BASE_URL_MEDIA =  BuildConfig.BASE_MEDIA_URL
    const val BASE_URL_PROFILE =  BuildConfig.BASE_PROFILE_URL
    const val BASE_URL_LINKS = BuildConfig.TERMS_URL
    const val BASE_SOCKET_PATH =BuildConfig.BASE_SOCKET_PATH
    const val SOCKET_BASE_URL = BuildConfig.BASE_SOCKET_URL

    const val CHAT_BASE_URL =BuildConfig.CHAT_BASE_URL



    /*private const val BASE_URL_APIS = "http://103.149.154.53/5starmind/api/"
    const val BASE_URL_MEDIA = "http://103.149.154.53/5starstorage/"
    const val BASE_URL_PROFILE = "http://103.149.154.53/5starstorage/profile/"

    const val BASE_URL_LINKS = "http://103.149.154.53/5starmind/"
   // const val SOCKET_BASE_URL = "http://192.168.88.98:3000/chat"
    const val SOCKET_BASE_URL = "http://103.149.154.53/chat"
    const val CHAT_BASE_URL ="http://103.149.154.53/5starapi/"*/

    // Live base url
  /*  private const val BASE_URL_APIS = "https://fivestarmind.com/api/"
    const val BASE_URL_MEDIA = "https://fivestarmind.com/storage/"
    const val BASE_URL_PROFILE = "https://fivestarmind.com/storage/profile/"

    const val BASE_URL_LINKS = "https://fivestarmind.com/"
    const val SOCKET_BASE_URL = "https://fivestarmind.com/chat"
    const val CHAT_BASE_URL  = ""*/



    private var chtRetrofit: Retrofit? = null

    //val BASE_URL_LINKS_NEW = ""

    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {
            if (retrofit == null) {

               /* val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()*/

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL_APIS)
                    //.client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }


    val chatClient: Retrofit
        get() {
            if (chtRetrofit == null) {

                /* val interceptor = HttpLoggingInterceptor()
                 interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                 val client: OkdHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()*/

                chtRetrofit = Retrofit.Builder()
                    .baseUrl(CHAT_BASE_URL)
                    //.client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return chtRetrofit!!
        }
}
