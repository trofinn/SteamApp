package com.esgi.steamapp
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class API {
}


data class Response(
    @SerializedName("response")
    val response : SteamResponse)


data class SteamResponse(
    @SerializedName("rollup_date")
    val rollupDate : Int,
    @SerializedName("ranks")
    val ranks : List<apiGame>)


data class apiGame (

    @SerializedName("rank")
    var rank : Int,
    @SerializedName("appid")
    var appid : Int,
    @SerializedName("last_week_rank")
    var lastWeekRank : Int,
    @SerializedName("peak_in_game")
    var peakInGame : Int,

)

interface SteamAPI {

    @GET("GetMostPlayedGames/v1/?")
    fun getListOfGames() : Deferred<Response>


}

object NetworkManagerGameList {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.steampowered.com/ISteamChartsService/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(SteamAPI::class.java)

    suspend fun getList() : Response {
        return api.getListOfGames().await()
    }
}