package com.palmapp.master.baselib.constants

import android.graphics.Color
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.Product
import java.io.File

/**
 *
 * @ClassName:      AppConstants
 * @Description:    应用公共常量定义
 * @Author:         xiemingrui
 * @CreateDate:     2019/7/23
 */
object AppConstants {
    const val CONF_PRODUCT_ID = Product.CONF_PRODUCT_ID

    const val APP_CID = Product.APP_CID

    const val APP_CID2 = Product.APP_CID2

    const val PUBLIC_KEY = Product.PUBLIC_KEY

//    const val APPLICATION_ID = "com.palmsecret.horoscope"

    //        const val HTTP_CNT_HOST = "http://cntcore.3g.net.cn"
    const val HTTP_CNT_HOST = Product.HTTP_CNT_HOST

    const val HTTP_TARCORE_HOST = Product.HTTP_TARCORE_HOST
//    const val HTTP_TARCORE_HOST = "http://tarcore.palm-secret.com"

    const val HTTP_FACECORE_HOST = Product.HTTP_FACECORE_HOST
//    const val HTTP_FACECORE_HOST = "http://faccore.palm-secret.com"

    const val HTTP_CONF_HOST = Product.HTTP_CONF_HOST

    const val HTTP_OLD_CNT_HOST = Product.HTTP_OLD_CNT_HOST

    const val HTTP_FACECORE_HOST_2 = Product.HTTP_FACECORE_HOST_2

    const val HTTP_FACECORE_HOST_2_FACE_DETECTION = Product.HTTP_FACECORE_HOST_2_FACE_DETECTION

    const val HTTP_FACECORE_HOST_2_CARTOON = Product.HTTP_FACECORE_HOST_2_CARTOON

    const val SIGNATURE_DES_CNT = Product.SIGNATURE_DES_CNT
    const val SIGNATURE_DES_TARCORE = Product.SIGNATURE_DES_TARCORE
    const val SIGNATURE_DES_FACE = Product.SIGNATURE_DES_FACE
    const val SIGNATURE_DES_CONF = Product.SIGNATURE_DES_CONF
    const val SIGNATURE_DES_FACE_2 = Product.SIGNATURE_DES_FACE_2


    const val SIGNATURE_CNT_KEY = Product.SIGNATURE_CNT_KEY
    const val SIGNATURE_TAROT_KEY = Product.SIGNATURE_TAROT_KEY
    const val SIGNATURE_FACE_KEY = Product.SIGNATURE_FACE_KEY
    const val SIGNATURE_FACE_KEY_2 = Product.SIGNATURE_FACE_KEY_2

    const val ACTION_AUTO_CHECK_UPDATE = "sehedule.action.AUTO_CHECK_UPDATE";

    const val NEED_SUBSCRIBE = 6

    const val NEED_SUBSCRIBE_AND_CURRENT = 7

    const val TAROT_CARD_PLACE_TYPE_UPRIGHT = 1 //卡片正位
    const val TAROT_CARD_PLACE_TYPE_REVERSED = 2 //卡片逆位

    const val TAROT_CARD_TYPE_LOVE = "love" //爱情
    const val TAROT_CARD_TYPE_FORTUNE = "fortune" //运势
    const val TAROT_CARD_TYPE_CAREER = "career" //事业

    const val TAROT_TOPIC_LOVE = 13 //话题塔罗牌真爱id
    const val TAROT_TOPIC_CAREER = 14 //话题塔罗牌职业id

    // ---------------------------------意见反馈 相关配置------------------------------------
    const val HTTP_FEEDBACK_HOST = Product.HTTP_FEEDBACK_HOST
    const val K_FEEDBACK_DES_KEY = Product.K_FEEDBACK_DES_KEY
    const val K_FEEDBACK_PID = Product.K_FEEDBACK_PID
    const val k_CONTACT_MAIL = Product.k_CONTACT_MAIL

    const val PAY_REQUEST_CODE = 666 //订阅请求码
    const val PAY_RESULT_CODE = 999 //订阅请求结果码

    val FACE_BABY_FATHER = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("father")
    val FACE_BABY_MOTHER = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("mother")
    val FACE_BABY = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("baby")
}