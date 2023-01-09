package com.esgi.steamapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esgi.steamapp.databinding.HomePageBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageActivity : AppCompatActivity() {
    lateinit var more_info_button : Button
    lateinit var big_image : ImageView
    lateinit var recycler_view : RecyclerView
    var list_of_game_ids : MutableList<Int> = emptyList<Int>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))


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


        val games = mutableListOf<Game>()

        val game = Game(name = "Counter Strike", editeur = "Gesco", prix = "10,00 $", image = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg?t=1668125812")
        games.add(game)
        games.add(game)
        games.add(game)
        games.add(game)
        games.add(game)
        games.add(game)
        games.add(game)
        games.add(game)

        recycler_view = findViewById(R.id.game_list)
        recycler_view.apply{
            layoutManager = GridLayoutManager(this@HomePageActivity,1)
            adapter = ListAdapter(games)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.elements, menu)
        return true
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

}

data class Game(val name : String, val editeur : String, val prix : String,val image : String)

class ListAdapter(val games: MutableList<Game>) : RecyclerView.Adapter<GameViewHolder>() {

    override fun getItemCount(): Int = games.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.game_cell, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateGame(
            games[position]
        )
    }

}

class GameViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val game_name = v.findViewById<TextView>(R.id.nom)
    private val editeur = v.findViewById<TextView>(R.id.editeur)
    private val prix = v.findViewById<TextView>(R.id.prix)
    private val image = v.findViewById<ImageView>(R.id.image)

    fun updateGame(game: Game) {
        game_name.text = game.name
        editeur.text = game.editeur
        prix.text = game.prix

        val url = game.image

        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(itemView.context).load(url).into(image)
    }

}