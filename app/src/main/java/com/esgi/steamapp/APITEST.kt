package com.esgi.steamapp

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class APITEST {
}

data class Data (

    @SerializedName("name"                 ) var name                : String,
    @SerializedName("steam_appid"          ) var steamAppid          : Int,
    @SerializedName("is_free"              ) var isFree              : Boolean,
    @SerializedName("short_description"    ) var shortDescription    : String,
    @SerializedName("header_image"         ) var headerImage         : String,
    @SerializedName("developers"           ) var developers          : ArrayList<String>,

)

data class GameDetails(

    @SerializedName("success" )
    var success : Boolean,
    @SerializedName("data"    )
    @Expose
    var data    : Data,
)

interface GameAPI {
    @GET("appdetails?")
    fun getDetailsOfGame(@Query("appids") appids : String) : Deferred<GameDetails>
}

object NetworkManagerGameDetails {
    private val api = Retrofit.Builder()
        .baseUrl("https://store.steampowered.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(GameAPI::class.java)

    suspend fun getGameDetails(appid : String) : GameDetails {
        return api.getDetailsOfGame(appid).await()
    }
}