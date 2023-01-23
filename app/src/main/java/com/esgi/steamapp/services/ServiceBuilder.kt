package com.esgi.steamapp.services
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.file.attribute.AclEntry.Builder

object ServiceBuilder {

    private const val URL = "https://api.steampowered.com/ISteamChartsService/GetMostPlayedGames/v1?"
    private var URL2 = "hhttps://store.steampowered.com/api/appdetails?appids"

    //Create client
    private val okHttp = OkHttpClient.Builder()

    //retrofit builder
    private val builder = Retrofit.Builder().baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())


    //fetch each game
    private var builder2: Retrofit.Builder = TODO()

    //create retrofit Instance
    private val retrofit = builder.build()



    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }

    fun <T> buildService2 (serviceType :Class<T>, appid: Int):T{
        URL2 += appid
        builder2 = Retrofit.Builder().baseUrl(URL2)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build())
        return builder2.build().create(serviceType)
    }
}

