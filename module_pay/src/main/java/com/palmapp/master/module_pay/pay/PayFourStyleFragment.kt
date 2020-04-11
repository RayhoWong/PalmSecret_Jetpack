package com.palmapp.master.module_pay.pay

import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.*
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.PaymentGuideConfig
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.module_pay.R
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq59OperationStatistic
import com.palmapp.master.module_pay.BillingManager
import com.palmapp.master.module_pay.shakeBtn
import kotlinx.android.synthetic.main.pay_fragment_four.*
import java.lang.Exception


/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/21
 */
class PayFourStyleFragment : PayBaseStyleFragment() {
    private var isFrist = true
    private val mMediaPlayer = MediaPlayer()
    private val videoText = arrayListOf<String>()
    private val video = arrayListOf<Int>()
    private val pics = arrayListOf<Int>()
    private val banners = arrayListOf<View>()
    private val surfaces = arrayListOf<Surface>()
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            banners.get(msg?.what ?: 0).visibility = View.GONE
        }
    }

    override fun isNeedToBlockBack(): Boolean {
        return iv_payment_close.visibility != View.VISIBLE
    }

    override fun getStyle(): String {
        return "style4"
    }

    override fun getResLayout() = R.layout.pay_fragment_four

    override fun getDefaultConfig(): PaymentGuideConfig? {
        return null
    }

    inner class MyAdapter : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int = videoText.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(activity).inflate(R.layout.pay_layout_four_video, container, false)
            val tv = view.findViewById<TextView>(R.id.tv_payment_title2)
            val banner = view.findViewById<ImageView>(R.id.iv_payment_banner)
            banner.setImageResource(pics[position])
            view.tag = banner
            banners.add(banner)
            tv.text = videoText.get(position)
            val textureView = view.findViewById<TextureView>(R.id.textureView)
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    try {
                        surfaces.get(position).release()
                    }catch (e:Exception){

                    }
                    return true
                }

                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    try {
                        surfaces.add( Surface(surface))
                        if (isFrist) {
                            isFrist = false
                            play(viewpager_payment.currentItem)
                        }
                    }catch (e:Exception){

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
        val fd = resources.openRawResourceFd(video[position]) ?: return
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
        paymentGuideConfig?.let {paymentGuideConfig->
            pics.clear()
            pics.add(R.mipmap.style4_pic1)
            pics.add(R.mipmap.style4_pic2)
            pics.add(R.mipmap.style4_pic3)
            pics.add(R.mipmap.style4_pic4)
            pics.add(R.mipmap.style4_pic5)
            pics.add(R.mipmap.style4_pic6)
            video.clear()
            video.add(R.raw.style4_video1)
            video.add(R.raw.style4_video2)
            video.add(R.raw.style4_video3)
            video.add(R.raw.style4_video4)
            video.add(R.raw.style4_video5)
            video.add(R.raw.style4_video6)
            if (TextUtils.isEmpty(paymentGuideConfig.title)) {
                tv_payment_title1.visibility = View.GONE
            } else {
                tv_payment_title1.visibility = View.VISIBLE
                tv_payment_title1.text = paymentGuideConfig.title.replace("<divider/>", "\n")
            }
            iv_payment_close.alpha = paymentGuideConfig.closeButtonAlpha
            ThreadExecutorProxy.runOnMainThread(Runnable {
                if (iv_payment_close != null) {
                    iv_payment_close.visibility = View.VISIBLE
                }
            }, paymentGuideConfig.closeButtonDelay * 1000L)
            paymentGuideConfig.mainText1.split("<divider/>").forEach {
                videoText.add(it)
            }
            btn_payment_continue.text = paymentGuideConfig.button1
            viewpager_payment.pageMargin = resources.getDimensionPixelSize(R.dimen.change_18px)
            viewpager_payment.adapter = MyAdapter()
            viewpager_payment.offscreenPageLimit = videoText.size
            viewpager_payment.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    selected(position)
                    play(position)
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
                    if ((viewpager_payment.currentItem + 1) < videoText.size) viewpager_payment.currentItem + 1 else 0,
                    true
                )
            }
            tv_payment_btn_text1.text = paymentGuideConfig.option1Text1
            tv_payment_btn_text2.text = paymentGuideConfig.option2Text1
            tv_payment_btn_subtext2.text = paymentGuideConfig.option2Text2
            activity?.let { activity ->
                view_payment_btn1.setOnClickListener {
                    view_payment_btn1.isSelected = true
                    view_payment_btn2.isSelected = false
                    iv_payment_checkbox1.isSelected = true
                    iv_payment_checkbox2.isSelected = false
                    tv_payment_btn_text1.isSelected = true
                    tv_payment_btn_text2.isSelected = false
                    if(!TextUtils.isEmpty(paymentGuideConfig.option1Text2)){
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
                    if(!TextUtils.isEmpty(paymentGuideConfig.option2Text2)){
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
                    BillingManager.startPay(activity, sku, entrance ?: "",getStyle(), callBack = this,clickTimes = ++mClickTimes)
                }
            }
            iv_payment_close.alpha = paymentGuideConfig.closeButtonAlpha

            iv_payment_close.setOnClickListener {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.subs_close_a000,"0",entrance)
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
                ,getStyle()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMediaPlayer.release()
    }

}