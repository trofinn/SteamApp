package com.esgi.steamapp

import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
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
    @SerializedName("detailed_description" ) var shortDescription    : String,
    @SerializedName("header_image"         ) var headerImage         : String,
    @SerializedName("developers"           ) var developers          : ArrayList<String>,
    @SerializedName("price_overview"       ) var price               : Price,

)

data class Price (
    @SerializedName("currency")
    var currency : String,

    @SerializedName("initial")
    var initial : Int,

    @SerializedName("final")
    var final : Int,

    @SerializedName("discount_percent")
    var discount_percent : Int,

    @SerializedName("initial_formatted")
    var initial_formatted : String,

    @SerializedName("final_formatted")
    var final_formatted : String,
)


data class Games(
    @SerializedName("730")
    val appid : GameDetails,
    @SerializedName("578080")
    val appid2 : GameDetails,
)

data class GameDetails(

    @SerializedName("success" )
    var success : Boolean,
    @SerializedName("data"    )
    @Expose
    var data    : Data,
)

interface GameAPI {
    @GET("appdetails")
    fun getDetailsOfGame(@Query("appids") appids : String) : Deferred<Games>
}

object NetworkManagerGameDetails {
    private val api = Retrofit.Builder()
        .baseUrl("https://store.steampowered.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(GameAPI::class.java)

    suspend fun getGameDetails(appid : String) : Games {
        return api.getDetailsOfGame(appid).await()
    }
}
