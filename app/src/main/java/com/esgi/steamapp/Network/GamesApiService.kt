package com.esgi.steamapp.Network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


private const val URL = "https://api.steampowered.com/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(URL)
    .build()

interface GamesAPiService {

    @GET("games")
    suspend fun getGames(): String
}

object GamesApi {
    val retrofitService : GamesAPiService by lazy {
        retrofit.create(GamesAPiService::class.java)
    }
}