package com.esgi.steamapp.repository

import com.esgi.steamapp.fragments.Game

class Repository {
    var games: MutableMap<String, Game> = mutableMapOf()
        get() = field
        set(value) {
            games = value
        }
}