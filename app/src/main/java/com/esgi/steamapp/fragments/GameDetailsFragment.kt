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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GameDetailsFragment : Fragment() {
    private lateinit var game_name : TextView
    private lateinit var game_editor : TextView
    private lateinit var game_photo_link : String
    private lateinit var game_photo : ImageView
    private lateinit var game_description : TextView
    private lateinit var game_id : String
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
        game_name = view.findViewById(R.id.nom)
        game_editor = view.findViewById(R.id.editeur2)
        game_photo = view.findViewById(R.id.image)
        game_description = view.findViewById(R.id.data_transition_view)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                findNavController().navigateUp()
            }
        })


        game_name.text =  GameDetailsFragmentArgs.fromBundle(requireArguments()).gameName
        game_id = GameDetailsFragmentArgs.fromBundle(requireArguments()).gameId
        game_editor.text = GameDetailsFragmentArgs.fromBundle(requireArguments()).gameEditor
        game_description.text = GameDetailsFragmentArgs.fromBundle(requireArguments()).gameDescription
        game_photo_link = GameDetailsFragmentArgs.fromBundle(requireArguments()).gameImage
        game_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);


        Glide.with(this).load(game_photo_link).into(game_photo)

        game_photo = view.findViewById(R.id.mini_image)
        Glide.with(this).load(game_photo_link).centerCrop().into(game_photo)
        replaceFragment(GameDescriptionFragment().newInstance(game_description.text.toString()))
        view.findViewById<Button>(R.id.description_button).setOnClickListener() {
            //onClick(findViewById<Button>(R.id.description_button))
            replaceFragment(GameDescriptionFragment().newInstance(game_description.text.toString()))
        }
        view.findViewById<Button>(R.id.avis_button).setOnClickListener {
            //onClick(findViewById<Button>(R.id.avis_button))
            replaceFragment(GameAvisFragment().newInstance(game_id.toString()))
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
                if(snapshot.child(game_id).exists()) {
                    menu.getItem(0).setIcon(R.drawable.like_full)
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })
        database_favorites.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(game_id).exists()) {
                    menu.getItem(1).setIcon(R.drawable.whishlist_full)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId)  {
        R.id.like -> {
            val database_likes = database.getReference().child("Likes")
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) {
                        item.setIcon(R.drawable.like_full)
                        database_likes.child(game_id).child("name").setValue(game_name.text)
                        database_likes.child(game_id).child("dev").setValue(game_editor.text)
                        database_likes.child(game_id).child("photo").setValue(game_photo_link)
                        database_likes.child(game_id).child("description").setValue(game_description.text)
                    }
                    else {
                        item.setIcon(R.drawable.like)
                        database_likes.child(game_id).removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(ContentValues.TAG, error.getMessage());
                }
            }
            database_likes.child(game_id).addListenerForSingleValueEvent(eventListener)
            true
        }

        R.id.favorite -> {
            val database_favorites = database.getReference().child("Favorites")
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) {
                        item.setIcon(R.drawable.whishlist_full)
                        database_favorites.child(game_id).child("name").setValue(game_name.text)
                        database_favorites.child(game_id).child("dev").setValue(game_editor.text)
                        database_favorites.child(game_id).child("photo").setValue(game_photo_link)
                        database_favorites.child(game_id).child("description").setValue(game_description.text)
                    }
                    else {
                        item.setIcon(R.drawable.whishlist)
                        database_favorites.child(game_id).removeValue()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d(ContentValues.TAG, error.getMessage());
                }
            }
            database_favorites.child(game_id).addListenerForSingleValueEvent(eventListener)
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