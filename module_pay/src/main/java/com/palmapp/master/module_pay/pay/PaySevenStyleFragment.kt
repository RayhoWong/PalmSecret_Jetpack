package com.palmapp.master.module_pay.pay

import android.content.Intent
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.PaymentGuideConfig
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq59OperationStatistic
import com.palmapp.master.baselib.view.GradientBorderDrawable
import com.palmapp.master.module_pay.BillingManager
import com.palmapp.master.module_pay.R
import kotlinx.android.synthetic.main.pay_fragment_seven.*
import java.lang.Exception

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/4
 */
class PaySevenStyleFragment : PayBaseStyleFragment() {
    private lateinit var helper: PayVideoHelper
    private var isFrist = true
    private val mMediaPlayer = MediaPlayer()
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
        return "style7"
    }

    override fun getResLayout() = R.layout.pay_fragment_seven

    override fun getDefaultConfig(): PaymentGuideConfig? {
        return null
    }

    override fun initView() {
        paymentGuideConfig?.let { paymentGuideConfig ->
            helper = PayVideoHelper(paymentGuideConfig.videoStyle)
            val videoText = arrayListOf<String>()
            videoText.add("")
            videoText.add("")
            videoText.add("")
            videoText.add("")
            videoText.add("")
            videoText.add("")
            helper.setText(videoText)
            viewpager_payment.adapter = MyAdapter()
            viewpager_payment.offscreenPageLimit = helper.datas.size
            viewpager_payment.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    play(position)
                }
            })

            mMediaPlayer.setOnCompletionListener {
                viewpager_payment.setCurrentItem(
                    if ((viewpager_payment.currentItem + 1) < helper.datas.size) viewpager_payment.currentItem + 1 else 0,
                    true
                )
            }

            tv_payment_title1.text = paymentGuideConfig.title1
            tv_payment_title2.text = paymentGuideConfig.title2
            tv_payment_content.text = paymentGuideConfig.mainText
            btn_payment_continue.text = paymentGuideConfig.button1
            tv_payment_continue.text = paymentGuideConfig.button2
            tv_payment_state.text = paymentGuideConfig.state
            activity?.let { activity ->
                btn_payment_continue.setOnClickListener {
                    BillingManager.startPay(
                        activity,
                        paymentGuideConfig.button1Sku,
                        entrance ?: "",
                        getStyle(),
                        callBack = this, clickTimes = ++mClickTimes
                    )
                }
                layout_payment_month.setOnClickListener {
                    BillingManager.startPay(
                        activity,
                        paymentGuideConfig.button2Sku,
                        entrance ?: "",
                        getStyle(),
                        callBack = this
                        , clickTimes = ++mClickTimes
                    )
                }
            }
            iv_payment_close.alpha = paymentGuideConfig.closeButtonAlpha

            iv_payment_close.setOnClickListener {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.subs_close_a000, "0", entrance)
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
                "${paymentGuideConfig.button1Sku}#${paymentGuideConfig.button2Sku}",
                entrance ?: "", getStyle()
            )
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

    inner class MyAdapter : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int = helper.datas.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(activity).inflate(R.layout.pay_layout_seven_video, container, false)
            val banner = view.findViewById<ImageView>(R.id.iv_payment_banner)
            banner.setImageResource(helper.datas.get(position).pic)
            view.tag = banner
            banners.add(banner)
            val textureView = view.findViewById<TextureView>(R.id.textureView)
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
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

                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        mMediaPlayer.release()
    }
}