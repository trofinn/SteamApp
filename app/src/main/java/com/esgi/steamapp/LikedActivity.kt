package com.esgi.steamapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isGone
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esgi.steamapp.databinding.HomePageBinding
import com.esgi.steamapp.databinding.LikedGamesBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

class LikedActivity : AppCompatActivity() {
    lateinit var recycler_view : RecyclerView
    lateinit var empty_likes : ImageView
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = LikedGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        database = FirebaseDatabase.getInstance("https://steamapp-558cf-default-rtdb.europe-west1.firebasedatabase.app")
        recycler_view = findViewById(R.id.game_list)
        getGamesFromDatabase(object : GameListCallback {
           override fun onCallback(games_map: MutableMap<String, Game>) {
               val game_list = buildRecyclerView(recycler_view,games_map,this@LikedActivity)
               empty_likes = findViewById(R.id.empty_likes)
               binding.emptyLikes.visibility = View.GONE
               if(game_list.isEmpty()) {
                   binding.emptyLikes.visibility = View.VISIBLE
               }
           }
       },"Likes")
    }
}

fun buildRecyclerView(recycler_view : RecyclerView, games_map: MutableMap<String, Game>, context : Context) : MutableList<Game> {
    val games_list = games_map.values.toMutableList()
    recycler_view.apply{
        layoutManager = GridLayoutManager(context,1)
        adapter = ListAdapter(games_list, object : OnProductListener {
            override fun onClicked(game : Game, position : Int) {
                val key = getKey(games_map,game)
                Toast.makeText(
                    context,
                    "Game $position clicked",
                    Toast.LENGTH_SHORT).show();
                val intent = Intent(context,GameDetailsActivity::class.java)
                intent.putExtra("game_name", game.name)
                intent.putExtra("game_editor", game.editeur)
                intent.putExtra("game_image", game.image)
                intent.putExtra("game_id",key!!)
                intent.putExtra("game_description", game.description)
                context.startActivity(intent)
            }
        })
    }
    return games_list
}


interface GameListCallback {
    fun onCallback(games : MutableMap<String, Game>)
}
fun <K, V> getKey(map: Map<K, V>, target: V): K? {
    for ((key, value) in map)
    {
        if (target == value) {
            return key
        }
    }
    return null
}

fun getGamesFromDatabase(myCallback: GameListCallback, reference : String) {
    val database = FirebaseDatabase.getInstance("https://steamapp-558cf-default-rtdb.europe-west1.firebasedatabase.app")
    val games : MutableMap<String, Game> = mutableMapOf()
    val databaseReference = database.reference.child(reference)
    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (child : DataSnapshot in snapshot.children) {
                val key = child.key
                val value = child.getValue().toString()
                val game = Game(child.child("name").getValue().toString(),child.child("dev").getValue().toString(),"", child.child("photo").getValue().toString(), child.child("description").getValue().toString())
                games.set(key.toString(), game)
            }
            myCallback.onCallback(games)
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}