package com.esgi.steamapp

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class GameDescriptionFragment : Fragment(R.layout.fragment_game_description) {

    private lateinit var description : TextView

    public fun newInstance(game_description : String) : GameDescriptionFragment {
        val fragment = GameDescriptionFragment()
        val bundle = Bundle()
        bundle.putString("game_description", game_description)
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_game_description, container, false)
        description = view.findViewById(R.id.description)
        val game_description = arguments?.getString("game_description")
        description.text = game_description
        return view
    }


}