package com.esgi.steamapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.esgi.steamapp.R
import com.esgi.steamapp.model.Game
import com.esgi.steamapp.model.Games

class GameAdapter(games: MutableList<Games.Result.Data>, val context: Context) :
    RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    private var games: MutableList<Games.Result.Data>
    init {
        this.games = games
    }

    override fun getItemCount(): Int = games.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.game_cell, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateGame(
            games[position]
        )
    }

    fun filterGame(filterList: MutableList<Games.Result.Data>) {
        games = filterList
        notifyDataSetChanged()
    }

    class GameViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val game_name = v.findViewById<TextView>(R.id.nom)
        private val editeur = v.findViewById<TextView>(R.id.editeur)
        private val prix = v.findViewById<TextView>(R.id.prix)
        private val image = v.findViewById<ImageView>(R.id.image)

        fun updateGame(game: Games.Result.Data) {
            game_name.text = game.name

            val url = game.header_image

            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(itemView.context).load(url).into(image)
        }

    }

}