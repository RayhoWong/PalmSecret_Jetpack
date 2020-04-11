package com.palmapp.master.module_tarot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.palmapp.master.module_tarot.activity.DailyTarotActivity
import com.palmapp.master.module_tarot.activity.TopicCareerActivity
import com.palmapp.master.module_tarot.activity.TopicLoveActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tarot_activity_main)

        findViewById<View>(R.id.btn_daily_tarot).setOnClickListener {
            startActivity(Intent(this, DailyTarotActivity::class.java))
        }

        findViewById<View>(R.id.btn_topic_love).setOnClickListener {
            startActivity(Intent(this, TopicLoveActivity::class.java))
        }

        findViewById<View>(R.id.btn_topic_career).setOnClickListener {
            startActivity(Intent(this, TopicCareerActivity::class.java))
        }
    }
}
