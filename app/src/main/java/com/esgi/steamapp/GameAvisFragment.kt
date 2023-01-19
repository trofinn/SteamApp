package com.esgi.steamapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GameAvisFragment : Fragment(R.layout.fragment_game_avis) {

    private val avis_list = mutableListOf<Avis>()
    private val avis1 = Avis("gesco","text");
    private lateinit var recycler_view : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        avis_list.add(avis1)
        avis_list.add(avis1)
        avis_list.add(avis1)
        avis_list.add(avis1)
        avis_list.add(avis1)

        recycler_view = view.findViewById(R.id.avis_list)
        recycler_view.apply {
            layoutManager = GridLayoutManager(activity,1)
            adapter = AvisListAdapter(avis_list)
        }
    }
}

data class Avis(val author : String, val avis : String)


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
        utilisateur.text = avis.author
        text.text = avis.avis
    }
}