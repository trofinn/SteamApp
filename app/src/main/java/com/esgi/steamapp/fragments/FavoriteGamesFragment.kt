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
import com.google.firebase.database.FirebaseDatabase


class FavoriteGamesFragment : Fragment() {
    lateinit var recycler_view : RecyclerView
    lateinit var empty_favorites : ImageView
    private lateinit var database : FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_favorite_games, container, false)
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
                                FavoriteGamesFragmentDirections.actionFavoriteGamesFragmentToGameDetailsFragment(
                                    game.name,
                                    game.editeur,
                                    game.image,
                                    key!!,
                                    game.description))
                        }
                    })
                }
                empty_favorites = view.findViewById(R.id.empty_stars)
                empty_favorites.visibility = View.GONE
                if (games_list.isEmpty()) {
                    empty_favorites.visibility = View.VISIBLE
                }
            }
        },"Favorites")
    }
}