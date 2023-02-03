package com.esgi.steamapp.fragments

import android.app.Activity
import android.content.Context
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
import com.esgi.steamapp.NetworkManagerGameList
import com.esgi.steamapp.R
import com.esgi.steamapp.model.GameDetailResponse
import com.esgi.steamapp.model.SearchGameResponse
import com.esgi.steamapp.service.GameRetriever
import com.firebase.ui.auth.AuthUI
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
    lateinit var searchView: SearchView
    lateinit var searchedGamesList : RecyclerView
    val gameRetriever: GameRetriever = GameRetriever()
    lateinit var email : String

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
        searchedGamesList = view.findViewById(R.id.search_game_list)
        searchView = view.findViewById<SearchView>(R.id.search_bar)
        email = HomePageFragmentArgs.fromBundle(requireArguments()).username.toString()
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
            listOfGameIds = listOfGameIds.subList(0, 15)
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
                                    gameDetails.get("price_overview").asJsonObject.get("final_formatted").asString else
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
                        view.findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        GlobalScope.launch {
                            filter(query!!)
                        }

                        view.findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE

                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        //val new_games_list = filter(newText!!)

                        //buildRecyclerView(recyclerView, new_games_list, requireContext())
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
                    view.findViewById<TextView>(R.id.description).text.toString(),email
                )
            )
        }
    }

    fun buildRecyclerView(
        recyclerView: RecyclerView,
        gamesMap: MutableMap<String, Game>,
        context: Context
    ): MutableList<Game> {
        val list = email.split("@")
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
                            game.description,email
                        )
                    )
                }
            })
        }
        return gamesList
    }

    private suspend fun filter(newText: String) {
        val list = email.split("@")
        gamesFiltered.clear()
        val searchText = newText.lowercase(Locale.getDefault())
        val errorHandler = CoroutineExceptionHandler  {
                coroutineContext, throwable ->
            throwable.printStackTrace()
            Toast.makeText(this.context, "Error", Toast.LENGTH_SHORT).show()
        }
        if (searchText.isNotEmpty()) {
            var gameDetails : JsonObject
            var result : List<SearchGameResponse>
            try {

                GlobalScope.launch(Dispatchers.IO) {
                    result = async { gameRetriever.searchGame(searchText) }.await()
                    println("---- SIZE ------${result.size}")
                    result.forEach {
                        gameDetails = async { gameRetriever.getAGame(it.appid) }
                            .await().getAsJsonObject(it.appid)
                        if (gameDetails.getAsJsonObject("data") != null) {
                            gameDetails = gameDetails.getAsJsonObject("data")
                            gamesFiltered[it.appid] = Game(
                                name = gameDetails.get("name").asString,
                                editeur = gameDetails.get("publishers").asJsonArray.get(0).asString,
                                prix = if (gameDetails.get("price_overview") != null)
                                    gameDetails.get("price_overview").asJsonObject.get("final_formatted").asString else
                                    "free",
                                image = gameDetails.get("header_image").asString,
                                description = gameDetails.get("short_description").asString)
                        }


                    }
                    if (gamesFiltered.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(), "No Data Found..",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        withContext(Dispatchers.Main) {
                            println(gamesFiltered.size)
                            recyclerView.adapter = ListAdapter(gamesFiltered.values.toMutableList(),
                                object : OnProductListener {
                                    override fun onClicked(game: Game, position: Int) {
                                        val key = getKey(gamesFiltered, game)
                                        findNavController().navigate(
                                            HomePageFragmentDirections.actionHomePageFragmentToGameDetailsFragment(
                                                game.name,
                                                game.editeur,
                                                game.image,
                                                key!!,
                                                game.description,email
                                            )
                                        )
                                    }
                                })
                        }

                    }
                }

            }
            catch (e: Exception) {
                println(e.message)
            }




        } else {
            gamesFiltered.clear()
            gamesFiltered.putAll(gamesMap)
            recyclerView.adapter = ListAdapter(gamesFiltered.values.toMutableList(),
                object : OnProductListener {
                    override fun onClicked(game: Game, position: Int) {
                        val key = getKey(gamesMap, game)
                        findNavController().navigate(
                            HomePageFragmentDirections.actionHomePageFragmentToGameDetailsFragment(
                                game.name,
                                game.editeur,
                                game.image,
                                key!!,
                                game.description,email
                            )
                        )
                    }
                })
        }
        //return gamesFiltered
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.elements, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.like -> {
            val list = email.split("@")
            findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToLikedGamesFragment(email)
            )
            true
        }

        R.id.favorite -> {
            val list = email.split("@")
            findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToFavoriteGamesFragment(email)
            )
            true
        }
        R.id.sign_out -> {
            AuthUI.getInstance()
                .signOut(requireContext())
            findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToSignInFragment("")
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

class ListAdapter(games: MutableList<Game>, val listener: OnProductListener) :
    RecyclerView.Adapter<GameViewHolder>() {

    private var games: MutableList<Game>
    override fun getItemCount(): Int = games.size

    init {
        this.games = games
    }

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
