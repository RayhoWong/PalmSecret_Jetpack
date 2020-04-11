package com.palmapp.master.module_face

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.palmapp.master.module_face.activity.animal.AnimalScanActivity
import androidx.appcompat.app.AppCompatActivity
import com.palmapp.master.module_face.activity.album.AlbumTakePhotoActivity
import com.palmapp.master.module_face.activity.baby.BabyMatchActivity
import com.palmapp.master.module_face.activity.cartoon.CartoonTakePhotoActivity
import com.palmapp.master.module_face.activity.takephoto.TakephotoActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_main)
    }

    fun testOldTakePhoto(view: View?) {
        startActivity(Intent(this@MainActivity, TakephotoActivity::class.java))
    }


    fun testBabyMatchResult(view: View?) {
        startActivity(Intent(this@MainActivity, BabyMatchActivity::class.java))
    }

    fun testCartoonTakePhoto2(view: View?) {}

    fun testCartoonTakePhoto(view: View?) {
        startActivity(Intent(this@MainActivity, CartoonTakePhotoActivity::class.java))
    }

    fun toAnimal(view: View?) {
        startActivity(Intent(this@MainActivity, AnimalScanActivity::class.java))
    }

    fun testAlbumTakePhoto(view: View?) {
        startActivity(Intent(this@MainActivity, AlbumTakePhotoActivity::class.java))
    }
}
