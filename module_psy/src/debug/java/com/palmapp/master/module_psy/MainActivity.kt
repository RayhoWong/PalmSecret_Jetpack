package com.palmapp.master.module_psy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.bean.quiz.Quzi_answer
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.module_imageloader.ImageLoader
import com.palmapp.master.module_psy.heartrate.BirthActivity
import com.palmapp.master.module_psy.heartrate.HeartRateDetectActivity

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/15
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.psy_activity_main)
        ImageLoader.init(com.palmapp.master.module_imageloader.glide.GlideImageLoaderStrategy())
        findViewById<View>(R.id.btn_quiz).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_LIST).withString("id", "120")
                .withString("title", "title").navigation()
        }

        findViewById<View>(R.id.btn_result).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_RESULT).withString("id", "120")
                .withSerializable("answer", testAnwser()).navigation()
        }

        findViewById<View>(R.id.btn_heartrate).setOnClickListener {
           startActivity(Intent(this,HeartRateDetectActivity::class.java))
        }

        findViewById<View>(R.id.btn_progress_bar).setOnClickListener {
            startActivity(Intent(this,BirthActivity::class.java))
        }

    }

    private fun testAnwser(): Quzi_answer {
        val answer = Quzi_answer(
            145,
            "The dependence on LOVE is 75%!",
            "The dependence on LOVE is 75%! You are blind in love. You often put the other person in the first place in your mind. If lover asks you to go out, you will disregard everything immediately and run to him! But this kind of enthusiasm comes and goes quickly.",
            "Love Master",
            "3"
        )

        return answer
    }
}