package com.palmapp.master.module_psy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.view.RoundProgress

class TestCircleProgressBarActivity : BaseActivity() {

    private lateinit var progressbar: RoundProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.psy_activity_test_circle_progress_bar)

        startProgress()
    }


    private fun startProgress(){
        progressbar = findViewById(R.id.round_progress_bar)
        progressbar.setProgress(100f)
        progressbar.stopAnim()
    }
}
