package com.esgi.steamapp;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.Deferred;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Classs {
}

interface GameService {
    @GET("appdetails")
    public Deferred<ApiJavaTest> getDetailsOfGame(@Query("appids") String appids);
}

class GetItemDetailsDeserializer implements JsonDeserializer<GameDetailsJava> {

    @Override
    public GameDetailsJava deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final boolean success = jsonObject.get("success").getAsBoolean();
        return new GameDetailsJava();
    }
}
/*
class GetItemListDeserializer implements JsonDeserializer<List<Item>> {

    @Override

    public List deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        List items = new ArrayList<>();
        final JsonObject jsonObject = json.getAsJsonObject();
        final Boolean success = jsonObject.get("success").getAsBoolean();
        final JsonArray itemsJsonArray = jsonObject.get("data").getAsJsonArray();

        for (JsonElement itemsJsonElement : itemsJsonArray) {
            final JsonObject itemJsonObject = itemsJsonElement.getAsJsonObject();
            final String id = itemJsonObject.get("id").getAsString();
            final String name = itemJsonObject.get("name").getAsString();
            items.add(new Item(id, name));
        }
        return items;
    }
}

 */

class RetrofitClientInstance {

    private static final String BASE_URL = "https://store.steampowered.com/api/";

    private static Converter.Factory createGsonConverter(Type type, Object typeAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        Gson gson = gsonBuilder.create();
        return GsonConverterFactory.create(gson);
    }

    public static Retrofit getRetrofitInstance(Type type, Object typeAdapter) {
        return new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(createGsonConverter(type, typeAdapter))
                .build();
    }
}