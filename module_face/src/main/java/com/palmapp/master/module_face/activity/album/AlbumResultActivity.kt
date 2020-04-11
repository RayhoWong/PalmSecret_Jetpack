package com.palmapp.master.module_face.activity.album

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
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.face.CartoonParam
import com.palmapp.master.baselib.bean.face.CartoonTemplate
import com.palmapp.master.baselib.bean.face.FaceInfo
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.GlideUtil
import com.palmapp.master.baselib.utils.ImageUtil
import com.palmapp.master.module_ad.HotRatingPresenter
import com.palmapp.master.module_ad.IHotRatingView
import com.palmapp.master.module_ad.IRewardVideoView
import com.palmapp.master.module_ad.RewardVideoPresenter
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_face.R
import kotlinx.android.synthetic.main.face_activity_album_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@Route(path = RouterConstants.ACTIVITY_ALBUM_RESULT)
class AlbumResultActivity : BaseMultipleMVPActivity(),
    AlbumResultView, IRewardVideoView, IHotRatingView, IPermissionView {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""

    var sender = "1"

    private val albumResultPresenter = AlbumResultPresenter()
    private val rewardVideoPresenter = RewardVideoPresenter(this)
    private val hotRatingPresenter = HotRatingPresenter(this, rewardVideoPresenter)

    private var pos = 0
    private var datas = arrayListOf<AlbumBean>()
    private var isVip = false

    private var s3ImageInfo: S3ImageInfo? = null
    private var cartoonTemplates = ArrayList<CartoonTemplate>()
    private var faceInfo: FaceInfo? = null


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOrignEvent(orign: Bitmap) {
        albumResultPresenter.start(orign)
        EventBus.getDefault().removeStickyEvent(orign)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onS3ImageInfoEvent(s3ImageInfo: S3ImageInfo) {
        this.s3ImageInfo = s3ImageInfo
        EventBus.getDefault().removeStickyEvent(faceInfo)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCartoonTemplatesEvent(templates: List<CartoonTemplate>) {
        cartoonTemplates = templates as ArrayList<CartoonTemplate>
        EventBus.getDefault().removeStickyEvent(templates)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFaceInfoEvent(faceInfo: FaceInfo) {
        this.faceInfo = faceInfo
        EventBus.getDefault().removeStickyEvent(faceInfo)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        sender = if (newPlan == "1") "1" else "2"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_album_result)
        EventBus.getDefault().register(this)
        initDatas()
        if (!rewardVideoPresenter.isCanUseFun) {
            showNotVipView()
        } else {
            showVipView(true)
        }
        ExitAdManager.loadAd(this)
        findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.magic_comic_title)
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
                "6",
                ""
            )
            ImageUtil.save(this,  (iv_result.drawable as BitmapDrawable).bitmap)
        }
        mIvLike.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.like_a000,"","6","")
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
            albumResultPresenter.share((iv_result.drawable as BitmapDrawable).bitmap)
        }

        //首次进入
