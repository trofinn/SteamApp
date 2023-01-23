package com.esgi.steamapp.model

data class Rank(
    val appid: Int,
    val last_week_rank: Int,
    val peak_in_game: Int,
    val rank: Int
)