package com.esgi.steamapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esgi.steamapp.databinding.HomePageBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomePageActivity : AppCompatActivity() {
    lateinit var more_info_button : Button
    lateinit var big_image : ImageView
    lateinit var recycler_view : RecyclerView
    var list_of_game_ids : MutableList<Int> = emptyList<Int>().toMutableList()
    val games = mutableListOf<Game>()
    var list_of_game_ids_test : MutableList<String> = emptyList<String>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        list_of_game_ids_test.add("730")
        list_of_game_ids_test.add("578080");


        GlobalScope.launch(Dispatchers.Main) {
            binding.progressbar.visibility = View.VISIBLE
            binding.gameList.visibility = View.GONE

            val response = withContext(Dispatchers.Default) {
                NetworkManagerGameList.getList()
            }
            val api_games = response.response.ranks
            for (i in api_games) {
                list_of_game_ids.add(i.appid)
            }

            withContext(Dispatchers.Default) {
                for(game_id in list_of_game_ids_test) {
                    val game_details = NetworkManagerGameDetails.getGameDetails(game_id)
                    if(game_id == "730") {
                        val game = Game(name = game_details.appid.data.name, editeur = game_details.appid.data.developers.toString(), prix = "00,00 $", image = game_details.appid.data.headerImage, description = game_details.appid.data.shortDescription)
                        games.add(game)
                        val img = game_details.appid.data.headerImage
                    }
                    if(game_id == "578080"){
                        val game = Game(name = game_details.appid2.data.name.toString(), editeur = game_details.appid2.data.developers.toString(), prix = "00,00 $", image = game_details.appid2.data.headerImage, description = game_details.appid2.data.shortDescription)
                        games.add(game)
                    }
                }
                runOnUiThread(java.lang.Runnable {
                    recycler_view = findViewById(R.id.game_list)
                    recycler_view.apply{
                        layoutManager = GridLayoutManager(this@HomePageActivity,1);
                        adapter = ListAdapter(games, object : OnProductListener {
                            override fun onClicked(game : Game, position : Int) {
                                Toast.makeText(
                                    this@HomePageActivity,
                                    "Game $position clicked",
                                    Toast.LENGTH_SHORT).show();
                                val intent = Intent(this@HomePageActivity,GameDetailsActivity::class.java)
                                intent.putExtra("game_name", game.name)
                                intent.putExtra("game_editor", game.editeur)
                                intent.putExtra("game_image", game.image)
                                intent.putExtra("game_id", list_of_game_ids_test[position])
                                intent.putExtra("game_description", game.description)
                                startActivity(intent)
                            }
                        });
                    }
                })
            }

            binding.progressbar.visibility = View.GONE
            binding.gameList.visibility = View.VISIBLE
        }

        /*
        GlobalScope.launch(Dispatchers.Main) {
            val response2 = withContext(Dispatchers.Default) {
                val gameService = RetrofitClientInstance
                    .getRetrofitInstance(ApiJavaTest::class.java, GetItemDetailsDeserializer())
                    .create(GameService::class.java)
                val test = gameService.getDetailsOfGame("730")
            }
        }
         */

        val url = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg?t=1668125812"
        big_image = findViewById(R.id.big_game)
        big_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(url).into(big_image)

        more_info_button = findViewById(R.id.button)
        more_info_button.setOnClickListener() {
            val intent = Intent(this, GameDetailsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.elements, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.like -> {
            val binding = HomePageBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val intent = Intent(this, LikedActivity::class.java)
            startActivity(intent)
            val database = Firebase.database
            val myRef = database.getReference("message")
            true
        }

        R.id.favorite -> {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}

data class Game(val name : String, val editeur : String, val prix : String,val image : String, val description : String)

class ListAdapter(val games: MutableList<Game>, val listener : OnProductListener) : RecyclerView.Adapter<GameViewHolder>() {

    override fun getItemCount(): Int = games.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.game_cell, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.updateGame(game)
        holder.button.setOnClickListener() {
            listener.onClicked(game, position)
        }
    }

}

class GameViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val game_name = v.findViewById<TextView>(R.id.nom)
    private val editeur = v.findViewById<TextView>(R.id.editeur)
    private val prix = v.findViewById<TextView>(R.id.prix)
    private val image = v.findViewById<ImageView>(R.id.image)
    val button = v.findViewById<Button>(R.id.en_savoir_plus_button)


    fun updateGame(game: Game) {
        game_name.text = game.name
        editeur.text = game.editeur
        prix.text = game.prix

        val url = game.image

        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(itemView.context)
            .load(url)
            .centerCrop()
            .into(image)
    }
}

interface OnProductListener {
    fun onClicked(game : Game, position : Int) {

    }

}