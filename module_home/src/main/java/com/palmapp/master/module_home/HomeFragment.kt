package com.palmapp.master.module_home

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.bingoogolapple.bgabanner.BGABanner
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayout
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.module_ad.manager.TabBottomAdManager
import com.palmapp.master.module_home.fragment.ConstellationFragment
import com.palmapp.master.module_home.fragment.FaceFragment
import com.palmapp.master.module_home.fragment.PlamFragment
import com.palmapp.master.module_home.fragment.TarotFragment
import com.palmapp.master.module_home.fragment.psy.PsychologyFragment
import kotlinx.android.synthetic.main.home_fragment_home.*
import kotlinx.android.synthetic.main.home_layout_banner_video.*

/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/31
 * 首页
 */
class HomeFragment : Fragment() {

    private lateinit var mImages: List<String> //banner图
    private lateinit var mFragments: List<Fragment>
    private lateinit var mActivity: Activity
    private val mMediaPlayer = MediaPlayer()
    private val surfaces = arrayOfNulls<Surface>(3)
    private val video = arrayOf(R.raw.banner_palm, R.raw.banner_old, R.raw.banner_baby)
    private var mPos = 0

    private val pics = arrayListOf<Int>()
    private val holders = arrayListOf<ImageView>()

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (holders.isNotEmpty()) {
                holders.get(msg?.what ?: 0).visibility = View.GONE
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as Activity
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initBanner()
        initViewPager()

    }

    override fun onPause() {
        super.onPause()
        try {
            mMediaPlayer.stop()
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        banner.postDelayed(Runnable {
            play(mPos)
        }, 1000)
    }


    private fun play(position: Int) {
        if (surfaces[position]?.isValid == false)
            return
        try {
            mMediaPlayer.stop()
        } catch (e: Exception) {

        }
        try {
            holders.forEach {
                it.visibility = View.VISIBLE
            }
            mMediaPlayer.reset()
            mMediaPlayer.setSurface(surfaces[position])
            val afd = resources.openRawResourceFd(video[position])
            mMediaPlayer.setDataSource(
                afd.getFileDescriptor(),
                afd.getStartOffset(),
                afd.getLength()
            )
            mMediaPlayer.prepareAsync()
        } catch (e: Exception) {

        }
    }

    //初始化Banner
    private fun initBanner() {
        pics.clear()
        pics.add(R.mipmap.palm_first)
        pics.add(R.mipmap.old_first)
        pics.add(R.mipmap.old_first)
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inSampleSize = 2
        mImages = listOf(
            ""
            , ""
            , ""
        )
        banner.setAutoPlayAble(false)
        banner.setData(R.layout.home_layout_banner_video, mImages, null)
        banner.viewPager.offscreenPageLimit = 3
        banner.setAdapter(object : BGABanner.Adapter<FrameLayout, String> {
            override fun fillBannerItem(
                banner: BGABanner,
                itemView: FrameLayout,
                model: String?,
                position: Int
            ) {

                val holder = itemView.findViewById<ImageView>(R.id.iv_holder)
                holder.setImageBitmap(BitmapFactory.decodeResource(resources,pics[position],options))
                holders.add(holder)

                val textureView = itemView.findViewById<TextureView>(R.id.tv_video)
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
                        surfaces[position]?.release()
                        return true
                    }

                    override fun onSurfaceTextureAvailable(
                        surface: SurfaceTexture?,
                        width: Int,
                        height: Int
                    ) {
                        surfaces[position] = Surface(surface)
                    }

                }
            }
        })
        banner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                play(position)
                when (position) {
                    0 -> {//每日手相
                        mPos = 0
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(
                            BaseSeq101OperationStatistic.banner_f000,
                            "",
                            "1"
                        )
//                        tv_banner_title.text = getString(R.string.palm_daily_title)
//                        tv_banner_content.text = getString(R.string.palm_daily_des)

                    }
                    1 -> {//变老
                        mPos = 1
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(
                            BaseSeq101OperationStatistic.banner_f000,
                            "",
                            "4"
                        )
//                        tv_banner_title.text = getString(R.string.cnt_match_title)
//                        tv_banner_content.text = getString(R.string.cnt_match_des)
                    }
//                    2 -> {//每日塔罗
//                        mPos = 2
//                        BaseSeq101OperationStatistic.uploadOperationStatisticData(
//                            BaseSeq101OperationStatistic.banner_f000,
//                            "",
//                            "3"
//                        )
//                        tv_banner_title.text = getString(R.string.tarot_daily_title)
//                        tv_banner_content.text = getString(R.string.tarot_daily_des)
//                    }
                    2 -> {//宝宝
                        mPos = 2
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(
                            BaseSeq101OperationStatistic.banner_f000,
                            "",
                            "5"
                        )
//                        tv_banner_title.text = getString(R.string.face_daily_title)
//                        tv_banner_content.text = getString(R.string.face_daily_des)
                    }
                }
            }
        })
