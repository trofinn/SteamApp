package com.esgi.steamapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.bumptech.glide.Glide
import com.esgi.steamapp.databinding.GameDetailsLayoutBinding
import com.esgi.steamapp.databinding.HomePageBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class GameDetailsActivity : AppCompatActivity() {
    private lateinit var game_name : TextView
    private lateinit var game_editor : TextView
    private lateinit var game_photo_link : String
    private lateinit var game_photo : ImageView
    private lateinit var game_description : TextView

    private var game_id : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_details_layout)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        game_name = findViewById(R.id.nom)
        game_editor = findViewById(R.id.editeur)
        game_photo = findViewById(R.id.image)
        game_description = findViewById(R.id.data_transition_view)

        game_name.text =  intent.extras?.getString("game_name")
        game_editor.text = intent.extras?.getString("game_editor")
        game_description.text = intent.extras?.getString("game_description")
        game_photo_link = intent.extras?.getString("game_image").toString()
        game_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(game_photo_link).into(game_photo)

        game_photo = findViewById(R.id.mini_image)
        Glide.with(this).load(game_photo_link).centerCrop().into(game_photo)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,GameDescriptionFragment().newInstance(game_description.text.toString())).commit()

        findViewById<Button>(R.id.description_button).setOnClickListener() {
            //onClick(findViewById<Button>(R.id.description_button))
            replaceFragment(GameDescriptionFragment().newInstance(game_description.text.toString()))
        }
        findViewById<Button>(R.id.avis_button).setOnClickListener {
            //onClick(findViewById<Button>(R.id.avis_button))
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