package com.esgi.steamapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esgi.steamapp.Avis
import com.esgi.steamapp.NetworkManagerAvisList
import com.esgi.steamapp.R
import kotlinx.coroutines.*

class GameAvisFragment : Fragment(R.layout.fragment_game_avis) {

    private val avisList = mutableListOf<Avis>()
    private lateinit var recyclerView : RecyclerView
    private lateinit var gameId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    public fun newInstance(game_id : String) : GameAvisFragment {
        val fragment = GameAvisFragment()
        val bundle = Bundle()
        bundle.putString("game_id", game_id)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_game_avis, container, false)
        gameId = arguments?.getString("game_id").toString()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(Dispatchers.Main) {
            val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
            recyclerView = view.findViewById(R.id.avis_list)
            progressbar.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE

            val response = withContext(Dispatchers.Default) {
                NetworkManagerAvisList.getListAvis(gameId)
            }
            withContext(Dispatchers.Default) {
                val avis = response.reviews
                for(i in avis) {
                    avisList.add(i)
                }

                activity?.runOnUiThread(java.lang.Runnable {
                    recyclerView = view.findViewById(R.id.avis_list)
                    recyclerView.apply {
                        layoutManager = GridLayoutManager(activity,1)
                        adapter = AvisListAdapter(avisList)
                    }
                })
            }
            progressbar.visibility = View.INVISIBLE
            recyclerView.visibility = View.VISIBLE
        }
    }
}

class AvisListAdapter(val avis: MutableList<Avis>) : RecyclerView.Adapter<AvisViewHolder>() {

    override fun getItemCount(): Int = avis.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvisViewHolder {
        return AvisViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.avis_cell, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AvisViewHolder, position: Int) {
        val avis = avis[position]
        holder.updateAvis(avis)
    }

}

class AvisViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val utilisateur = v.findViewById<TextView>(R.id.utilisateur)
    private val text = v.findViewById<TextView>(R.id.avis)


    fun updateAvis(avis: Avis) {
        utilisateur.text = avis.author.steamid
        text.text = avis.review
    }
}