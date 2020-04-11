package com.palmapp.master.module_transform

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.palmapp.master.module_transform.activity.ResultActivity
import com.palmapp.master.module_transform.activity.TakePhotoActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transform_activity_main)

        findViewById<View>(R.id.btn_transform).setOnClickListener {
            startActivity(Intent(this@MainActivity, TakePhotoActivity::class.java))
        }

    }
}
