package com.esgi.steamapp.service

import com.esgi.steamapp.model.MostPlayedGamesResponse
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkInterface {
    @GET("/ISteamChartsService/GetMostPlayedGames/v1/")
    suspend fun getMostPlayedGames(): MostPlayedGamesResponse

    @GET("/api/appdetails")
    suspend fun getEachGame(@Query("appids") appids: String): JsonObject
}