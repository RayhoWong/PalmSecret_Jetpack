package com.palmapp.master.module_face.activity.cartoon;

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.module_ad.IHotRatingView
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.ImageUtil
import com.palmapp.master.module_ad.HotRatingPresenter
import com.palmapp.master.module_ad.IRewardVideoView
import com.palmapp.master.module_ad.RewardVideoPresenter
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_face.R
import kotlinx.android.synthetic.main.face_activity_cartoon_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterConstants.ACTIVITY_CARTOON_RESULT)
class CartoonResultActivity : BaseMultipleMVPActivity(),
    CartoonResultView, IRewardVideoView, IHotRatingView, IPermissionView {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""

    var sender = "1"

    private val cartoonResultPresenter = CartoonResultPresenter()
    private val rewardVideoPresenter = RewardVideoPresenter(this)
    private val hotRatingPresenter = HotRatingPresenter(this, rewardVideoPresenter)

    private val datas = arrayListOf<CartoonBean>()
    private var pos = 0

    init {
        datas.add(CartoonBean(R.mipmap.save_cartoon_1, "Original", "save_cartoon_1", ""))
        datas.add(
            CartoonBean(
                R.mipmap.save_cartoon_9,
                "Mononoke",
                "save_cartoon_9",
                "512x512_mononoke_crop_s20_c8_f24_encrypt",
                false
            )
        )
        datas.add(
            CartoonBean(
                R.mipmap.save_cartoon_7,
                "Gothic",
                "save_cartoon_7",
                "512x512_gothic_s30_c5_f24_encrypt",
                false
            )
        )
        datas.add(
            CartoonBean(
                R.mipmap.save_cartoon_2,
                "Sketch V1",
                "save_cartoon_2",
                "512x512_curlyhair_crop_s20_c1_encrypt"
            )
        )
        datas.add(
            CartoonBean(
                R.mipmap.save_cartoon_3,
                "Sketch V2",
                "save_cartoon_3",
                "512x512_curlyhair_crop_s20_c1_v1_f24_encrypt",
                false
            )
        )
        datas.add(
            CartoonBean(
                R.mipmap.save_cartoon_4,
                "Daryl Feril",
                "save_cartoon_4",
                "512x512_Daryl_Feril_s20_c10_f24_encrypt"
            )
        )
        datas.add(
            CartoonBean(
                R.mipmap.save_cartoon_8,
                "Marcus D",
                "save_cartoon_8",
                "512x512_Marcus_D_Lone_Wolf_enlarge_s20_c10_f24_encrypt",
                false
            )
        )
        datas.add(
            CartoonBean(
                R.mipmap.save_cartoon_5,
                "Femme",
                "save_cartoon_5",
                "512x512_femme_s10_c15_f24_encrypt",
                false
            )
        )
        datas.add(
            CartoonBean(
                R.mipmap.save_cartoon_11,
                "Managa",
                "save_cartoon_11",
                "512x512_manga_enlarge_s20_c15_f24_encrypt"
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOrignEvent(orign: Bitmap) {
        cartoonResultPresenter.start(orign)
        EventBus.getDefault().removeStickyEvent(orign)
    }

    override fun finish() {
        goHomeAndClearTop() {
            super.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        iv_result.stopAnim()
        ThreadExecutorProxy.clearAsyncThread()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        sender = if (newPlan == "1") "1" else "2"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_cartoon_result)
        if (!rewardVideoPresenter.isCanUseFun) {
            showNotVipView()
        } else {
            showVipView(true)
        }
        ExitAdManager.loadAd(this)
        findViewById<TextView>(R.id.tv_titlebar_title).text =
            getString(R.string.filter_cartoon_title)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener { onBackPressed() }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = MyAdapter()
        //点赞
        val mIvLike = layout_share.findViewById<ImageView>(R.id.iv_like)
        val mIvShare_2 = layout_share.findViewById<ImageView>(R.id.iv_share)
        val mDownload = layout_share.findViewById<ImageView>(R.id.iv_download)
        mDownload.visibility = View.VISIBLE
        mDownload.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.download_func_a000,
                "",
                "4",
                ""
            )
            iv_result.getCartoonView().getImageBitmap()?.let { it1 -> ImageUtil.save(this, it1) }
        }
        mIvLike.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.like_a000,"","4","")
            //帧动画
            val animationDrawable = mIvLike.drawable as AnimationDrawable
            animationDrawable.start()

            ThreadExecutorProxy.runOnMainThread(
                Runnable {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.thumbs_up_a000
                    )
                    hotRatingPresenter.showLikeDialog()
                }, 1000
            )
        }

        mIvShare_2.setOnClickListener {
            iv_result.getCartoonView().getImageBitmap()?.let { it1 -> cartoonResultPresenter.share(it1) }
        }
        EventBus.getDefault().register(this)
    }

    override fun showRewardVipView() {
        showVipView(true)
    }

    private fun showNotVipView() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.show_result_a000,
            "",
            "6",
            ""
        )

        if (newPlan == "1") {
            if (GoCommonEnv.isFirstRunPayGuide) {
                val config = PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", config.scen).withString("type", "11").withString("newPlan", "1").navigation()
            } else {
                val config = PayGuideManager.payGuideConfig.get(PayGuideManager.NOT_FIRST_LAUNCHER)!!
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", config.scen).withString("type", "11").withString("newPlan", "1").navigation()
            }
        } else {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                .withString("entrance", "11").withString("type", "11").navigation()
        }

        bv_palmresult.findViewById<View>(R.id.btn_pay).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                .withString("entrance", "11").withString("type", "11").navigation()
        }
        bv_palmresult.setWatchClickListener(View.OnClickListener {
            rewardVideoPresenter.tryToWatchAd()
        })
    }

    fun showVipView(isVip: Boolean) {
        if (!isVip) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.art_page_f000,
                sender, "5", ""
            )
        } else {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.art_page_f000,
                sender, "4", ""
            )
        }
        bv_palmresult.visibility = if (isVip) View.GONE else View.VISIBLE
        layout_share.visibility = if (isVip) View.VISIBLE else View.GONE
    }

    override fun showResult(bitmap: Bitmap?) {
        loading.visibility = View.GONE
        iv_result.startAnim(Runnable {
            iv_result.getCartoonView().switchBitmap(bitmap)
        })
    }

    override fun showOriginalBitmap(bitmap: Bitmap?) {
        loading.visibility = View.GONE
        iv_result.getCartoonView().switchBitmap(bitmap)
        iv_result.getCartoonView().postInvalidate()
        recyclerview.adapter?.notifyItemChanged(0)
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    private inner class MyAdapter() :
        RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(this@CartoonResultActivity).inflate(
                    R.layout.face_layout_cartoon_result,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int = datas.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = datas.get(position)
            holder.itemView.setOnClickListener {
                showVipView(rewardVideoPresenter.isCanUseFun || datas.get(position).isFree)
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.art_page_a000, "", position.toString(), "")
                recyclerview.adapter?.notifyItemChanged(pos)
                recyclerview.adapter?.notifyItemChanged(position)
                pos = position
                cartoonResultPresenter.selectFilter(position, data.path, data.fileName)
            }
            holder.name.text = data.name
            holder.result.setImageResource(data.result)
            holder.cover_select.visibility = if (pos == position) View.VISIBLE else View.GONE
            holder.cover_black.visibility = if (pos == position) View.GONE else View.VISIBLE
            holder.vip.visibility = if (data.isFree) View.GONE else View.VISIBLE
        }

    }

    private class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val result = itemView.findViewById<ImageView>(R.id.iv_filter_result)
        val name = itemView.findViewById<TextView>(R.id.tv_filter_name)
        val cover_select = itemView.findViewById<View>(R.id.fl_filter_select)
        val cover_black = itemView.findViewById<View>(R.id.fl_filter_cover)
        val vip = itemView.findViewById<View>(R.id.fl_filter_vip)
    }

    private class CartoonBean(
        val result: Int,
        val name: String,
        val path: String,
        val fileName: String,
        val isFree: Boolean = true
    )

    override fun addPresenters() {
        addToPresenter(cartoonResultPresenter)
        addToPresenter(rewardVideoPresenter)
        addToPresenter(hotRatingPresenter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 14) {
            if (!BillingServiceProxy.isVip()) {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.art_page_f000,
                    sender, "5", ""
                )
            } else {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.art_page_f000,
                    sender, "4", ""
                )
            }
            showVipView(true)
        }
    }

    override fun withoutHotRating() {
        if (!BillingServiceProxy.isVip()) {
            ExitAdManager.showAd()
        }
        finish()
    }

    override fun onPermissionGranted(code: Int) {
    }

    override fun onPermissionDenied(code: Int) {
    }

    override fun onPermissionEveryDenied(code: Int) {
    }

}
