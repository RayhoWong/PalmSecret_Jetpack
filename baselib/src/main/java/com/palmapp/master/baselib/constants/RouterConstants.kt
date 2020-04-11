package com.palmapp.master.baselib.constants

object RouterConstants {
    const val MODULE_PAY = "/module_pay"
    const val MODULE_GOOGLE = "/module_google"
    const val MODULE_PALM = "/module_palm"
    const val MODULE_HOME = "/module_home"
    const val MODULE_PSY = "/module_psy"
    const val MODULE_APP = "/module_app"
    const val MODULE_AD = "/module_ad"
    const val MODULE_CNT = "/module_cnt"
    const val MODULE_ME = "/module_me"
    const val MODULE_TAROT = "/module_tarot"
    const val MODULE_FACE = "/module_face"
    const val MODULE_TRANSFORM = "/module_transform"
    const val MODULE_CARTOON = "/module_cartoon"
    const val MODULE_ALBUM = "/module_album"

    /**
     * google模块
     */
    const val SERVICE_GMS = "$MODULE_GOOGLE/google"   //google服务

    /**
     * 广告sdk模块
     */
    const val SERVICE_AD = "$MODULE_AD/ad"   //google服务

    /**
     * 支付模块
     */
    const val SERVICE_PAY = "$MODULE_PAY/pay"   //支付服务
    const val ACTIVITY_PAY = "$MODULE_PAY/activity"   //订阅页

    /**
     * 手相模块
     */
    const val ACTIVITY_PALM_SCAN = "$MODULE_PALM/scan"
    const val ACTIVITY_PALM_RESULT = "$MODULE_PALM/result"
    const val ACTIVITY_PALM_CHAT = "$MODULE_PALM/chat"
    const val ACTIVITY_PALM_QUIZ = "$MODULE_PALM/quiz"
    const val ACTIVITY_PALM_MATCH = "$MODULE_PALM/match"

    /**
     * 变老模块
     */
    const val ACTVITIY_TakephotoActivity = "$MODULE_HOME/takephoto/TakephotoActivity"
    const val ACTVITIY_FACE_BABY_MATCH = "$MODULE_FACE/baby/match"
    const val ACTVITIY_FACE_BABY_TAKE_PHOTO = "$MODULE_FACE/baby/takephoto"
    const val ACTVITIY_FACE_BABY_RESULT = "$MODULE_FACE/baby/result"

    /**
     * 我的模块
     */
    const val ACTIVITY_MEEditInfoActivity = "$MODULE_ME/me/EditInfoActivity"
    const val ACTIVITY_MESettingsActivity = "$MODULE_ME/me/SettingsActivity"
    const val ACTIVITY_MEPolicyActivity = "$MODULE_ME/me/PolicyActivity"
    const val ACTIVITY_MEFeedbackActivity = "$MODULE_ME/me/FeedbackActivity"

    /**
     * 心理模块
     */

    const val ACTIVITY_PSY_LIST = "$MODULE_PSY/list"
    const val ACTIVITY_PSY_RESULT = "$MODULE_PSY/result"
    const val ACTIVITY_PSY_HEARTRATE_DETECT = "$MODULE_PSY/heartrate_detect"

    /**
     * APP模块
     */
    const val ACTIVITY_APP_MAIN = "$MODULE_APP/MainActivity"


    /**
     * 星座模块
     */
    const val ACTIVITY_CNT_DAILY = "$MODULE_CNT/daily"
    const val ACTIVITY_CNT_MATCHING = "$MODULE_CNT/matching"
    const val ACTIVITY_CNT_SELECT = "$MODULE_CNT/select"

    /**
     * 塔罗模块
     */
    const val ACTIVITY_TAROT_DAILY = "$MODULE_TAROT/daily"
    const val ACTIVITY_TAROT_TOPIC_LOVE = "$MODULE_TAROT/topic/love"
    const val ACTIVITY_TAROT_TOPIC_CAREER = "$MODULE_TAROT/topic/career"

    /**
     * 变性模块
     */
    const val ACTIVITY_TRANSFORM_TAKE_PHOTO = "$MODULE_TRANSFORM/transform/takephoto"

    /**
     * 卡通滤镜模块
     */
    const val ACTIVITY_CARTOON_TAKE_PHOTO = "$MODULE_CARTOON/cartoon/takephoto"
    const val ACTIVITY_CARTOON_RESULT = "$MODULE_CARTOON/cartoon/result"

    /**
     * 变动物模块
     */
    const val ACTIVITY_ANIMAL_TAKE_PHOTO = "$MODULE_FACE/animal/takephoto"
    const val ACTIVITY_ANIMAL_RESULT = "$MODULE_FACE/animal/result"

    /**
     * 魔法相册
     */
    const val ACTIVITY_ALBUM_TAKE_PHOTO = "$MODULE_ALBUM/album/takephoto"
    const val ACTIVITY_ALBUM_RESULT = "$MODULE_ALBUM/album/result"




}