package com.palmapp.master.baselib.constants

/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/29
 */
class PreConstants {
    object Launcher {
        const val KEY_FIRST_PALM_SCAN = "KEY_FIRST_PALM_SCAN"//启动页展示次数
        const val KEY_BILLING_TIMES = "KEY_BILLING_TIMES"//订阅页展示次数
        const val KEY_LAUNCHER_FIRST_TIME = "KEY_LAUNCHER_FIRST_TIME"//是否首次启动
    }

    object Cache {
        const val KEY_CACHE_HOME_PSY = "KEY_CACHE_HOME_PSY" //首页-心理页缓存数据
        const val KEY_CACHE_HOME_PALM = "KEY_CACHE_HOME_PALM" //手相-掌纹缓存数据
    }

    object User {
        const val KEY_USER_INFO = "KEY_USER_INFO"
    }

    object Today {
        const val KEY_TODAY_QUOTE_IDS = "KEY_TODAY_QUOTE_IDS"
        const val KEY_TODAY_CACHE_DAY_OF_YEAR = "KEY_TODAY_CACHE_DAY_OF_YEAR"
        const val KEY_TODAY_CACHE_QUOTE = "KEY_TODAY_CACHE_QUOTE"
        const val KEY_TODAY_CACHE_COLOR = "KEY_TODAY_CACHE_COLOR"
        const val KEY_TODAY_CACHE_CLOTHES = "KEY_TODAY_CACHE_CLOTHES"
        const val KEY_TODAY_CACHE_NUMBER = "KEY_TODAY_CACHE_NUMBER"
        const val KEY_TODAY_CACHE_CNT = "KEY_TODAY_CACHE_CNT"
        const val KEY_TODAY_CACHE_TAROT = "KEY_TODAY_CACHE_TAROT"
        const val KEY_TODAY_CACHE_TAROT_RESULT = "KEY_TODAY_CACHE_TAROT_RESULT"

        //真爱话题塔罗牌
        const val KEY_TODAY_CACHE_TOPIC_LOVE_DAY_OF_YEAR = "KEY_TODAY_CACHE_TOPIC_LOVE_DAY_OF_YEAR"
        const val KEY_TODAY_CACHE_TAROT_TOPIC = "KEY_TODAY_CACHE_TAROT_TOPIC"
        const val KEY_TODAY_CACHE_TAROT_TOPIC_DEFAULT = "KEY_TODAY_CACHE_TAROT_TOPIC_DEFAULT"
        const val KEY_TODAY_CACHE_TAROT_TOPIC_RESULT = "KEY_TODAY_CACHE_TAROT_TOPIC_RESULT"

        //职业话题塔罗牌
        const val KEY_TODAY_CACHE_TOPIC_CAREER_DAY_OF_YEAR =
            "KEY_TODAY_CACHE_TOPIC_CAREER_DAY_OF_YEAR"
        const val KEY_TODAY_CACHE_TAROT_TOPIC_CAREER = "KEY_TODAY_CACHE_TAROT_TOPIC_CAREER"
        const val KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_DEFAULT =
            "KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_DEFAULT"
        const val KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_RESULT =
            "KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_RESULT"

    }

    object App {
        const val KEY_INSTALL_TIME = "KEY_INSTALL_TIME"
        const val KEY_OUTER_CHANNEL_STR = "KEY_OUTER_CHANNEL_STR"
        const val KEY_AB_CONFIG = "KEY_AB_CONFIG"
        const val KEY_ABTEST_FREE_USE = "KEY_ABTEST_FREE_USE"
    }

    object Version {
        const val KEY_LAST_VERSION_CODE = "KEY_LAST_VERSION_CODE"
        const val KEY_CURRENT_VERSION_CODE = "KEY_CURRENT_VERSION_CODE"
        const val KEY_APP_INSTALL_TIME = "KEY_APP_INSTALL_TIME"
    }

    object Pay {
        const val KEY_PAY_USER_TYPE = "KEY_PAY_USER_TYPE"
        const val KEY_PAY_PAYMENT_CONFIG = "KEY_PAY_PAYMENT_CONFIG2"
        const val KEY_FIRST_PAY_CONFIG = "KEY_FIRST_PAY_CONFIG"
    }

    object BuyChannel {
        //是否为没接过买量sdk的旧用户
        const val IS_BUY_SDK_OLD_USER = "IS_BUY_SDK_OLD_USER"
    }

