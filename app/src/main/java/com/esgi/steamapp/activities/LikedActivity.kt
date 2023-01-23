package com.esgi.steamapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esgi.steamapp.Adapter.GameAdapter
import com.esgi.steamapp.R
import com.esgi.steamapp.model.Game

class LikedActivity : AppCompatActivity() {
    lateinit var recycler_view : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.liked_games)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val games = mutableListOf<Game>()

        val game = Game(name = "Counter Strike", editeur = "Gesco", prix = "10,00 $", image = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg?t=1668125812")
        games.add(game)

        recycler_view = findViewById(R.id.game_list)
        recycler_view.apply{
            layoutManager = GridLayoutManager(this@LikedActivity,1)
            adapter = GameAdapter(games, this.context)
        }
    }
}