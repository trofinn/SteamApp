package com.esgi.steamapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    lateinit var recyclerView : RecyclerView
    lateinit var emptyLikes : ImageView
    private lateinit var database : FirebaseDatabase
    private lateinit var email : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_liked_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance("https://steamapp-558cf-default-rtdb.europe-west1.firebasedatabase.app")
        recyclerView = view.findViewById(R.id.game_list)
        email = LikedGamesFragmentArgs.fromBundle(requireArguments()).username.toString()
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                findNavController().navigateUp()
            }
        })
        getGamesFromDatabase(object : GameListCallback {
            override fun onCallback(gamesMap: MutableMap<String, Game>) {
                val gamesList = gamesMap.values.toMutableList()
                recyclerView.apply{
                    layoutManager = GridLayoutManager(context,1)
                    adapter = ListAdapter(gamesList, object : OnProductListener {
                        override fun onClicked(game : Game, position : Int) {
                            val key = getKey(gamesMap,game)
                            findNavController().navigate(
                                LikedGamesFragmentDirections.actionLikedGamesFragmentToGameDetailsFragment(
                                    game.name,
                                    game.editeur,
                                    game.image,
                                    key!!,
                                    game.description,email))
                        }
                    })
                }
                emptyLikes = view.findViewById(R.id.empty_likes)
                emptyLikes.visibility = View.GONE
                if(gamesList.isEmpty()) {
                    emptyLikes.visibility = View.VISIBLE
                }
            }
        },"Likes", email)
    }


}

fun getGamesFromDatabase(myCallback: GameListCallback, reference : String, email : String) {
    val database = FirebaseDatabase.getInstance("https://steamapp-558cf-default-rtdb.europe-west1.firebasedatabase.app")
    val games : MutableMap<String, Game> = mutableMapOf()
    val list = email.split("@")
    val databaseReference = database.reference.child(list[0]).child(reference)
    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (snapshot : DataSnapshot in snapshot.children) {
                val key = snapshot.key
                val game = Game(snapshot.child("name").getValue().toString(),snapshot.child("dev").getValue().toString(),"", snapshot.child("photo").getValue().toString(), snapshot.child("description").getValue().toString())
                games.set(key.toString(), game)
            }
            myCallback.onCallback(games)
        }
        override fun onCancelled(error: DatabaseError) {}
    })
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

