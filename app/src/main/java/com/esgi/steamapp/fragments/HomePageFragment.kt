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
import com.esgi.steamapp.activity.MainActivity
import com.esgi.steamapp.activity.SignUpActivity
import com.esgi.steamapp.service.GameRetriever
import com.firebase.ui.auth.AuthUI
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

class SignInFragment : Fragment() {
    lateinit var email: EditText
    lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPassword: TextView
    private lateinit var signupButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.login)
        forgotPassword = view.findViewById(R.id.forgot_password)
        signupButton = view.findViewById(R.id.new_account)
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener() {
            login()
        }

        forgotPassword.setOnClickListener() {
            findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment(email.text.toString())
            )
        }

        signupButton.setOnClickListener() {
            findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToSignUpFragment(email.text.toString(),password.text.toString())
            )
        }
    }

    private fun login() {
        val email = email.text.toString().trim()
        val password = password.text.toString().trim()
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Tous les champs sont obligatoires",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Connexion Success", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        SignInFragmentDirections.actionSignInFragmentToHomePageFragment(
                            email,
                            "",
                            "",
                            "",
                            ""
                        )
                    )
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure ", task.exception)
                    Toast.makeText(requireContext(), "Connexion Echoué", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

class SignUpFragment : Fragment() {
    lateinit var userName: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var passwordVerification: EditText
    private lateinit var signUp: Button
    private lateinit var backButton: ImageButton
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = view.findViewById(R.id.user_name)
        email = view.findViewById(R.id.email)
        email.setText(SignUpFragmentArgs.fromBundle(requireArguments()).email)
        password = view.findViewById(R.id.password)
        password.setText(SignUpFragmentArgs.fromBundle(requireArguments()).password)
        passwordVerification = view.findViewById(R.id.password_verification)
        signUp = view.findViewById(R.id.new_account)
        backButton = view.findViewById(R.id.back_button)
        auth = FirebaseAuth.getInstance()
        signUp.setOnClickListener() {
            signUpUser()
        }

        backButton.setOnClickListener() {
            findNavController().navigateUp()
        }
    }

    private fun signUpUser() {
        val userName = userName.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()
        val passwordVerification = passwordVerification.text.toString()

        if (userName.isBlank() || email.isBlank() || password.isBlank() || passwordVerification.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Tous les champs sont obligatoires",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (password != passwordVerification) {
            Toast.makeText(
                requireContext(),
                "Le mot de passe ne corresponde pas",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {
            if (it.isSuccessful) {
                startActivity(Intent(requireContext(), SignUpActivity::class.java))
                Toast.makeText(requireContext(), "Inscription complete", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Inscription echoué", Toast.LENGTH_SHORT).show()
            }
        }
    }
}