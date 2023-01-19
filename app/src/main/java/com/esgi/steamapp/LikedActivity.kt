package com.esgi.steamapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esgi.steamapp.databinding.HomePageBinding
import com.esgi.steamapp.databinding.LikedGamesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LikedActivity : AppCompatActivity() {
    lateinit var recycler_view : RecyclerView
    lateinit var empty_likes : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = LikedGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val games = mutableListOf<Game>()
        empty_likes = findViewById(R.id.empty_likes)

        binding.emptyLikes.visibility = View.GONE

        recycler_view = findViewById(R.id.game_list)
        recycler_view.apply{
            layoutManager = GridLayoutManager(this@LikedActivity,1)
            adapter = ListAdapter(games, object : OnProductListener {
                override fun onClicked(game : Game, position : Int) {
                    Toast.makeText(
                        this@LikedActivity,
                        "Game $position clicked",
                        Toast.LENGTH_SHORT).show();
                    val intent = Intent(this@LikedActivity,GameDetailsActivity::class.java)
                    startActivity(intent)
                }
            });
        }

        if(games.isEmpty()) {
            binding.emptyLikes.visibility = View.VISIBLE
        }
    }
}