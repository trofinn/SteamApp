package com.esgi.steamapp.service

import com.esgi.steamapp.model.Games
import com.esgi.steamapp.model.MyGames
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkInterface {
    @GET("/ISteamChartsService/GetMostPlayedGames/v1/")
    suspend fun getMostPlayedGames(): MyGames

    @GET("/api/appdetails")
    suspend fun getEachGame(@Query("appids") appids: String): JsonObject
}