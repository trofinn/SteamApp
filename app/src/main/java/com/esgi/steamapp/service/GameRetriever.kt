package com.esgi.steamapp.service

import com.esgi.steamapp.model.MostPlayedGamesResponse
import com.esgi.steamapp.model.SearchGameResponse
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameRetriever {
    private val networkInterface: NetworkInterface
    private val networkInterface2: NetworkInterface
    private val networkInterface3: NetworkInterface

    companion object {
        var URL = "https://api.steampowered.com/"
        var URL2 = "https://store.steampowered.com/"
        var URL3 = "https://steamcommunity.com/"
    }

    init{
        val retrofit = Retrofit
            .Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofit2 = Retrofit
            .Builder()
            .baseUrl(URL2)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofit3 = Retrofit
            .Builder()
            .baseUrl(URL3)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        networkInterface = retrofit.create(NetworkInterface::class.java)
        networkInterface2 = retrofit2.create(NetworkInterface::class.java)
        networkInterface3 = retrofit3.create(NetworkInterface::class.java)
    }

    suspend fun getMostPlayedGames() : MostPlayedGamesResponse {
        return networkInterface.getMostPlayedGames()
    }
    suspend fun getAGame(appid: String) : JsonObject {
        return networkInterface2.getEachGame(appid)
    }
    suspend fun searchGame(appid: String) : List<SearchGameResponse> {
        return networkInterface3.searchGame(appid)
    }
}

