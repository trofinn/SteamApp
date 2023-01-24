package com.esgi.steamapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esgi.steamapp.databinding.ActivityFavoriteBinding.inflate
import com.esgi.steamapp.databinding.FragmentGameAvisBinding
import com.esgi.steamapp.databinding.HomePageBinding
import kotlinx.coroutines.*
import java.lang.Runnable

class GameAvisFragment : Fragment(R.layout.fragment_game_avis) {

    private val avis_list = mutableListOf<Avis>()
    private lateinit var recycler_view : RecyclerView
    private lateinit var game_id : String

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
        game_id = arguments?.getString("game_id").toString()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(Dispatchers.Main) {
            var progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
            var avis_list_rview = view.findViewById<RecyclerView>(R.id.avis_list)
            progressbar.visibility = View.VISIBLE
            avis_list_rview.visibility = View.INVISIBLE

            val response = withContext(Dispatchers.Default) {
                NetworkManagerAvisList.getListAvis(game_id)
            }
            withContext(Dispatchers.Default) {
                val avis = response.reviews
                for(i in avis) {
                    avis_list.add(i)
                    println("aaaaaaa ${i.review}")
                }

                activity?.runOnUiThread(java.lang.Runnable {
                    recycler_view = view.findViewById(R.id.avis_list)
                    recycler_view.apply {
                        layoutManager = GridLayoutManager(activity,1)
                        adapter = AvisListAdapter(avis_list)
                    }
                })
            }
            progressbar.visibility = View.INVISIBLE
            avis_list_rview.visibility = View.VISIBLE
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