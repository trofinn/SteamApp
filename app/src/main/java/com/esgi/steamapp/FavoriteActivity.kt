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

class FavoriteActivity : AppCompatActivity() {
    lateinit var recycler_view : RecyclerView
    lateinit var empty_likes : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val games = mutableListOf<Game>()
        empty_likes = findViewById(R.id.empty_starts)

        recycler_view = findViewById(R.id.game_list)
        recycler_view.apply{
            layoutManager = GridLayoutManager(this@FavoriteActivity,1)
            adapter = ListAdapter(games, object : OnProductListener {
                override fun onClicked(game : Game, position : Int) {
                    Toast.makeText(
                        this@FavoriteActivity,
                        "Game $position clicked",
                        Toast.LENGTH_SHORT).show();
                    val intent = Intent(this@FavoriteActivity,GameDetailsActivity::class.java)
                    startActivity(intent)
                }
            });
        }

        if(games.isEmpty()) {
            binding.emptyStarts.visibility = View.VISIBLE
        }
    }
}