    object Schedule {
        const val KEY_CHECK_TIME = "KEY_CHECK_TIME"
        const val KEY_IS_FIRST_RUN_NEW_UESR = "KEY_IS_FIRST_RUN_NEW_UESR"
    }

    object AbTest {
        const val KEY_ABTEST_VALUE = "KEY_ABTEST_VALUE3"
        const val KEY_HAS_ABTEST_VALUE = "KEY_HAS_ABTEST_VALUE2"
    }

    object First {
        const val KEY_FIRST_RUN = "KEY_FIRST_RUN"
        const val KEY_FIRST_TEST_USER = "KEY_FIRST_TEST_USER"
        const val KEY_FIRST_TEST_USER_VERSION = "KEY_FIRST_TEST_USER_VERSION"
        const val KEY_FIRST_RUN_GUIDE = "KEY_FIRST_RUN_GUIDE"
        const val KEY_FIRST_RUN_PAY_GUIDE = "KEY_FIRST_RUN_PAY_GUIDE"
        const val KEY_FIRST_RUN_MATCH = "KEY_FIRST_RUN_MATCH"
        const val KEY_FIRST_UPLOAD_PERMISSION = "KEY_FIRST_UPLOAD_PERMISSION"
        const val KEY_FIRST_SHOW_WATCH_AD = "KEY_FIRST_SHOW_WATCH_AD"
    }


    object ScoreGuide {
        const val KEY_IS_SCORED = "KEY_IS_SCORED"
        const val KEY_CACHE_NOW_DATE = "KEY_CACHE_NOW_DATE"
        const val KEY_IS_FIRST_SHOW = "KEY_IS_FIRST_SHOW"
        const val KEY_GUIDE_TOTAL_COUNT = "KEY_GUIDE_TOTAL_COUNT"
        const val KEY_IS_FIRST_ENTER_APP = "KEY_IS_FIRST_ENTER_APP"
        const val KEY_IS_FIRST_LIKE = "KEY_IS_FIRST_LIKE"
    }


    object HeartRateDialog {
        const val KEY_IS_FIRST_HEARTRATE_ENTER_APP = "KEY_IS_FIRST_HEARTRATE_ENTER_APP"
    }


    object AD {
        const val KEY_BOOT_AD_SHOW_TIMES = "KEY_BOOT_AD_SHOW_TIMES"
        const val KEY_BOOT_AD_LAST_SHOW_TIME = "KEY_BOOT_AD_LAST_SHOW_TIME"

        const val KEY_TAB_AD_SHOW_TIMES = "KEY_TAB_AD_SHOW_TIMES"
        const val KEY_TAB_AD_LAST_SHOW_TIME = "KEY_TAB_AD_LAST_SHOW_TIME"

        const val KEY_EXIT_PAY_RESULT_SHOW_TIMES = "KEY_EXIT_PAY_RESULT_SHOW_TIMES"
        const val KEY_EXIT_PAY_RESULT_LAST_SHOW_TIME = "KEY_EXIT_PAY_RESULT_LAST_SHOW_TIME"
    }

    object Palm{
        const val KEY_PALM_OLD_RESULT = "KEY_PALM_OLD_RESULT"
        const val KEY_PALM_SHOW_TIME = "KEY_PALM_SHOW_TIME"
        const val KEY_PALM_RESULT = "KEY_PALM_RESULT"
        const val KEY_PALM_CHAT_SHOW = "KEY_PALM_CHAT_SHOW"
    }

    object Face{
        const val KEY_FACE_IS_FIRST_ENTER_ALBUM_TAKE_PHOTO = "KEY_FACE_IS_FIRST_ENTER_ALBUM_TAKE_PHOTO"
        const val KEY_FACE_IS_FIRST_ENTER_ALBUM_CONFIRM = "KEY_FACE_IS_FIRST_ENTER_ALBUM_CONFIRM"
        const val KEY_FACE_IS_FIRST_ENTER_ALBUM_RESULT_TRUE = "KEY_FACE_IS_FIRST_ENTER_ALBUM_RESULT_TRUE"
        const val KEY_FACE_IS_FIRST_ENTER_ALBUM_RESULT_FALSE = "KEY_FACE_IS_FIRST_ENTER_ALBUM_RESULT_FALSE"
    }


}