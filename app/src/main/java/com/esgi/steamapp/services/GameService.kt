package com.esgi.steamapp.services

import com.esgi.steamapp.model.Games
import com.esgi.steamapp.model.MyGames
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameService {

    @GET("mygames")
    fun getMostPlayedGames(@Path("endpoint") endpoint: String): Call<MyGames>

    @GET("game")
    fun getEachGame(@Query("appids") appids: Int): Call<JsonObject>
}