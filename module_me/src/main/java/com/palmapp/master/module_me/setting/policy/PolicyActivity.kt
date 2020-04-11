package com.palmapp.master.infomation;

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.module_me.R
import kotlinx.android.synthetic.main.me_activity_policy.*



@Route(path = RouterConstants.ACTIVITY_MEPolicyActivity)
class PolicyActivity : BaseMVPActivity<PolicyView, PolicyPresenter>(), PolicyView, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v) {
            mBackIv -> finish()

        }
    }

    override fun createPresenter(): PolicyPresenter {
        return PolicyPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.me_activity_policy)
        setupWebview()
        mBackIv = findViewById(R.id.iv_titlebar_back)
        mBackIv?.let {
            it.setOnClickListener(this)
        }
        url?.let {
            webview.loadUrl(url)
        }
    }

    @JvmField
    @Autowired(name = "url")
    var url: String? = null

    fun setupWebview() {
        webview.settings?.javaScriptEnabled = true
        webview.settings?.setAppCacheEnabled(true)
        webview.settings?.cacheMode = WebSettings.LOAD_DEFAULT
        webview.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val params = view?.getLayoutParams()
                params?.let {
                    it.width = ViewGroup.LayoutParams.MATCH_PARENT
                    it.height = ViewGroup.LayoutParams.MATCH_PARENT
                    view.setLayoutParams(it)
                }

            }
        }
    }

    private var mBackIv:ImageView? = null
}