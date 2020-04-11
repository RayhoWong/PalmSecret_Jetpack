package com.palmapp.master.module_pay.pay

import android.content.Intent
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.*
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.PaymentGuideConfig
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.module_pay.R
import android.os.Handler
import android.os.Message
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.BillingClient
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq59OperationStatistic
import com.palmapp.master.module_pay.BillingManager
import com.palmapp.master.module_pay.shakeBtn
import kotlinx.android.synthetic.main.pay_fragment_nine.*
import kotlinx.android.synthetic.main.pay_fragment_nine.btn_payment_continue
import kotlinx.android.synthetic.main.pay_fragment_nine.iv_payment_checkbox1
import kotlinx.android.synthetic.main.pay_fragment_nine.iv_payment_checkbox2
import kotlinx.android.synthetic.main.pay_fragment_nine.iv_payment_close
import kotlinx.android.synthetic.main.pay_fragment_nine.layout_payment_indicator
import kotlinx.android.synthetic.main.pay_fragment_nine.tv_payment_btn_subtext1
import kotlinx.android.synthetic.main.pay_fragment_nine.tv_payment_btn_subtext2
import kotlinx.android.synthetic.main.pay_fragment_nine.tv_payment_btn_text1
import kotlinx.android.synthetic.main.pay_fragment_nine.tv_payment_btn_text2
import kotlinx.android.synthetic.main.pay_fragment_nine.tv_payment_state
import kotlinx.android.synthetic.main.pay_fragment_nine.tv_payment_tab
import kotlinx.android.synthetic.main.pay_fragment_nine.view_payment_btn1
import kotlinx.android.synthetic.main.pay_fragment_nine.view_payment_btn2
import kotlinx.android.synthetic.main.pay_fragment_nine.viewpager_payment
import kotlinx.android.synthetic.main.pay_fragment_two.*
import java.lang.Exception


/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/21
 */
