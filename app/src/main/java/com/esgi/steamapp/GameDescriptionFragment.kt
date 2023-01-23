package com.esgi.steamapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class GameDescriptionFragment : Fragment(R.layout.fragment_game_description) {

    private lateinit var description : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        description = view.findViewById(R.id.description)

    }

}