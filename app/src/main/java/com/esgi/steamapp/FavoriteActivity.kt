package com.esgi.steamapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esgi.steamapp.databinding.ActivityFavoriteBinding
import com.esgi.steamapp.databinding.LikedGamesBinding
import com.google.firebase.database.FirebaseDatabase

class FavoriteActivity : AppCompatActivity() {
    lateinit var recycler_view : RecyclerView
    lateinit var empty_favorites : ImageView
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        database = FirebaseDatabase.getInstance("https://steamapp-558cf-default-rtdb.europe-west1.firebasedatabase.app")
        recycler_view = findViewById(R.id.game_list)

        getGamesFromDatabase(object : GameListCallback {
            override fun onCallback(games_map: MutableMap<String, Game>) {
                val game_list = buildRecyclerView(recycler_view,games_map,this@FavoriteActivity)
                empty_favorites = findViewById(R.id.empty_stars)
                binding.emptyStars.visibility = View.GONE
                if (game_list.isEmpty()) {
                    binding.emptyStars.visibility = View.VISIBLE
                }
            }
        },"Favorites")
    }
}