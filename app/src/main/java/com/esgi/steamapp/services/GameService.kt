package com.esgi.steamapp.services

import com.esgi.steamapp.model.Games
import com.esgi.steamapp.model.MyGame
import com.esgi.steamapp.model.Rank
import retrofit2.Call
import retrofit2.http.GET

interface GameService {

    @GET("mygames")
    fun getMostPlayedGames(): Call<MyGame>

    @GET("game")
    fun getEachGame(): Call<Games>
}