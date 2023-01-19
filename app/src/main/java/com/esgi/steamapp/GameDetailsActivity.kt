package com.esgi.steamapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.esgi.steamapp.databinding.GameDetailsLayoutBinding
import com.esgi.steamapp.databinding.HomePageBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GameDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_details_layout)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,GameDescriptionFragment()).commit()

        findViewById<Button>(R.id.description_button).setOnClickListener() {
            onClick(findViewById<Button>(R.id.description_button))
            replaceFragment(GameDescriptionFragment())
        }
        findViewById<Button>(R.id.avis_button).setOnClickListener {
            onClick(findViewById<Button>(R.id.avis_button))
            replaceFragment(GameAvisFragment())
        }

    }

    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.elements, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.like -> {
            true
        }

        R.id.favorite -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
private fun onClick(v : View) {
    var isPressed = true
    if(isPressed) {
        v.setBackgroundColor(R.color.button)
    }
    else {
        v.setBackgroundResource(R.color.dark_grey)
    }
    isPressed = !isPressed
}