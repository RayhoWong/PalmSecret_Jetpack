package com.palmapp.master.module_pay.pay

import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.module_pay.R
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/2
 */
class PayVideoHelper(val peopleStyle: String) {
    var order_palm = 6
    var order_heart_beat = 5
    var order_art = 4
    var order_old = 3
    var order_baby = 2
    var order_transform = 1

    val datas = ArrayList<DataHolder>(6)
    private val people = if (peopleStyle == "asian") {
        Asian()
    } else {
        Latin()
    }
    val pics = people.createPics()
    val videos = people.createVideos()
    val smallPics = people.createSmallPics()

    init {
        val campaign = BuyChannelProxy.getBuyChannelBean().campaign.replace("palmsecret", "").toLowerCase()
        when {
            campaign.contains("old") -> {
                order_old = 100
            }
            campaign.contains("baby") -> {
                order_baby = 100
            }
            campaign.contains("heart") || campaign.contains("heartrate") -> {
                order_heart_beat = 100
            }
            campaign.contains("art") -> {
                order_art = 100
            }
        }
    }

    fun setText(text: ArrayList<String>) {
        datas.clear()
        datas.add(DataHolder(1, order_palm, pics[0], smallPics[0], videos[0], text[0]))
        datas.add(DataHolder(2, order_heart_beat, pics[1], smallPics[1], videos[1], text[1]))
        datas.add(DataHolder(3, order_art, pics[2], smallPics[2], videos[2], text[2]))
        datas.add(DataHolder(4, order_old, pics[3], smallPics[3], videos[3], text[3]))
        datas.add(DataHolder(5, order_baby, pics[4], smallPics[4], videos[4], text[4]))
        datas.add(DataHolder(6, order_transform, pics[5], smallPics[5], videos[5], text[5]))
        datas.sort()
    }
}


private abstract class People() {
    abstract fun createPics(): ArrayList<Int>
    abstract fun createVideos(): ArrayList<Int>
    abstract fun createSmallPics(): ArrayList<Int>
    val pics = createPics()
    val videos = createVideos()
}

private class Asian() : People() {
    override fun createPics() = arrayListOf(
        R.mipmap.style_pic1,
        R.mipmap.style_pic8,
        R.mipmap.style_pic7,
        R.mipmap.style_asia_pic2,
        R.mipmap.style_asia_pic3,
        R.mipmap.style_asia_pic4
    )

    override fun createVideos() = arrayListOf(
        R.raw.style_asia_video1,
        R.raw.style_video8,
        R.raw.style_asia_video7,
        R.raw.style_asia_video2,
        R.raw.style_asia_video3,
        R.raw.style_asia_video4
    )

    override fun createSmallPics() = arrayListOf(
        R.mipmap.pay_premium_asia_pic_palm,
        R.mipmap.pay_premium_pic_heart,
        R.mipmap.pay_premium_asia_pic_filter,
        R.mipmap.pay_premium_asia_pic_old,
        R.mipmap.pay_premium_asia_pic_baby,
        R.mipmap.pay_premium_asia_pic_gender
    )
}

private class Latin() : People() {
    override fun createPics() = arrayListOf(
        R.mipmap.style_pic1,
        R.mipmap.style_pic8,
        R.mipmap.style_pic7,
        R.mipmap.style_pic2,
        R.mipmap.style_pic3,
        R.mipmap.style_pic4
    )

    override fun createVideos() = arrayListOf(
        R.raw.style_video1,
        R.raw.style_video8,
        R.raw.style_video7,
        R.raw.style_video2,
        R.raw.style_video3,
        R.raw.style_video4
    )

    override fun createSmallPics() = arrayListOf(
        R.mipmap.pay_premium_europe_pic_palm,
        R.mipmap.pay_premium_pic_heart,
        R.mipmap.pay_premium_europe_pic_filter,
        R.mipmap.pay_premium_europe_pic_old,
        R.mipmap.pay_premium_europe_pic_baby,
        R.mipmap.pay_premium_europe_pic_gender
    )

}

class DataHolder(val type: Int, val order: Int, val pic: Int, val smallPic: Int, val video: Int, val text: String) : Comparable<DataHolder> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: DataHolder): Int {
        return other.order - order
    }

}