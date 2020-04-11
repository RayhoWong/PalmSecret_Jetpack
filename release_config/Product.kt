package com.palmapp.master.baselib

/**
 *
 * @author :     xiemingrui
 * @since :      2019/9/2
 */
//脚本替换文件，不要随便更改
object Product {
    //基础服务产品id
    const val CONF_PRODUCT_ID = 1749
    //产品id
    const val APP_CID = 393
    //统计协议的产品ID
    const val APP_CID2 = 483
    //GP支付秘钥
    const val PUBLIC_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA/EDlPCtdg/byViCDAwAQSI6i+HvCSx94IeLsCTeh/Zf1VPy6ypzH0yvuxSG8f2o7A7MVhJYlEpdhysXlY7ZQx3zOGCSq1vq3yw/w0b4YDFrXnthQIoikGVOCcOOCo2NqzPniEtz0dnOCNouxskLA37GsIxS3D9MSopADzz3mmWzGcD8uVEq2CZ5Zq1SBHXrdxp6k6x538P2hqRIChjz2b9bdWxa9duvIoFL6RHNrlLFMFiEVZU4JZv1IYq7Krfwzkpq9wuJY30/tud0vbSLeyXdXYdGBAfUw2Hfr7/t875Wkr/UR9LDVFWnHDMY8GWKjpUOe12a9J6sw2OYABhWCDQIDAQAB"
    const val PRODUCT_KEY = "BMIS4O7CHNJFCVXZYIX5DBVR"
    const val ACCESS_KEY  = "MR9KMFNQHA23VRE8061QIF638NJTXF4Z"
    const val APP_CID3 = "478"
    //星座Host
//    const val HTTP_CNT_HOST = "http://cntcore.3g.net.cn"
        const val HTTP_CNT_HOST = "http://cstcore.palm-secret.com"
    //塔罗牌Host
//    const val HTTP_TARCORE_HOST = "http://tarcore.3g.net.cn"
        const val HTTP_TARCORE_HOST = "http://tarcore.palm-secret.com"
    //面相Host
//    const val HTTP_FACECORE_HOST = "http://faccore.3g.net.cn"
        const val HTTP_FACECORE_HOST = "http://faccore.palm-secret.com"
    //基础服务后台HOST
    const val HTTP_CONF_HOST = "https://navigation.gomo.com"

    //老版星座接口HOST
    const val HTTP_OLD_CNT_HOST = "https://horoscope.bbcget.com"

    //新版人脸后台Host
    const val HTTP_FACECORE_HOST_2 = "http://vision.palm-secret.com"
    //新版人脸检测后台Host
    const val HTTP_FACECORE_HOST_2_FACE_DETECTION = "/api/public/v2/face/face_detection"
    //新版人脸卡通效果
    const val HTTP_FACECORE_HOST_2_CARTOON = "/api/public/v2/face/cartoon"

    const val SIGNATURE_DES_FACE_2 = "8fT4khVbUgE"

    const val SIGNATURE_DES_CNT = "CONSTLLT"

    const val SIGNATURE_DES_TARCORE = "TAROT189"

    const val SIGNATURE_DES_FACE = "HJGTYE76"

    const val SIGNATURE_DES_CONF = "D2-mU9aZb-kGb3PGOvTpLA"

    const val SIGNATURE_CNT_KEY = "539A221BC18B0C1F"  //星座签名秘钥
    const val SIGNATURE_TAROT_KEY = "A221BC18B5390C1F"  //塔罗牌签名秘钥
    const val SIGNATURE_FACE_KEY = "539A228B0C1F1BC1"  //面相签名秘钥
    const val SIGNATURE_FACE_KEY_2 = "mfzOmzczDZZOyaQthAePrvdwvRRxFjiF"  //新面相签名秘钥

    //反馈相关
    const val HTTP_FEEDBACK_HOST = "https://fb.cpcphone.com"

    const val K_FEEDBACK_DES_KEY = "K8N9X68T"

    const val K_FEEDBACK_PID = "247"

    const val k_CONTACT_MAIL = "flashbrowserhelp@gmail.com"

    const val DEFAULT_SKU_MONTH = "palmsecret.vip.1month.3daystrial"

    const val DEFAULT_SKU_YEAR = "palmsecret.vip.1year.3daystrial"

    const val MODULE_AD_LAUNCHER = 363955

    const val MODULE_AD_RESULT = 363993

    const val MODULE_AD_EXIT_APP = 363974

    const val MODULE_ID_QUOTE = 20811

    const val MODULE_ID_PAY_GUIDE = 20948

    const val MAGIC_ALBUM_MODULE_ID = 100041
}