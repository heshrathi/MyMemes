package com.example.mymemes

import SingletonPattern
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    var currentMemeURL: String? = null
    var upVote by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadMeme()

        nextButton.setOnClickListener() {
            loadMeme()
        }

        shareButton.setOnClickListener() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Hey mate, check out this funny meme $currentMemeURL")
            val chooser = Intent.createChooser(intent, "Share with")
            startActivity(chooser)
        }
    }

    private fun loadMeme() {
        // Instantiate the RequestQueue.
        progress_circular.visibility = View.VISIBLE
        currentMemeURL = "https://meme-api.herokuapp.com/gimme"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, currentMemeURL, null,
            { response ->
                val userName = response.getString("author")
                upVote = response.getInt("ups")
                userNameText.text = "$userName"
                memeVoteText.text = "$upVote"
                val url = response.getString("url")
                Glide.with(this).load(url).listener(object: RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress_circular.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress_circular.visibility = View.GONE
                        return false
                    }

                }).into(memeImageView)
            },
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        SingletonPattern.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    var check: Int = 0
    fun likeInc(view: View) {
        if (check == 0) {
            likeButton.setImageDrawable(getDrawable(R.drawable.liked))
            upVote++
            val voteInc: String = "$upVote"
            memeVoteText.setText(voteInc)
            check = 1
        }
        else if (check == 1) {
            likeButton.setImageDrawable(getDrawable(R.drawable.like))
            upVote--
            val voteDec: String = "$upVote"
            memeVoteText.setText(voteDec)
            check = 0
        }

    }

}