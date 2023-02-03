package com.esgi.steamapp.service

import com.esgi.steamapp.model.MostPlayedGamesResponse
import com.esgi.steamapp.model.SearchGameResponse
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkInterface {
    @GET("/ISteamChartsService/GetMostPlayedGames/v1/")
    suspend fun getMostPlayedGames(): MostPlayedGamesResponse

    @GET("/api/appdetails")
    suspend fun getEachGame(@Query("appids") appids: String): JsonObject

    @GET("/actions/SearchApps/{name}")
    suspend fun searchGame(@Path(value = "name", encoded = true) name: String): List<SearchGameResponse>
}