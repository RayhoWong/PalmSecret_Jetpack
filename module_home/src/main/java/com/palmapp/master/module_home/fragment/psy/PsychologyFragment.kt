package com.palmapp.master.module_home.fragment.psy


import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.applovin.adview.AppLovinAdView
import com.cs.bd.daemon.util.LogUtils
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.NativeAppInstallAd
import com.google.android.gms.ads.formats.NativeAppInstallAdView
import com.google.android.gms.ads.formats.NativeContentAd
import com.google.android.gms.ads.formats.NativeContentAdView
import com.palmapp.master.baselib.BaseFragment
import com.palmapp.master.baselib.bean.quiz.QuizListBean
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_ad.manager.TabBottomAdManager
import com.palmapp.master.module_home.R
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import kotlinx.android.synthetic.main.home_fragment_psychology.*

/**
 * 心里测试
 */
class PsychologyFragment : BaseFragment<PsychologyView, PsychologyPresenter>(), PsychologyView {
    private var mList: MutableList<QuizListBean> = listOf<QuizListBean>().toMutableList()

    override fun showLoading() {
        rcv_psychology.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    override fun showNetworkError() {
    }

    override fun showServerError() {
    }

    override fun showContent(list: List<QuizListBean>?) {
        loading?.visibility = View.GONE
        rcv_psychology?.visibility = View.VISIBLE

        list?.let {
            mList = it.toMutableList()
            mList.add(
                QuizListBean(
                    "ad", "", "", "", ""
                    , "", "", "", true, "", 1
                )
            )
            mList.add(0,
                QuizListBean(
                    "heartrate", "", "", "", ""
                    , "", "", "", true, "", 1
                )
            )

            mAdapter = PsyAdapter(mList)
        }
        rcv_psychology?.layoutManager = LinearLayoutManager(mActivity)
        rcv_psychology?.adapter = mAdapter
    }

    override fun createPresenter(): PsychologyPresenter {
        return PsychologyPresenter()
    }

    private lateinit var mActivity: Activity
    private lateinit var mAdapter: PsyAdapter

    companion object {
        fun newInstance(): PsychologyFragment {
            return PsychologyFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context!! as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment_psychology, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.loadData()
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.heart_page_f000,"","1","")
    }


    private inner class PsyAdapter(val list: MutableList<QuizListBean>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val NORMAL_VIEW = 0
        private val AD_VIEW = 1

        override fun getItemCount() = list.size

        override fun getItemViewType(position: Int): Int {
            val bean = list.get(position)
            return if (TextUtils.equals(bean.quiz_id, "ad")) {
                AD_VIEW
            } else {
                NORMAL_VIEW
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is MyHolder) {
                val info = list.get(position)
                if (info.quiz_id == "heartrate") {
                    holder.mIvIcon.setImageResource(R.mipmap.home_psychology_banner_heart)
                    holder.mTvTitle.text = getString(R.string.psy_heartrate_title)
                    holder.mTvContent.text = getString(R.string.psy_heartrate_des)
                    holder.itemView.setOnClickListener {
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.test_fun_a000, "", "0", "")
                        ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_HEARTRATE_DETECT)
                            .navigation()
                    }
                    return
                }

                ImageLoaderUtils.displayImage(
                    info.cover_img,
                    holder.mIvIcon,
                    R.mipmap.placeholder_circular
                )
                holder.mTvTitle.text = info.title
                holder.mTvContent.text = info.description
                holder.itemView.setOnClickListener {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.test_fun_a000,
                        "",
                        "${position + 1}", ""
                    )
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_LIST)
                        .withString("id", info.quiz_id)
                        .withString("title", info.title).navigation()
                }
            } else if (holder is AdHolder) {
                loadAd(holder, position)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                NORMAL_VIEW -> MyHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.home_item_common_tab,
                        parent,
                        false
                    )
                )
                AD_VIEW -> AdHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.home_item_ad_layout,
                        parent,
                        false
                    )
                )
                else -> MyHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.home_item_common_tab,
                        parent,
                        false
                    )
                )
            }
        }

        @Suppress("MISSING_DEPENDENCY_CLASS")
        private fun loadAd(holder: AdHolder, pos: Int) {
            activity?.let {
                TabBottomAdManager.addAdListener(2, object : TabBottomAdManager.OnAdListener {
                    override fun getAppLovinAdView(appLovinAdView: AppLovinAdView) {
                        appLovinAdView.let { appLovinAdView ->
                            LogUtils.d("CommonTabAdapter", "Banner加载成功 adapter")
                            holder.mContainer.setWrapParent()
                            holder.mRlNative.visibility = View.GONE
                            holder.mIvAd.visibility = View.VISIBLE
                            holder.mNativeContent.addView(appLovinAdView)
                            holder.mIvClose.setOnClickListener {
                                mList.removeAt(pos)
                                notifyItemRemoved(pos)
                                notifyItemRangeChanged(pos, mList.size)
                            }
                        }
                    }

                    override fun getNativeAppInstallAd(ad: NativeAppInstallAd) {
                        ad?.apply {
                            LogUtils.d("CommonTabAdapter", "NativeAppInstallAd加载成功 adapter")
                            holder.mContainer.setWrapParent()
                            holder.mRlNative.visibility = View.VISIBLE
                            var icon = icon
                            icon?.let { image ->
                                holder.mIvIcon.setImageDrawable(image.drawable)
                            }
                            holder.mTvTitle.text = headline
                            holder.mTvContent.text = body
                            var images = images
                            if (!images.isNullOrEmpty()) {
                                val bitmap = (images[0].drawable as BitmapDrawable).bitmap
                                val w = it.resources.getDimensionPixelSize(R.dimen.change_822px)
                                val h = bitmap.height * w / bitmap.width
                                holder.mIvImage.setImageBitmap(bitmap)
                                val lp = holder.mIvImage.layoutParams
                                lp.width = w
                                lp.height = h
                                holder.mIvImage.layoutParams = lp
                            }
                            holder.mIvClose.setOnClickListener {
                                mList.removeAt(pos)
                                notifyItemRemoved(pos)
                                notifyItemRangeChanged(pos, mList.size)
                            }
                            holder.mTvInstall.text = ad.callToAction
                            holder.mNativeAppInstall.iconView = holder.mIvIcon
                            holder.mNativeAppInstall.imageView = holder.mIvImage
                            holder.mNativeAppInstall.headlineView = holder.mTvTitle
                            holder.mNativeAppInstall.bodyView = holder.mTvContent
                            holder.mNativeAppInstall.setNativeAd(ad)
                            holder.mNativeAppInstall.callToActionView = holder.mTvInstall
                        }
                    }

                    override fun getNativeContentAd(ad: NativeContentAd) {
                        ad?.apply {
                            LogUtils.d("CommonTabAdapter", "getNativeContentAd加载成功 adapter")
                            holder.mContainer.setWrapParent()
                            holder.mRlNative.visibility = View.VISIBLE
                            var icon = logo
                            icon?.let { image ->
                                holder.mIvIcon.setImageDrawable(image.drawable)
                            }
                            holder.mTvTitle.text = headline
                            holder.mTvContent.text = body
                            var images = images
                            if (!images.isNullOrEmpty()) {
                                val bitmap = (images[0].drawable as BitmapDrawable).bitmap
                                val w = it.resources.getDimensionPixelSize(R.dimen.change_822px)
                                val h = bitmap.height * w / bitmap.width
                                holder.mIvImage.setImageBitmap(bitmap)
                                val lp = holder.mIvImage.layoutParams
                                lp.width = w
                                lp.height = h
                                holder.mIvImage.layoutParams = lp
                            }
                            holder.mIvClose.setOnClickListener {
                                mList.removeAt(pos)
                                notifyItemRemoved(pos)
                                notifyItemRangeChanged(pos, mList.size)
                            }
                            holder.mTvInstall.text = ad.callToAction
                            holder.mNativeContent.logoView = holder.mIvIcon
                            holder.mNativeContent.imageView = holder.mIvImage
                            holder.mNativeContent.headlineView = holder.mTvTitle
                            holder.mNativeContent.bodyView = holder.mTvContent
                            holder.mNativeContent.setNativeAd(ad)
                            holder.mNativeContent.callToActionView = holder.mTvInstall
                        }
                    }

                    override fun getBannerAdView(adView: AdView) {
                        adView?.let { adView ->
                            LogUtils.d("CommonTabAdapter", "Banner加载成功 adapter")
                            holder.mContainer.setWrapParent()
                            holder.mRlNative.visibility = View.GONE
                            holder.mIvAd.visibility = View.VISIBLE
                            holder.mNativeContent.addView(adView)
                            val w = holder.mNativeContent.width
                            val h = w * 250 / 300
                            val lp = holder.mNativeContent.layoutParams
                            lp.width = w
                            lp.height = h
                            holder.mNativeContent.layoutParams = lp
                            holder.mIvClose.setOnClickListener {
                                mList.removeAt(pos)
                                notifyItemRemoved(pos)
                                notifyItemRangeChanged(pos, mList.size)
                            }
                        }
                    }
                })
            }
        }
    }

    private class AdHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mContainer: RelativeLayout = itemView.findViewById(R.id.fl_container)
        val mIvIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        val mIvImage: ImageView = itemView.findViewById(R.id.iv_image)
        val mIvClose: ImageView = itemView.findViewById(R.id.iv_close)
        val mTvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val mTvContent: TextView = itemView.findViewById(R.id.tv_content)
        val mTvInstall: TextView = itemView.findViewById(R.id.tv_install)
        val mRlNative: RelativeLayout = itemView.findViewById(R.id.rl_native)
        val mNativeAppInstall: NativeAppInstallAdView =
            itemView.findViewById(R.id.native_appinstall)
        val mNativeContent: NativeContentAdView = itemView.findViewById(R.id.native_content)
        val mIvAd: ImageView = itemView.findViewById(R.id.iv_ad2)
    }

    private class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvIcon: ImageView = itemView.findViewById(R.id.iv_icon) //图标
        val mTvTitle: TextView = itemView.findViewById(R.id.tv_title) //标题
        val mTvContent: TextView = itemView.findViewById(R.id.tv_content) //内容
    }

    private fun View.setWrapParent() {
        val lp = this.layoutParams
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.layoutParams = lp
    }
}
