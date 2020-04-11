package com.palmapp.master.module_palm

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.bean.palm.*
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.GoPrefProxy
import com.palmapp.master.baselib.utils.GoGson
import com.palmapp.master.module_palm.match.PalmMatchActivity
import com.palmapp.master.module_palm.match.PalmMatchLoadingActivity
import com.palmapp.master.module_palm.match.PalmMatchResultActivity
import com.palmapp.master.module_palm.scan.PalmImageManager
import com.palmapp.master.module_palm.scan.PalmResultCache
import com.palmapp.master.module_palm.scan.PalmScanActivity
import com.palmapp.master.module_palm.test.PalmprintJudgActivity

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/8
 */
class MainActivity : AppCompatActivity() {
    val result= "{\"id\":303,\"sign1\":1,\"sign2\":2,\"content\":{\"category\":31,\"article\":[{\"type\":3,\"text\":\"你的熱情可以幫助振作有時懶惰的伴侶。你傾向於比你的伴侶更加焦躁不安，而伴侶是由愛所統治的。您可能需要在許多活動中起帶頭作用。\"},{\"type\":3,\"text\":\"另一方面，你的牛頭朋友比你曾經更加堅持不懈，並且可能會為這段關係帶來堅韌和耐力。你是一個頭腦冷靜，衝動的人，但有些東西可能使你變得柔和。\"},{\"type\":3,\"text\":\"你的伴侶可能不會經常發脾氣，但是當他們這樣做時，你最好小心！在你對權威和領導的回應中，你們兩個都可能是反動的，有點孩子氣。然而，當談到比賽時，你們兩個人可以在生活的領域中嬉戲和漫遊。慢下來，你將能夠在很長一段時間內享受這種關係。\"}]},\"type\":1,\"score\":65,\"lang\":\"ZH\",\"intimacy\":61,\"suitable\":74,\"marry_age\":\"22, 25, 28\",\"children_count\":\"2\"}"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_layout_main)
        findViewById<View>(R.id.btn_scan).setOnClickListener {
            startActivity(Intent(this@MainActivity, PalmScanActivity::class.java))
        }
        findViewById<View>(R.id.btn_result).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_RESULT)
                .withSerializable("handleinfos", testHandleInfos())
                .navigation(this)
        }
        findViewById<View>(R.id.btn_test).setOnClickListener {
            startActivity(Intent(this@MainActivity,PalmprintJudgActivity::class.java))
        }
        findViewById<View>(R.id.btn_match).setOnClickListener {
            startActivity(Intent(this@MainActivity,PalmMatchActivity::class.java))
        }

        findViewById<View>(R.id.btn_match_loading).setOnClickListener {
            startActivity(Intent(this@MainActivity,PalmMatchLoadingActivity::class.java))
        }

        findViewById<View>(R.id.btn_match_result).setOnClickListener {
            val intent = Intent(this@MainActivity,PalmMatchResultActivity::class.java)
            intent.putExtra("result",GoGson.fromJson(result,PalmMatchResponse::class.java))
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_scan2).setOnClickListener {

        }
    }

    fun testHandleInfos(): PalmResultCache? {
        return GoGson.fromJson<PalmResultCache>(GoPrefManager.getDefault().getString(PreConstants.Palm.KEY_PALM_RESULT,""),PalmResultCache::class.java)
    }
}