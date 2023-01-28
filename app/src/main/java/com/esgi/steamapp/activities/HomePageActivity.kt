package com.esgi.steamapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esgi.steamapp.Adapter.GameAdapter
import com.esgi.steamapp.NetworkManagerGameDetails
import com.esgi.steamapp.NetworkManagerGameList
import com.esgi.steamapp.Overview.HomeViewModel
import com.esgi.steamapp.R
import com.esgi.steamapp.databinding.HomePageBinding
import com.esgi.steamapp.model.Game
import com.esgi.steamapp.model.Games
import com.esgi.steamapp.model.MyGames
import com.esgi.steamapp.network1.GameRetriever
import com.esgi.steamapp.services.GameService
import com.esgi.steamapp.services.ServiceBuilder
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HomePageActivity : AppCompatActivity() {
    lateinit var more_info_button : Button
    lateinit var big_image : ImageView
    lateinit var recycler_view : RecyclerView
    lateinit var adapter: GameAdapter
    var list_of_game_ids : MutableList<Int> = emptyList<Int>().toMutableList()
    var that = this
     var game_filtered : MutableList<Games.Result.Data> = mutableListOf()
    var games: MutableList<Games.Result.Data> = mutableListOf()
    lateinit var myGames: List<MyGames.Response.Rank>
    val gameRetriever: GameRetriever = GameRetriever()



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        val viewModel: HomeViewModel by viewModels()




        //load game in recyclerView
        //buildRecycleView()

        println("-------LOADING GAMES------- ")
        fetchGames()



        GlobalScope.launch(Dispatchers.Main) {
            binding.progressbar.visibility = View.VISIBLE
            binding.gameList.visibility = View.GONE

            val response = withContext(Dispatchers.Default) {
                NetworkManagerGameList.getList()
            }

            binding.progressbar.visibility = View.GONE
            binding.gameList.visibility = View.VISIBLE
            val api_games = response.response.ranks
            for (i in api_games) {
                list_of_game_ids.add(i.appid)
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response2 = withContext(Dispatchers.Default) {
                NetworkManagerGameDetails.getGameDetails("730")
            }
            binding.lesMeilleuresVentes.text = response2.success.toString()
        }


        val url = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg?t=1668125812"

        big_image = findViewById(R.id.big_game)
        big_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(url).into(big_image)

        more_info_button = findViewById(R.id.button)
        more_info_button.setOnClickListener() {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.elements, menu)

        //searchview
        val searchView = findViewById<SearchView>(R.id.search_bar)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText!!)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.like -> {
            val intent = Intent(this, LikedActivity::class.java)
            startActivity(intent)
            val database = Firebase.database
            val myRef = database.getReference("message")
            true
        }

        R.id.favorite -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }



    private fun buildRecycleView() {
        games = mutableListOf()
        game_filtered = mutableListOf()
        myGames = mutableListOf()




        val game = Game(name = "Counter ", editeur = "Gesco", prix = "10,00 $",
            image = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg?t=1668125812")
        val game1 = Game(name = "test", editeur = "Gesco", prix = "10,00 $",
            image = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg?t=1668125812")
       /* games.add(game)

        games.add(game1)
        games.add(game)
        games.add(game)
        games.add(game)
        games.add(game)
        games.add(game)
        games.add(game)

        recycler_view = findViewById(R.id.game_list)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)


        game_filtered.addAll(games)

        recycler_view.adapter = GameAdapter(game_filtered, this)

        */

    }


    //apply filter on search event
    private fun filter(newText: String) {
        game_filtered.clear()
        val searchText = newText!!.toLowerCase(Locale.getDefault())
        if (searchText.isNotEmpty()) {

            games.forEach {
                if (it.name.toLowerCase().contains(searchText)) {
                    game_filtered.add(it)
                }
            }

            if (game_filtered.isEmpty()) {
                Toast.makeText(applicationContext, "No Data Found..",
                    Toast.LENGTH_SHORT).show()
            }
            else {
                recycler_view.adapter!!.notifyDataSetChanged()
            }


        }
        else {
            game_filtered.clear()
            game_filtered.addAll(games)
            recycler_view.adapter!!.notifyDataSetChanged()
        }
    }




    private fun loadGames() {
        var gamesList: MutableList<JsonElement> = mutableListOf()
        game_filtered = mutableListOf()

        var gameService: GameService
        var requestCall: Call<JsonObject>
        println("TEST -------myGames.size")
         myGames.forEach {
             println("------------ TEST -----------")
             gameService = ServiceBuilder.buildService2(GameService::class.java)
             requestCall = gameService.getEachGame(it.appid.toString())

             println("test")
             requestCall.enqueue(object : Callback<JsonObject> {

                 override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                     Log.d("Response", "onResponse: ${response.body()}")
                     if (response.isSuccessful) {
                         gamesList.add(response.body()!!.get(it.appid.toString()))

                         Log.d("Response", "gamelist size : ${gamesList.size}")

                     } else {
                         Toast.makeText(
                             this@HomePageActivity,
                             "Something went wrong ${response.message()}",
                             Toast.LENGTH_SHORT
                         ).show()
                     }
                 }

                 override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                     Toast.makeText(
                         this@HomePageActivity,
                         "Something went wrong $t",
                         Toast.LENGTH_LONG
                     ).show()
                 }
             })
         }



            //make network call asynchronously

        }

    private fun loadMyGames() {

        //initiate the service
        val gameService  = ServiceBuilder.buildService(GameService::class.java)
        val requestCall = gameService.getMostPlayedGames()



        //make network call asynchronously
        requestCall.enqueue(object : Callback<MyGames> {

            override fun onResponse(call: Call<MyGames>, response: Response<MyGames>) {
                Log.d("Response", "onResponse: ${response.body()}")
                if (response.isSuccessful){
                    myGames  = response.body()!!.response.ranks

                    Log.d("Response", "myGames size : ${myGames.size}")

                }else{
                    Toast.makeText(this@HomePageActivity, "Something went wrong ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MyGames>, t: Throwable) {
                Toast.makeText(this@HomePageActivity, "Something went wrong $t", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchGames() {
        val gamesFetchJob = Job()
        val errorHandler = CoroutineExceptionHandler  {
            coroutineContext, throwable ->
            throwable.printStackTrace()
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }

        val scope = CoroutineScope(gamesFetchJob + Dispatchers.IO)
        val mapper = Gson()
        var mydata : Games.Result.Data
        var myresult: JsonElement
        scope.launch(errorHandler) {
            val deferred = async {gameRetriever.getMostPlayedGames().response.ranks }
            myGames = deferred.await()
            Log.d("--------Response", "gameList size : ${myGames.size}")
            var deferred2 : Deferred<JsonObject>
            myGames = myGames.subList(0, 10)
            myGames.forEach {
                deferred2 = async { gameRetriever.getAGame(it.appid.toString()) }
                myresult = deferred2.await().get(it.appid.toString())
                println(myresult.get("data"))
                mydata = mapper.fromJson(myresult.getAsJsonObject("data"), Games.Result.Data::class.java)
                println(mydata)
                games.add(mydata)
            }
            Log.d("-----NEW Response", "games size : ${games.size}")

        }

    }

    private fun renderData(gameMist: MyGames) {
    }

        }












