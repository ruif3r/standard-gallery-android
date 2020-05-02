package com.example.standardgallery.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import com.bumptech.glide.Glide
import com.example.standardgallery.R

class FullImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)
        window.enterTransition = Fade()
        Glide.with(this).load(intent.getStringExtra("EXTRA_PATH")).into(findViewById(R.id.full_image))
    }
}
