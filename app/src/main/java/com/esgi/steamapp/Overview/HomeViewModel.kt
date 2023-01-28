package com.esgi.steamapp.Overview

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esgi.steamapp.Network.GamesApi
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<String>()

    // The external immutable LiveData for the request status
    val status: LiveData<String> = _status
    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getGames()
    }



    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     *
     */
     fun getGames() {
        viewModelScope.launch {
            try {
                val listResult = GamesApi.retrofitService.getSteamGames()
                _status.value = "--------------Success: ${listResult.size} Mars photos retrieved"
                println(_status.value)
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }
}