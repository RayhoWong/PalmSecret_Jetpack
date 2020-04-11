package com.palmapp.master.baselib

/**
 *
 * @author :     xiemingrui
 * @since :      2019/9/2
 */
//脚本替换文件，不要随便更改
object Product {
    //基础服务产品id
    const val CONF_PRODUCT_ID = 1750
    //ABtest产品id
    const val APP_CID = 394
    //统计协议的产品ID
    const val APP_CID2 = 483
    //GP支付秘钥
    const val PUBLIC_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh4XAbMY1qxQXHqIVyCzbfbHWT3b2S+pAhHTtivRzORfWl+ecd/MtB4jjUXYN+pWBJ4FXYTkniDg5orMjYKhbVX6MawUwaG1tLNBJzrYZueZPBk8qQIz+TVQwWAz+7OP6eQ7YEMX/3UtLjhJM3lEuPxTo4nvX9zeiA9PJxvftHDlbNVeSII4OopzCDkFpLW4DYrSews3PMHK6twwRpIz5cy/epED8wzv5p8GVGXfGWuEefw7qLqeHlaPHiY7qMzHRBf/D1HtDQHLTs0gkPddYWNqL0pEwtkV0BWmHteIeG9wFSkVl5bzJDNZ+g8cX+vjjSyUXnarRsFLcGozpXf1oiQIDAQAB"

    const val PRODUCT_KEY = "QMT0KB9TCK8ZLMJD3MDCSDM3"
    const val ACCESS_KEY  = "SJ07K9OK3GFJA0FWUZU79JJADU16DZSJ"
    const val APP_CID3 = "479"
    //星座Host
//    const val HTTP_CNT_HOST = "http://cntcore.3g.net.cn"
    const val HTTP_CNT_HOST = "http://cstcore.palm-secret.com"
    //塔罗牌Host
//    const val HTTP_TARCORE_HOST = "http://tarcore.3g.net.cn"
    const val HTTP_TARCORE_HOST = "http://tarcore.palm-secret.com"
    //面相Host
    const val HTTP_FACECORE_HOST = "http://faccore.3g.net.cn"
    //        const val HTTP_FACECORE_HOST = "http://faccore.palm-secret.com"
    //基础服务后台HOST
    const val HTTP_CONF_HOST = "https://navigation.gomo.com"
    //老版星座接口HOST
    const val HTTP_OLD_CNT_HOST = "http://horoscope-dev.3g.net.cn"

    //新版人脸后台Host
    const val HTTP_FACECORE_HOST_2 = "http://vision.palm-secret.com"
    //新版人脸检测后台Host
    const val HTTP_FACECORE_HOST_2_FACE_DETECTION = "/api/v2/face/face_detection"
    //新版人脸卡通效果
    const val HTTP_FACECORE_HOST_2_CARTOON = "/api/v2/face/cartoon"

    const val SIGNATURE_DES_FACE_2 = "8fT4khVbUgE"

    const val SIGNATURE_DES_CNT = "CONSTLLT"

    const val SIGNATURE_DES_TARCORE = "TAROT189"

    const val SIGNATURE_DES_FACE = "HJGTYE76"

    const val SIGNATURE_DES_CONF = "X2kaSwGEVusxdwSOOpNshg"

    const val SIGNATURE_CNT_KEY = "539A221BC18B0C1F"  //星座签名秘钥
    const val SIGNATURE_TAROT_KEY = "A221BC18B5390C1F"  //塔罗牌签名秘钥
    const val SIGNATURE_FACE_KEY = "539A228B0C1F1BC1"  //面相签名秘钥
    const val SIGNATURE_FACE_KEY_2 = "mfzOmzczDZZOyaQthAePrvdwvRRxFjiF"  //新面相签名秘钥

    //反馈相关
    const val HTTP_FEEDBACK_HOST = "https://fb.cpcphone.com"

    const val K_FEEDBACK_DES_KEY = "K8N9X68T"

    const val K_FEEDBACK_PID = "247"

    const val k_CONTACT_MAIL = "flashbrowserhelp@gmail.com"

    const val DEFAULT_SKU_MONTH = "test.palmsecret.1month"

    const val DEFAULT_SKU_YEAR = "test.palmsecret.1year"

    const val MODULE_AD_LAUNCHER = 362757

    const val MODULE_AD_RESULT = 362757

    const val MODULE_AD_EXIT_APP = 362757

    const val MODULE_ID_QUOTE = 20685

    const val MODULE_ID_PAY_GUIDE = 20031


    const val MODULE_AD_TAB_BOTTOM = 8822

    const val MODULE_AD_BOOT_INSET = 8824

    const val MAGIC_ALBUM_MODULE_ID = 100064
}