class PayNinetStyleFragment : PayBaseStyleFragment() {
    private lateinit var helper: PayVideoHelper
    private var clickEvent: Boolean = false
    private var isFrist = true
    private val mMediaPlayer = MediaPlayer()
    private val banners = arrayListOf<View>()
    private val surfaces = arrayListOf<Surface>()
    private val adapter = PicAdapter()
    private var lastPos = 0
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            banners.get(msg?.what ?: 0).visibility = View.GONE
        }
    }
    private var style = "style9"
    override fun isNeedToBlockBack(): Boolean {
        return iv_payment_close.visibility != View.VISIBLE
    }

    override fun getStyle(): String {
        return "style9"
    }

    override fun getResLayout() = R.layout.pay_fragment_nine

    override fun getDefaultConfig(): PaymentGuideConfig? {
        style = if (isLauncherScen()) "local_style9" else "internal_style9"
        val config = PaymentGuideConfig()
        config.option1Text1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_option1_text1)
        config.option1Text2 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_option1_text2)
        config.option2Text1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_option2_text1)
        config.option2Text2 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_option2_text2)
        config.option2Sku = Product.DEFAULT_SKU_MONTH
        config.option1Sku = Product.DEFAULT_SKU_YEAR
        config.closeButtonAlpha = 0.7f
        config.closeButtonDelay = 0
        config.button1 = GoCommonEnv.getApplication().getString(R.string.pay_default_two_btn1_title)
        config.option2Tab = GoCommonEnv.getApplication().getString(R.string.pay_defaule_tab)
        config.mainText1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_title)
        config.state =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_state)
        return config
    }

    inner class MyAdapter : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int = helper.datas.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(activity)
                .inflate(R.layout.pay_layout_nine_video, container, false)
            val tv = view.findViewById<TextView>(R.id.tv_payment_title2)
            val banner = view.findViewById<ImageView>(R.id.iv_payment_banner)
            banner.setImageResource(helper.datas.get(position).pic)
            view.tag = banner
            banners.add(banner)
            tv.text = helper.datas.get(position).text
            val textureView = view.findViewById<TextureView>(R.id.textureView)
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(
                    surface: SurfaceTexture?,
                    width: Int,
                    height: Int
                ) {
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    try {
                        surfaces.get(position).release()
                    } catch (e: Exception) {

                    }
                    return true
                }

                override fun onSurfaceTextureAvailable(
                    surface: SurfaceTexture?,
                    width: Int,
                    height: Int
                ) {
                    try {
                        surfaces.add(Surface(surface))
                        if (isFrist) {
                            isFrist = false
                            play(viewpager_payment.currentItem)
                        }
                    } catch (e: Exception) {

                    }
                }

            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            if (`object` is View) {
                banners.remove(`object`.tag)
                container.removeView(`object`)
            }
        }

    }

    private fun loadVideo(position: Int) {
        val fd = resources.openRawResourceFd(helper.datas.get(position).video) ?: return
        mMediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mMediaPlayer.setOnPreparedListener { mp ->
            mp.start()
            handler.removeCallbacksAndMessages(null)
            handler.sendEmptyMessageDelayed(position, 250)

        }
        mMediaPlayer.prepareAsync()
    }

    private fun play(position: Int) {
        try {
            if (surfaces[position]?.isValid == false)
                return
            banners.forEach {
                it.visibility = View.VISIBLE
            }
            mMediaPlayer.seekTo(0)
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            mMediaPlayer.setSurface(surfaces[position])
            loadVideo(position)
        } catch (e: Exception) {

        }
    }

    private fun selected(pos: Int) {
        layout_payment_indicator.children.iterator().forEach {
            it.isSelected = false
        }

        layout_payment_indicator.getChildAt(pos).isSelected = true
    }

    override fun initView() {
        paymentGuideConfig?.let { paymentGuideConfig ->
            if (!TextUtils.isEmpty(paymentGuideConfig.option2Tab)) {
                tv_payment_tab.text = paymentGuideConfig.option2Tab
            } else {
                tv_payment_tab.visibility = View.INVISIBLE
            }
            val videoText = arrayListOf<String>()
            var tab = GoPrefManager.getDefault().getInt(PreConstants.Launcher.KEY_BILLING_TIMES, -1)
            if (tab != -1 && tab < 4) {
                tab += 200
            } else {
                tab = -1
            }
            if (entrance == "1" || entrance == "14") {
                tab = -1
            }
            helper = PayVideoHelper(paymentGuideConfig.videoStyle)
//            if (TextUtils.isEmpty(paymentGuideConfig.title)) {
//                tv_payment_title1.visibility = View.GONE
//            } else {
//                tv_payment_title1.visibility = View.VISIBLE
//                tv_payment_title1.text = paymentGuideConfig.title.replace("<divider/>", "\n")
//            }
            iv_payment_close.alpha = paymentGuideConfig.closeButtonAlpha
            ThreadExecutorProxy.runOnMainThread(Runnable {
                if (iv_payment_close != null) {
                    iv_payment_close.visibility = View.VISIBLE
                }
            }, paymentGuideConfig.closeButtonDelay * 1000L)
            paymentGuideConfig.mainText1.split("<divider/>").forEach {
                videoText.add(it)
            }
            helper.setText(videoText)
            btn_payment_continue.text = paymentGuideConfig.button1
            viewpager_payment.pageMargin = resources.getDimensionPixelSize(R.dimen.change_18px)
            viewpager_payment.adapter = MyAdapter()
            viewpager_payment.offscreenPageLimit = helper.datas.size
            viewpager_payment.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    if (clickEvent) {
                        //点击滑动
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.subs_page_action,
                            "style9", if (tab == -1) "" else tab.toString(), entrance, "", "2-${helper.datas.get(position).type}")
                    } else {
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.subs_page_action,
                            "style9", if (tab == -1) "" else tab.toString(), entrance, "", "1")
                    }
                    selected(position)
                    play(position)
                    adapter.notifyItemChanged(position)
                    adapter.notifyItemChanged(lastPos)
                    lastPos = position
                    recycler_payment.smoothScrollToPosition(position)
                }
            })
            val padding = resources.getDimensionPixelOffset(R.dimen.change_15px)
            val size = resources.getDimensionPixelSize(R.dimen.change_18px)
            videoText.forEach {
                val view = View(activity)
                val margin = LinearLayout.LayoutParams(size, size)
                margin.leftMargin = padding
                margin.rightMargin = padding
                view.background = resources.getDrawable(R.drawable.pay_selector_two_circul)
                layout_payment_indicator.addView(view, margin)
            }
            selected(0)
            mMediaPlayer.setOnCompletionListener {
                viewpager_payment.setCurrentItem(
                    if ((viewpager_payment.currentItem + 1) < helper.datas.size) viewpager_payment.currentItem + 1 else 0,
                    true
                )
            }
            tv_payment_btn_text1.text = paymentGuideConfig.option1Text1
            if (TextUtils.isEmpty(paymentGuideConfig.option1Text2)) {
                tv_payment_btn_subtext1.visibility = View.GONE
            } else {
                tv_payment_btn_subtext1.text = paymentGuideConfig.option1Text2
            }
            tv_payment_btn_text2.text = paymentGuideConfig.option2Text1
            tv_payment_btn_subtext2.text = paymentGuideConfig.option2Text2
            if (TextUtils.isEmpty(paymentGuideConfig.option2Text2)) {
                tv_payment_btn_subtext2.visibility = View.GONE
            } else {
                tv_payment_btn_subtext2.text = paymentGuideConfig.option2Text2
            }
            activity?.let { activity ->
                view_payment_btn1.setOnClickListener {
                    view_payment_btn1.isSelected = true
                    view_payment_btn2.isSelected = false
                    iv_payment_checkbox1.isSelected = true
                    iv_payment_checkbox2.isSelected = false
                    tv_payment_btn_text1.isSelected = true
                    tv_payment_btn_text2.isSelected = false
                    if (!TextUtils.isEmpty(paymentGuideConfig.option1Text2)) {
                        tv_payment_btn_subtext1.visibility = View.VISIBLE
                    }
                    tv_payment_btn_subtext2.visibility = View.GONE
                }

                view_payment_btn2.setOnClickListener {
                    view_payment_btn1.isSelected = false
                    view_payment_btn2.isSelected = true
                    iv_payment_checkbox1.isSelected = false
                    iv_payment_checkbox2.isSelected = true
                    tv_payment_btn_text1.isSelected = false
                    tv_payment_btn_text2.isSelected = true
                    tv_payment_btn_subtext1.visibility = View.GONE
                    if (!TextUtils.isEmpty(paymentGuideConfig.option2Text2)) {
                        tv_payment_btn_subtext2.visibility = View.VISIBLE
                    }
                }

                view_payment_btn2.performClick()
                btn_payment_continue.shakeBtn()
                btn_payment_continue.setOnClickListener {
                    var sku = ""
                    if (view_payment_btn1.isSelected) {
                        sku = paymentGuideConfig.option1Sku
                    } else {
                        sku = paymentGuideConfig.option2Sku
                    }
                    BillingManager.startPay(activity, sku, entrance
                        ?: "", style, callBack = this, clickTimes = ++mClickTimes)
                }
                try {
                    val state =
                        paymentGuideConfig.state.replace("@", "").replace("#", "").replace("%", "")
                    val builder = SpannableStringBuilder(state)
                    setSpan(paymentGuideConfig.state, state, "@", builder)
                    setSpan(paymentGuideConfig.state, state, "#", builder)
                    setSpan(paymentGuideConfig.state, state, "%", builder)
                    tv_payment_state.highlightColor = Color.TRANSPARENT; //设置点击后的颜色为透明，否则会一直出现高亮
                    tv_payment_state.text = builder;
                    tv_payment_state.movementMethod = LinkMovementMethod.getInstance();
                } catch (e: Exception) {
                    tv_payment_state.text = paymentGuideConfig.state
                }
            }

            val layoutManager = LinearLayoutManager(activity)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            recycler_payment.layoutManager = layoutManager
            recycler_payment.adapter = adapter

            iv_payment_close.alpha = paymentGuideConfig.closeButtonAlpha

            iv_payment_close.setOnClickListener {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.subs_close_a000, "0", entrance
                )
                val intent = Intent()
                intent.putExtra("status", "返回订阅状态")
                activity?.setResult(AppConstants.PAY_RESULT_CODE, intent)
                activity?.finish()
            }

            ThreadExecutorProxy.runOnMainThread(Runnable {
                if (iv_payment_close != null) {
                    iv_payment_close.visibility = View.VISIBLE
                }
            }, paymentGuideConfig.closeButtonDelay * 1000L)

            BaseSeq59OperationStatistic.uploadStatisticPayShow(
                GoCommonEnv.getApplication(),
                "${paymentGuideConfig.option1Sku}#${paymentGuideConfig.option2Sku}",
                entrance ?: ""
                , style
            )
        }
    }

    private fun setSpan(str: String, state: String, flag: String, builder: SpannableStringBuilder) {
        try {
            val string = str.splitFromChar(flag)
            if (TextUtils.isEmpty(string)) {
                return
            }
            val index = state.indexOf(string)
            builder.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        getWebSite(flag)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = Color.parseColor("#82cebb")
                        ds.isUnderlineText = true

                    }
                },
                index,
                index + string.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } catch (e: Exception) {

        }
    }

    val webSite = hashMapOf<String, String>()

    init {
        webSite.put("@", "http://d2prafqgniatg5.cloudfront.net/palmhoroscope/palmsecret_p2.html")
        webSite.put("#", "http://d2prafqgniatg5.cloudfront.net/palmhoroscope/palmsecret_p1.html")
        webSite.put("%", "https://support.google.com/googleplay/answer/7018481")

    }

    private fun getWebSite(index: String) {
        val uri = Uri.parse(webSite.get(index))
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        try {
            activity?.startActivity(intent)
        }catch (e:Exception){
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMediaPlayer.release()
    }

    private fun String.splitFromChar(char: String): String {
        return try {
            val result = this.substring(this.indexOf(char) + 1, this.lastIndexOf(char))
            result
        } catch (e: Exception) {
            ""
        }
    }

    private inner class PicAdapter : RecyclerView.Adapter<PicHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicHolder {
            return PicHolder(LayoutInflater.from(activity).inflate(R.layout.pay_layout_teim_nine, parent, false))
        }

        override fun getItemCount(): Int {
            return helper.datas.size
        }

        override fun onBindViewHolder(holder: PicHolder, position: Int) {
            holder.video.setImageResource(helper.datas.get(position).smallPic)
            holder.playing.visibility = if (viewpager_payment.currentItem == position) View.VISIBLE else View.GONE
            holder.itemView.setOnClickListener {
                clickEvent = true
                viewpager_payment.currentItem = position
            }
        }

    }

    private inner class PicHolder(view: View) : RecyclerView.ViewHolder(view) {
        val video: ImageView = view.findViewById(R.id.iv_video)
        val playing: ImageView = view.findViewById(R.id.iv_playing)
    }
}
