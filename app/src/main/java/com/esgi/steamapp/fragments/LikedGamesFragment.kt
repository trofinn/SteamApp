package com.esgi.steamapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esgi.steamapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LikedGamesFragment : Fragment() {
    lateinit var recycler_view : RecyclerView
    lateinit var empty_likes : ImageView
    private lateinit var database : FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_liked_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance("https://steamapp-558cf-default-rtdb.europe-west1.firebasedatabase.app")
        recycler_view = view.findViewById(R.id.game_list)

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                findNavController().navigateUp()
            }
        })
        getGamesFromDatabase(object : GameListCallback {
            override fun onCallback(games_map: MutableMap<String, Game>) {
                val games_list = games_map.values.toMutableList()
                recycler_view.apply{
                    layoutManager = GridLayoutManager(context,1)
                    adapter = ListAdapter(games_list, object : OnProductListener {
                        override fun onClicked(game : Game, position : Int) {
                            val key = getKey(games_map,game)
                            findNavController().navigate(
                                LikedGamesFragmentDirections.actionLikedGamesFragmentToGameDetailsFragment(
                                    game.name,
                                    game.editeur,
                                    game.image,
                                    key!!,
                                    game.description))
                        }
                    })
                }
                empty_likes = view.findViewById(R.id.empty_likes)
                empty_likes.visibility = View.GONE
                if(games_list.isEmpty()) {
                    empty_likes.visibility = View.VISIBLE
                }
            }
        },"Likes")
    }
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