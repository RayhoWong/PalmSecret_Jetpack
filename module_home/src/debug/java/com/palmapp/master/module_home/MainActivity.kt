package com.palmapp.master.module_home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.module_imageloader.ImageLoader
import com.palmapp.master.module_imageloader.glide.GlideImageLoaderStrategy


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageLoader.init(GlideImageLoaderStrategy())
        setContentView(R.layout.home_activity_main)
    }


}
