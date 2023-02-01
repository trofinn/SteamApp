package com.esgi.steamapp.model

data class MostPlayedGamesResponse(
    val response: Response
) {
    data class Response(
        val ranks: List<Rank>,
        val rollup_date: Int
    ) {
        data class Rank(
            val appid: Int,
            val last_week_rank: Int,
            val peak_in_game: Int,
            val rank: Int
        )
    }
}