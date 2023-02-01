package com.esgi.steamapp

import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class GameAvisAPI(
    @SerializedName("success")
    val success : Int,
    @SerializedName("query_summary")
    val query_summary : Summary,
    @SerializedName("reviews")
    val reviews : Array<Avis>
)

data class Summary(
    @SerializedName("num_reviews")
    val num_reviews : Int
)

data class Avis(
    @SerializedName("author")
    val author : Author,
    @SerializedName("review")
    val review : String
)

data class Author(
    @SerializedName("steamid")
    val steamid : String
)

interface GameAvisInterface {
    @GET("appreviews/{appid}?json=1")
    fun getListAvis(@Path("appid") appid : String) : Deferred<GameAvisAPI>
}

object NetworkManagerAvisList {
    private val api = Retrofit.Builder()
        .baseUrl("https://store.steampowered.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(GameAvisInterface::class.java)

    suspend fun getListAvis(appid : String) : GameAvisAPI {
        return api.getListAvis(appid).await()
    }
}