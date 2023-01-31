package com.esgi.steamapp

import android.widget.Toast
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*
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
    @SerializedName("short_description"    ) var shortDescription    : String,
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

/*
data class SteamResponse(val game: SteamGameResponse)

data class SteamGameResponse(
    val success: Boolean,
    val data: SteamGameResponseData?
)

data class SteamGameResponseData(
    val type: String,
    val name: String,
)

interface SteamAPI {
    @GET("appdetails")
    fun getGameById(@Query("appids") id: String): Deferred<SteamResponse>
}

object NetworkRequest {

    private val api = Retrofit.Builder()
        .baseUrl("https://store.steampowered.com/api/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(SteamResponse::class.java, SteamResponseDeserializer())
                    .create()
            )
        )
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(SteamAPI::class.java)


    suspend fun getGame(id: String): SteamResponse {
        return api.getGameById(id).await()
    }

}

class SteamResponseDeserializer : JsonDeserializer<SteamResponse> {

    companion object {
        val deserializer: Gson = GsonBuilder().create()
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SteamResponse {
        // On récupère le JSON
        val jsonObject = json?.asJsonObject

        // On récupère la clé du premier élément du json (ex : "578080") qui est un entier
        val key = jsonObject?.keySet()?.first { it.toIntOrNull() != null }

        return SteamResponse(
            deserializer.fromJson(
                jsonObject?.get(key), SteamGameResponse::class.java
            )
        )
    }
}
 */