package com.esgi.steamapp.Network

import com.esgi.steamapp.model.Games
import com.esgi.steamapp.model.MyGames
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val URL = "https://api.steampowered.com/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(URL)
    .build()

interface GamesAPiService {

    @GET("/ISteamChartsService/GetMostPlayedGames/v1/")
    suspend fun getSteamGames(): List<MyGames>
}

object GamesApi {
    val retrofitService : GamesAPiService by lazy {
        retrofit.create(GamesAPiService::class.java)
    }
}