//        var isFirstEnter = GoPrefManager.getDefault().getBoolean(PreConstants.Face.KEY_FACE_IS_FIRST_ENTER_ALBUM, true)
//        if (isFirstEnter) {
//            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.comic_page_f000, "1", "2","")
//            GoPrefManager.getDefault().putBoolean(PreConstants.Face.KEY_FACE_IS_FIRST_ENTER_ALBUM, false).commit()
//        } else {
//            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.comic_page_f000, "2", "2","")
//        }
    }


    private fun initDatas(){
        val filterNames = arrayListOf("origin",
            "summer v1","summer v2","dream v1","dream v2","dream v3","garden v1","garden v2","garden v3","milk v1","milk v2"
        )
        if (cartoonTemplates.size > 0){
            s3ImageInfo?.let { s3ImageInfo ->
                val s3ImageInfo = s3ImageInfo
                faceInfo?.let { info ->
                    datas.add(AlbumBean(S3ImageInfo(), CartoonParam(),"","Orign","",true))
                    cartoonTemplates.forEach {
                        var cartoonParam = CartoonParam()
                        cartoonParam.template_id = it.template_id.toInt()
                        cartoonParam.gender = info.gender
                        cartoonParam.ethnicity = info.ethnicity
                        datas.add(AlbumBean(s3ImageInfo,cartoonParam,
                            it.result_image_url
                            ,"Origin","save_album_${it.template_id}",false))
                    }
                }
            }
        }
        if (datas.size == filterNames.size){
            for (index in datas.indices){
                datas[index].name = filterNames[index]
            }
        }
    }


    override fun addPresenters() {
        addToPresenter(albumResultPresenter)
        addToPresenter(rewardVideoPresenter)
        addToPresenter(hotRatingPresenter)
    }


    private fun showNotVipView() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.comic_page_f000,
            sender, "4", ""
        )
        if (newPlan == "1") {
            if (GoCommonEnv.isFirstRunPayGuide) {
                val config = PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", config.scen).withString("type", "13").withString("newPlan", "1").navigation()
            } else {
                val config = PayGuideManager.payGuideConfig.get(PayGuideManager.NOT_FIRST_LAUNCHER)!!
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", config.scen).withString("type", "13").withString("newPlan", "1").navigation()
            }
        } else {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                .withString("entrance", "13").withString("type", "13").navigation()
        }

        bv_palmresult2.findViewById<View>(R.id.btn_pay).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                .withString("entrance", "13").withString("type", "13").navigation()
        }
        bv_palmresult2.setWatchClickListener(View.OnClickListener {
            rewardVideoPresenter.tryToWatchAd()
        })
    }


    fun showVipView(isVip: Boolean) {
        if (!isVip) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.comic_page_f000,
                sender, "4", ""
            )
        } else {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.comic_page_f000,
                sender, "3", ""
            )
        }
        this.isVip = isVip
        bv_palmresult2.visibility = if (isVip) View.GONE else View.VISIBLE
        layout_share.visibility = if (isVip) View.VISIBLE else View.GONE

        if (datas.size > 0)
        albumResultPresenter.selectFilter(isVip,pos, datas[pos].path,datas[pos].image,datas[pos].cartoon_param)
    }


    override fun showResult(bitmap: Bitmap?) {
        loading.visibility = View.GONE
        iv_result.setImageBitmap(bitmap)
    }

    override fun showOriginalBitmap(bitmap: Bitmap?) {
        loading.visibility = View.GONE
        iv_result.setImageBitmap(bitmap)
        recyclerview.adapter?.notifyItemChanged(0)
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun showRewardVipView() {
        showVipView(true)
    }


    private inner class MyAdapter() :
        RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(this@AlbumResultActivity).inflate(
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
//                showVipView(rewardVideoPresenter.isCanUseFun || datas.get(position).isFree)
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.comic_page_a000, "", position.toString(), "")
                recyclerview.adapter?.notifyItemChanged(pos)
                recyclerview.adapter?.notifyItemChanged(position)
                pos = position
                albumResultPresenter.selectFilter(isVip,position, data.path,data.image,data.cartoon_param)
            }
            holder.name.text = data.name
            if (position == 0){
                holder.result.setImageResource(R.mipmap.album_origin)
            }else{
                GlideUtil.loadImage2(this@AlbumResultActivity,data.result,holder.result)
//                holder.result.setImageResource(data.result)
            }
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


    private class AlbumBean(
        val image: S3ImageInfo,
        val cartoon_param: CartoonParam,
        val result: String,
        var name: String,
        val path: String,
        val isFree: Boolean = true
    )


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13) {
            if (!BillingServiceProxy.isVip()) {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.comic_page_f000,
                    sender, "4", ""
                )
            } else {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.comic_page_f000,
                    sender, "3", ""
                )
            }
            showVipView(true)
        }
    }

    override fun finish() {
        goHomeAndClearTop() {
            super.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ThreadExecutorProxy.clearAsyncThread()
        EventBus.getDefault().unregister(this)
    }

    override fun withoutHotRating() {
        if (!BillingServiceProxy.isVip()) {
            ExitAdManager.showAd()
        }
        finish()
    }

    override fun onPermissionGranted(code: Int) {}

    override fun onPermissionDenied(code: Int) {}

    override fun onPermissionEveryDenied(code: Int) {}
}
