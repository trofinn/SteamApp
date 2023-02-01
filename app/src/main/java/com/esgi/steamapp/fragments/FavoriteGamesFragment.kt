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
    lateinit var recyclerView : RecyclerView
    lateinit var emptyFavorites : ImageView
    private lateinit var database : FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_favorite_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance("https://steamapp-558cf-default-rtdb.europe-west1.firebasedatabase.app")
        recyclerView = view.findViewById(R.id.game_list)

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
                                FavoriteGamesFragmentDirections.actionFavoriteGamesFragmentToGameDetailsFragment(
                                    game.name,
                                    game.editeur,
                                    game.image,
                                    key!!,
                                    game.description))
                        }
                    })
                }
                emptyFavorites = view.findViewById(R.id.empty_stars)
                emptyFavorites.visibility = View.GONE
                if (gamesList.isEmpty()) {
                    emptyFavorites.visibility = View.VISIBLE
                }
            }
        },"Favorites")
    }
}