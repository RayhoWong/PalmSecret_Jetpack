package com.palmapp.master.module_today

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palmapp.master.module_imageloader.ImageLoader
import com.palmapp.master.module_imageloader.glide.GlideImageLoaderStrategy

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageLoader.init(GlideImageLoaderStrategy())
        setContentView(R.layout.activity_main)
    }
}
