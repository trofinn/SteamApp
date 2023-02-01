package com.esgi.steamapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esgi.steamapp.*
import com.esgi.steamapp.activity.ForgotPasswordActivity
import com.esgi.steamapp.activity.MainActivity
import com.esgi.steamapp.activity.SignUpActivity
import com.esgi.steamapp.model.MostPlayedGamesResponse
import com.esgi.steamapp.service.GameRetriever
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.util.*


class HomePageFragment : Fragment() {
    lateinit var moreInfoButton: Button
    lateinit var bigImage: ImageView
    lateinit var recyclerView: RecyclerView
    var listOfGameIds: MutableList<Int> = emptyList<Int>().toMutableList()
    val games = mutableListOf<Game>()
    val gamesMap = mutableMapOf<String, Game>()
    var gamesFiltered = mutableMapOf<String, Game>()

    //var rankList: MutableList<MostPlayedGamesResponse.Response.Rank> = mutableListOf()
    //var theGames: MutableList<JsonObject> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView = view.findViewById(R.id.game_list)

        val searchView = view.findViewById<SearchView>(R.id.search_bar)

        GlobalScope.launch(Dispatchers.Main) {
            view.findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            val response = coroutineScope {
                async { NetworkManagerGameList.getList() }
            }.await()

            val api_games = response.response.ranks
            for (i in api_games) {
                listOfGameIds.add(i.appid)
            }
            listOfGameIds = listOfGameIds.subList(0, 10)
            var gameDetails: JsonObject
            val gameRetriever = GameRetriever()
            var jsonObject: JsonObject

            coroutineScope {
                for (i in 0..listOfGameIds.size - 1) {
                    val game_id = listOfGameIds[i]
                    async {
                        jsonObject = gameRetriever.getAGame(game_id.toString())
                        gameDetails = jsonObject.get(game_id.toString()).asJsonObject
                        if (gameDetails.getAsJsonObject("data") != null) {
                            gameDetails = gameDetails.getAsJsonObject("data")
                            val game = Game(
                                name = gameDetails.get("name").asString,
                                editeur = gameDetails.get("publishers").asJsonArray.get(0).asString,
                                prix = if (gameDetails.get("price_overview") != null)
                                    gameDetails.get("price_overview").asJsonObject.get("initial_formatted").asString else
                                    "free",
                                image = gameDetails.get("header_image").asString,
                                description = gameDetails.get("short_description").asString
                            )
                            games.add(game)
                            gamesMap.set(game_id.toString(), game)
                        }
                    }

                }
            }
            Log.d("--------Response", "gameList size : ${listOfGameIds.size}")
            buildRecyclerView(recyclerView, gamesMap, requireContext())
            view.findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }.invokeOnCompletion {
            if (it == null) {
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        val new_games_list = filter(newText!!)
                        buildRecyclerView(recyclerView, new_games_list, requireContext())
                        return false
                    }
                })
            }
        }

        val url = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg?t=1668125812"
        bigImage = view.findViewById(R.id.big_game)
        bigImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(url).into(bigImage)

        moreInfoButton = view.findViewById(R.id.button)
        moreInfoButton.setOnClickListener() {
            findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToGameDetailsFragment(
                    view.findViewById<TextView>(R.id.big_game_name).text.toString(),
                    "[Valve, Hidden Path Entertainment]",
                    url,
                    "730",
                    view.findViewById<TextView>(R.id.description).text.toString()
                )
            )
        }
    }

    fun buildRecyclerView(
        recyclerView: RecyclerView,
        gamesMap: MutableMap<String, Game>,
        context: Context
    ): MutableList<Game> {
        val gamesList = gamesMap.values.toMutableList()
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = ListAdapter(gamesList, object : OnProductListener {
                override fun onClicked(game: Game, position: Int) {
                    val key = getKey(gamesMap, game)
                    findNavController().navigate(
                        HomePageFragmentDirections.actionHomePageFragmentToGameDetailsFragment(
                            game.name,
                            game.editeur,
                            game.image,
                            key!!,
                            game.description
                        )
                    )
                }
            })
        }
        return gamesList
    }

    private fun filter(newText: String): MutableMap<String, Game> {
        gamesFiltered.clear()
        val searchText = newText.lowercase(Locale.getDefault())
        if (searchText.isNotEmpty()) {

            gamesMap.forEach {
                if (it.value.name.lowercase(Locale.getDefault()).contains(searchText)) {
                    gamesFiltered.set(it.key, it.value)
                }
            }

            if (gamesFiltered.isEmpty()) {
                Toast.makeText(
                    requireContext(), "No Data Found..",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //recycler_view.adapter!!.notifyDataSetChanged()
            }

        } else {
            gamesFiltered.clear()
            gamesFiltered = gamesMap
            //recycler_view.adapter!!.notifyDataSetChanged()
        }
        return gamesFiltered
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.elements, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.like -> {
            findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToLikedGamesFragment()
            )
            true
        }

        R.id.favorite -> {
            findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToFavoriteGamesFragment()
            )
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}

data class Game(
    val name: String,
    val editeur: String,
    val prix: String,
    val image: String,
    val description: String
)

class ListAdapter(val games: MutableList<Game>, val listener: OnProductListener) :
    RecyclerView.Adapter<GameViewHolder>() {

    override fun getItemCount(): Int = games.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.game_cell, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.updateGame(game)
        holder.button.setOnClickListener() {
            listener.onClicked(game, position)
        }
    }

}

class GameViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val gameName = v.findViewById<TextView>(R.id.nom)
    private val editeur = v.findViewById<TextView>(R.id.editeur)
    private val prix = v.findViewById<TextView>(R.id.prix)
    private val image = v.findViewById<ImageView>(R.id.image)
    val button = v.findViewById<Button>(R.id.en_savoir_plus_button)


    fun updateGame(game: Game) {
        gameName.text = game.name
        editeur.text = game.editeur
        prix.text = game.prix

        val url = game.image

        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(itemView.context)
            .load(url)
            .centerCrop()
            .into(image)
    }
}

interface OnProductListener {
    fun onClicked(game: Game, position: Int) {

    }
}