//        //点击Item监听
        banner.setDelegate { banner, itemView, model, position ->
            when (position) {
                0 -> {//手相
                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
                        BaseSeq101OperationStatistic.banner_a000,
                        "",
                        "1"
                    )
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_SCAN).navigation()
                }
                1 -> {//变老
                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
                        BaseSeq101OperationStatistic.banner_a000,
                        "",
                        "4"
                    )
                    ARouter.getInstance().build(RouterConstants.ACTVITIY_TakephotoActivity)
                        .navigation()
                }
//                2 -> {//塔罗
//                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
//                        BaseSeq101OperationStatistic.banner_a000,
//                        "",
//                        "3"
//                    )
//                    ARouter.getInstance().build(RouterConstants.ACTIVITY_TAROT_DAILY).navigation()
//                }
                2 -> {//宝宝
                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
                        BaseSeq101OperationStatistic.banner_a000,
                        "",
                        "5"
                    )
                    ARouter.getInstance().build(RouterConstants.ACTVITIY_FACE_BABY_MATCH)
                        .navigation()
                }
            }
        }
        mMediaPlayer.setOnPreparedListener {
            handler.removeCallbacksAndMessages(null)
            handler.sendEmptyMessageDelayed(mPos, 250)
            mMediaPlayer.start()
        }
        mMediaPlayer.setOnCompletionListener {
            if (banner != null) {
                //切换视频
                banner.viewPager.setCurrentItem(
                    if ((banner.currentItem + 1) <= 2) banner.currentItem + 1 else 0,
                    true
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.stop()
        mMediaPlayer.release()
    }

    //初始化viewpager
    private fun initViewPager() {
        mFragments = listOf(
            FaceFragment.newInstance(), PlamFragment.newInstance(),
            PsychologyFragment.newInstance(), ConstellationFragment.newInstance()
            , TarotFragment.newInstance()
        )
        var homePagerAdapter = HomePagerAdapter(childFragmentManager, mFragments)
        viewPager.adapter = homePagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        for (index in 0..4) {
            tabLayout.getTabAt(index)?.customView = homePagerAdapter.getCustomView(index, mActivity)
        }
        //默认选中第一个
        tabLayout.getTabAt(0)?.select()
        activity?.let {
            TabBottomAdManager.preLoad(it, 0)
        }
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.top_tab_a000,
            "",
            "4",""
        )
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                TabBottomAdManager.releaseTab(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                activity?.let {
                    TabBottomAdManager.preLoad(it, tab?.position ?: 0)
                }
                when (tab?.position) {
                    0 ->
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.top_tab_a000,
                            "",
                            "4",""
                        )
                    1 ->
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.top_tab_a000,
                            "",
                            "1",""
                        )
                    2 ->
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.top_tab_a000,
                            "",
                            "5",""
                        )
                    3 ->
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.top_tab_a000,
                            "",
                            "2",""
                        )
                    4 ->
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.top_tab_a000,
                            "",
                            "3",""
                        )
                }
            }
        })
    }


    private class HomePagerAdapter(fm: FragmentManager, var fragments: List<Fragment>) :
        FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

        /**
         * 设置每个tab的视图
         * Tab的顺序不确定 后期会改变
         */
        fun getCustomView(position: Int, context: Activity): View {
            var view = LayoutInflater.from(context).inflate(R.layout.home_layout_tab, null)
            var rlBg = view.findViewById<RelativeLayout>(R.id.rl_bg)//tab的背景
            var ivIcon = view.findViewById<ImageView>(R.id.iv_icon)//tab的图标
            var tvTabName = view.findViewById<TextView>(R.id.tv_tabname)//tab标题
            var ivFree = view.findViewById<ImageView>(R.id.iv_free)//免费标记
            when (position) {
                0 -> {//面相
                    rlBg.setBackgroundResource(R.drawable.home_selector_tab_face)
                    ivIcon.setImageResource(R.mipmap.home_ic_face)
                    tvTabName.text = context.getString(R.string.home_face_title)
                    ivFree.visibility = View.GONE
                }
                1 -> {//手相
                    rlBg.setBackgroundResource(R.drawable.home_selector_tab_plam)
                    ivIcon.setImageResource(R.mipmap.home_ic_plam)
                    tvTabName.text = context.getString(R.string.home_palm_title)
                    ivFree.visibility = View.GONE
                }
                2 -> {//心理测试
                    rlBg.setBackgroundResource(R.drawable.home_selector_tab_psychology)
                    ivIcon.setImageResource(R.mipmap.home_ic_psychology)
                    tvTabName.text = context.getString(R.string.home_psy_title)
                    ivFree.visibility = View.GONE
                }
                3 -> {//星座
                    rlBg.setBackgroundResource(R.drawable.home_selector_tab_constellation)
                    ivIcon.setImageResource(R.mipmap.home_ic_constellation)
                    tvTabName.text = context.getString(R.string.home_cnt_title)
                    ivFree.visibility = View.VISIBLE
                }
                4 -> {//塔罗牌
                    rlBg.setBackgroundResource(R.drawable.home_selector_tab_tarot)
                    ivIcon.setImageResource(R.mipmap.home_ic_tarot)
                    tvTabName.text = context.getString(R.string.home_tarot_title)
                    ivFree.visibility = View.VISIBLE
                }
                else -> return view
            }
            return view
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        TabBottomAdManager.destroy()
    }

}