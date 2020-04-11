package com.palmapp.master.module_face.activity.old

import android.os.Bundle
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.module_ad.HotRatingPresenter
import com.palmapp.master.module_ad.IHotRatingView
import com.palmapp.master.module_ad.IRewardVideoView
import com.palmapp.master.module_ad.RewardVideoPresenter
import com.palmapp.master.module_face.R
import com.palmapp.master.module_face.activity.takephoto.fragment.old.OldPresenter

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/19
 */
class OldResultActivity:BaseMultipleMVPActivity(),OldResultView,IHotRatingView,IRewardVideoView {
    private val oldPresenter = OldResultPresenter()
    private val rewardVideoPresenter = RewardVideoPresenter(this)
    private val hotRatingPresenter = HotRatingPresenter(this,rewardVideoPresenter)
    override fun addPresenters() {
        addToPresenter(oldPresenter)
        addToPresenter(rewardVideoPresenter)
        addToPresenter(hotRatingPresenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_fragment_old)

    }

    override fun withoutHotRating() {
    }

    override fun showRewardVipView() {
    }
}