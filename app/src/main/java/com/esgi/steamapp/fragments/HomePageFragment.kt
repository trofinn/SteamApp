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
    lateinit var more_info_button : Button
    lateinit var big_image : ImageView
    lateinit var recycler_view : RecyclerView
    var list_of_game_ids : MutableList<Int> = emptyList<Int>().toMutableList()
    val games = mutableListOf<Game>()
    val games_map = mutableMapOf<String, Game>()
    var list_of_game_ids_test : MutableList<String> = emptyList<String>().toMutableList()
    var game_filtered = mutableMapOf<String, Game>()

    var rankList : MutableList<MostPlayedGamesResponse.Response.Rank> = mutableListOf()
    var theGames : MutableList<JsonObject> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recycler_view = view.findViewById(R.id.game_list)
        list_of_game_ids_test.add("730")
        list_of_game_ids_test.add("578080")

        val searchView = view.findViewById<SearchView>(R.id.search_bar)

        GlobalScope.launch(Dispatchers.Main) {
            view.findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
            recycler_view.visibility = View.GONE

            val response = withContext(Dispatchers.Default) {
                NetworkManagerGameList.getList()
            }
            val api_games = response.response.ranks
            for (i in api_games) {
                list_of_game_ids.add(i.appid)
            }

            var game_details: JsonObject
            var gameRetriever = GameRetriever()
            var deferred: Deferred<JsonObject>
            withContext(Dispatchers.Default) {
                for(game_id in list_of_game_ids) {
                    deferred = async { gameRetriever.getAGame(game_id.toString()) }

                    game_details = deferred.await().get(game_id.toString()).asJsonObject
                    if (game_details.getAsJsonObject("data") != null) {
                        game_details = game_details.getAsJsonObject("data")
                        val game = Game(
                            name = game_details.get("name").asString,
                            editeur = game_details.get("publishers").asJsonArray.get(0).asString
                            , prix = if (game_details.get("price_overview") != null)
                                game_details.get("price_overview").asJsonObject.get("initial_formatted").asString else
                                "free",
                            image = game_details.get("header_image").asString,
                            description = game_details.get("short_description").asString)

                        games.add(game)
                        games_map.set(game_id.toString(),game)
                    }



                }
            }
            Log.d("--------Response", "gameList size : ${list_of_game_ids.size}")



            buildRecyclerView(recycler_view,games_map,requireContext())
            view.findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
        }.invokeOnCompletion {
            if(it == null) {
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        val new_games_list = filter(newText!!)
                        buildRecyclerView(recycler_view,new_games_list,requireContext())
                        return false
                    }
                })
            }
        }


        /*
        GlobalScope.launch(Dispatchers.Main) {
            val response2 = withContext(Dispatchers.Default) {
                val gameService = RetrofitClientInstance
                    .getRetrofitInstance(ApiJavaTest::class.java, GetItemDetailsDeserializer())
                    .create(GameService::class.java)
                val test = gameService.getDetailsOfGame("730")
            }
        }
         */

        val url = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg?t=1668125812"
        big_image = view.findViewById(R.id.big_game)
        big_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(url).into(big_image)

        more_info_button = view.findViewById(R.id.button)
        more_info_button.setOnClickListener() {
            findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToGameDetailsFragment(
                    view.findViewById<TextView>(R.id.big_game_name).text.toString(),
                    "[Valve, Hidden Path Entertainment]",
                    url,
                    "730",
                    view.findViewById<TextView>(R.id.description).text.toString()))
        }

    }

    fun buildRecyclerView(recycler_view: RecyclerView, games_map: MutableMap<String, Game>, context: Context) : MutableList<Game> {
        val games_list = games_map.values.toMutableList()
        recycler_view.apply{
            layoutManager = GridLayoutManager(context,1)
            adapter = ListAdapter(games_list, object : OnProductListener {
                override fun onClicked(game : Game, position : Int) {
                    val key = getKey(games_map,game)
                    findNavController().navigate(
                        HomePageFragmentDirections.actionHomePageFragmentToGameDetailsFragment(
                            game.name,
                            game.editeur,
                            game.image,
                            key!!,
                            game.description))
                }
            })
        }
        return games_list
    }

    private fun filter(newText: String) : MutableMap<String, Game>{
        game_filtered.clear()
        val searchText = newText.lowercase(Locale.getDefault())
        if (searchText.isNotEmpty()) {

            games_map.forEach {
                if (it.value.name.lowercase(Locale.getDefault()).contains(searchText)) {
                    game_filtered.set(it.key,it.value)
                }
            }

            if (game_filtered.isEmpty()) {
                Toast.makeText(requireContext(), "No Data Found..",
                    Toast.LENGTH_SHORT).show()
            }
            else {
                //recycler_view.adapter!!.notifyDataSetChanged()
            }

        }
        else {
            game_filtered.clear()
            game_filtered = games_map
            //recycler_view.adapter!!.notifyDataSetChanged()
        }
        return game_filtered
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater : MenuInflater) {
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

data class Game(val name : String, val editeur : String, val prix : String,val image : String, val description : String)

class ListAdapter(val games: MutableList<Game>, val listener : OnProductListener) : RecyclerView.Adapter<GameViewHolder>() {

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

    private val game_name = v.findViewById<TextView>(R.id.nom)
    private val editeur = v.findViewById<TextView>(R.id.editeur)
    private val prix = v.findViewById<TextView>(R.id.prix)
    private val image = v.findViewById<ImageView>(R.id.image)
    val button = v.findViewById<Button>(R.id.en_savoir_plus_button)


    fun updateGame(game: Game) {
        game_name.text = game.name
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
    fun onClicked(game : Game, position : Int) {

    }
}

class SignInFragment : Fragment() {
    lateinit var email : EditText
    lateinit var password : EditText
    private lateinit var login_button : Button
    private lateinit var forgot_password : TextView
    private lateinit var signup_button : Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        login_button = view.findViewById(R.id.login)
        forgot_password = view.findViewById(R.id.forgot_password)
        signup_button = view.findViewById(R.id.new_account)
        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener() {
            login()
        }

        forgot_password.setOnClickListener() {
            val intent = Intent(requireContext(), ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        signup_button.setOnClickListener() {
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val email = email.text.toString().trim()
        val password = password.text.toString().trim()
        if(email.isBlank() || password.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Tous les champs sont obligatoires",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth.signInWithEmailAndPassword(email.trim(),password.trim()).addOnCompleteListener() {
                task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Connexion Success", Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    SignInFragmentDirections.actionSignInFragmentToHomePageFragment(email,"","","",""))
            }
            else {
                Log.w(ContentValues.TAG, "createUserWithEmail:failure ", task.exception)
                Toast.makeText(requireContext(), "Connexion Echoué", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class SignUpFragment : Fragment() {
    lateinit var user_name : EditText
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var password_verification : EditText
    private lateinit var sign_up : Button
    private lateinit var back_button : ImageButton
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user_name = view.findViewById(R.id.user_name)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        password_verification = view.findViewById(R.id.password_verification)
        sign_up = view.findViewById(R.id.new_account)
        back_button = view.findViewById(R.id.back_button)
        auth = FirebaseAuth.getInstance()
        sign_up.setOnClickListener() {
            signUpUser()
        }

        back_button.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser() {
        var user_name = user_name.text.toString()
        var email = email.text.toString()
        var password = password.text.toString()
        var password_verification = password_verification.text.toString()

        if (user_name.isBlank() || email.isBlank() || password.isBlank() || password_verification.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Tous les champs sont obligatoires",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (password != password_verification) {
            Toast.makeText(
                requireContext(),
                "Le mot de passe ne corresponde pas",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener() {
            if (it.isSuccessful) {
                startActivity(Intent(requireContext(), SignUpActivity::class.java))
                Toast.makeText(requireContext(), "Inscription complete", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(), "Inscription echoué", Toast.LENGTH_SHORT).show()
            }
        }
    }
}