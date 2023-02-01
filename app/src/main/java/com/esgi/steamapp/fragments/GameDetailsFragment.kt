package com.esgi.steamapp.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.esgi.steamapp.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GameDetailsFragment : Fragment() {
    private lateinit var gameName : TextView
    private lateinit var gameEditor : TextView
    private lateinit var gamePhotoLink : String
    private lateinit var gamePhoto : ImageView
    private lateinit var gameDescription : TextView
    private lateinit var gameId : String
    private lateinit var database : FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_game_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        database = FirebaseDatabase.getInstance("https://steamapp-558cf-default-rtdb.europe-west1.firebasedatabase.app")
        gameName = view.findViewById(R.id.nom)
        gameEditor = view.findViewById(R.id.editeur2)
        gamePhoto = view.findViewById(R.id.image)
        gameDescription = view.findViewById(R.id.data_transition_view)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                findNavController().navigateUp()
            }
        })


        gameName.text =  GameDetailsFragmentArgs.fromBundle(requireArguments()).gameName
        gameId = GameDetailsFragmentArgs.fromBundle(requireArguments()).gameId
        gameEditor.text = GameDetailsFragmentArgs.fromBundle(requireArguments()).gameEditor
        gameDescription.text = GameDetailsFragmentArgs.fromBundle(requireArguments()).gameDescription
        gamePhotoLink = GameDetailsFragmentArgs.fromBundle(requireArguments()).gameImage
        gamePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);


        Glide.with(this).load(gamePhotoLink).into(gamePhoto)

        gamePhoto = view.findViewById(R.id.mini_image)
        Glide.with(this).load(gamePhotoLink).centerCrop().into(gamePhoto)
        replaceFragment(GameDescriptionFragment().newInstance(gameDescription.text.toString()))
        view.findViewById<Button>(R.id.description_button).setOnClickListener() {
            //onClick(findViewById<Button>(R.id.description_button))
            replaceFragment(GameDescriptionFragment().newInstance(gameDescription.text.toString()))
        }
        view.findViewById<Button>(R.id.avis_button).setOnClickListener {
            //onClick(findViewById<Button>(R.id.avis_button))
            replaceFragment(GameAvisFragment().newInstance(gameId.toString()))
        }
    }
    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater : MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.elements, menu)
        val database_likes = database.getReference().child("Likes")
        val database_favorites = database.getReference().child("Favorites")
        database_likes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(gameId).exists()) {
                    menu.getItem(0).setIcon(R.drawable.like_full)
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })
        database_favorites.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(gameId).exists()) {
                    menu.getItem(1).setIcon(R.drawable.whishlist_full)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId)  {
        R.id.like -> {
            val databaseLikes = database.getReference().child("Likes")
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) {
                        item.setIcon(R.drawable.like_full)
                        databaseLikes.child(gameId).child("name").setValue(gameName.text)
                        databaseLikes.child(gameId).child("dev").setValue(gameEditor.text)
                        databaseLikes.child(gameId).child("photo").setValue(gamePhotoLink)
                        databaseLikes.child(gameId).child("description").setValue(gameDescription.text)
                    }
                    else {
                        item.setIcon(R.drawable.like)
                        databaseLikes.child(gameId).removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(ContentValues.TAG, error.getMessage());
                }
            }
            databaseLikes.child(gameId).addListenerForSingleValueEvent(eventListener)
            true
        }

        R.id.favorite -> {
            val databaseFavorites = database.getReference().child("Favorites")
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) {
                        item.setIcon(R.drawable.whishlist_full)
                        databaseFavorites.child(gameId).child("name").setValue(gameName.text)
                        databaseFavorites.child(gameId).child("dev").setValue(gameEditor.text)
                        databaseFavorites.child(gameId).child("photo").setValue(gamePhotoLink)
                        databaseFavorites.child(gameId).child("description").setValue(gameDescription.text)
                    }
                    else {
                        item.setIcon(R.drawable.whishlist)
                        databaseFavorites.child(gameId).removeValue()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d(ContentValues.TAG, error.getMessage());
                }
            }
            databaseFavorites.child(gameId).addListenerForSingleValueEvent(eventListener)
            true
        }
        R.id.sign_out -> {
            AuthUI.getInstance()
                .signOut(requireContext())
            findNavController().navigate(
                GameDetailsFragmentDirections.actionGameDetailsFragmentToSignInFragment("")
            )
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
        /*
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

         */
}