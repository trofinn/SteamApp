package com.esgi.steamapp.services
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    private const val URL = "https://api.steampowered.com/"
    private var URL2 = "https://store.steampowered.com/"

    //Create client
    private val okHttp = OkHttpClient.Builder()

    //retrofit builder
    private val builder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(URL)
        .client(okHttp.build())


    //fetch each game
    private var builder2 = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(URL2)
        .client(okHttp.build())

    //create retrofit Instance
    private val retrofit = builder.build()



    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }

    fun <T> buildService2 (serviceType :Class<T>):T{
        return builder2.build().create(serviceType)
    }
}

