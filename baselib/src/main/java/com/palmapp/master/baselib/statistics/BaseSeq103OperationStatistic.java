package com.palmapp.master.baselib.statistics;

import android.content.Context;

import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.utils.LogUtil;

/**
 * GO音乐播放器测试操作统计协议（103-492） 实时上传统计
 * wiki：http://wiki.3g.net.cn/pages/viewpage.action?pageId=17959318
 *
 * @author xiejianfeng
 */
public class BaseSeq103OperationStatistic extends AbsBaseStatistic {

    //新增测试用的１０３协议　（无开关）
    public static final int OPERATION_LOG_SEQ_103 = 103;


    //统计字段
    public static final String ENTRANCE = "entrance";
    public static final String SENDER = "sender";
    public static final String TAB = "tab";
    public static final String POSITION = "entrance";
    public static final String ASSOCIATED_OBJ = "associatedObj";

    public static final String p001 = "p001";

    //１０３协议的功能ＩＤ　４９２
    public static final int FUNCTION_ID_DIY_103 = 1993;

    public static final String vip_gui_page_f000 = "vip_gui_page_f000";
    public static final String vip_pay_but_a000 = "vip_pay_but_a000";
    public static final String vip_pay_succ = "vip_pay_succ";

    //手相功能点击
    public static final String palm_fun_a000 = "palm_fun_a000";
    //星座功能点击
    public static final String horoscope_fun_a000 = "horoscope_fun_a000";
    //每日星座页面点击
    public static final String horoscope_ever_a000 = "horoscope_ever_a000";
    //塔罗牌功能点击
    public static final String tarot_fun_a000 = "tarot_fun_a000";
    //面相功能点击
    public static final String face_fun_a000 = "face_fun_a000";
    //心理测试功能点击
    public static final String test_fun_a000 = "test_fun_a000";

    //首页曝光（完全进入到首页后上传）
    public static final String home_page_f000 = "home_page_f000";
    //进入应用前页面曝光
    public static final String enter_f000 = "enter_f000";
    //好评引导曝光
    public static final String rating_f000 = "rating_f000";
    //好评引导点击
    public static final String rating_a000 = "rating_a000";
    //点赞
    public static final String thumbs_up_a000 = "thumbs_up_a000";
    //分享
    public static final String share_a000 = "share_a000";
    //下载
    public static final String download_func_a000 = "download_func_a000";
    //顶部分类点击
    public static final String top_tab_a000 = "top_tab_a000";

    //伪装页展示结果
    public static final String show_result_a000 = "show_result_a000";

    //关闭订阅页
    public static final String subs_close_a000 = "subs_close_a000";

    //变老 1.成功 2.识别不出
    public static final String face_scan = "face_scan";
    //艺术滤镜
    public static final String art_page_f000 = "art_page_f000";
    //艺术滤镜
    public static final String art_page_a000 = "art_page_a000";
    //手相成功率
    public static final String palm_succ = "palm_succ";
    //手相扫描流程
    public static final String palm_scan_pro = "palm_scan_pro";

    public static final String wait_palm_a000 = "wait_palm_a000";

    public static final String palm_toast_f000 = "palm_toast_f000";

    //变老弹窗曝光（未到等待时间出现的悬浮弹窗）
    public static final String old_toast_f000 = "old_toast_f000";

    //变老等待点击 1.等待时间前 2.等待时间后
    public static final String wait_old_a000 = "wait_old_a000";
    //心率弹窗曝光
    public static final String heart_rate_f000 = "heart_rate_f000";
    //心率弹窗点击
    public static final String heart_rate_a000 = "heart_rate_a000";
    //心率功能页面曝光
    public static final String heart_page_f000 = "heart_page_f000";
    //变老  脸部识别流程
    public static final String face_scan_pro = "face_scan_pro";

    public static final String subs_page_time = "subs_page_time";

    public static final String subs_page_action = "subs_page_action";

    //漫画曝光
    public static final String comic_page_f000 = "comic_page_f000";
    //漫画滤镜点击（点击了哪一个漫画）
    public static final String comic_page_a000 = "comic_page_a000";
    //漫画成功
    public static final String comic_succ = "comic_succ";
    //点赞功能点击
    public static final String like_a000 = "like_a000";
    //漫画曝光
    public static final String animal_page_f000 = "animal_page_f000";

    public static final String animal_succ = "animal_succ";


    public static void uploadOperationStatisticDataFor103(Context context, String sender, String optionCode,
                                                          int optionResults, String entrance, String tabCategory,
                                                          String position, String associatedObj, String remark) {
        uploadOperationStatisticDataFor103(context, FUNCTION_ID_DIY_103, sender, optionCode,
                optionResults, entrance,
                tabCategory, position, associatedObj, remark);
    }

    public static void uploadOperationStatisticDataFor103(String optionCode, String sender) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(GoCommonEnv.INSTANCE.getApplication(), sender,
                optionCode, BaseSeq103OperationStatistic.OPERATE_SUCCESS,
                null, null, null, null, null);
    }

    public static void uploadOperationStatisticDataFor103(String optionCode) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(GoCommonEnv.INSTANCE.getApplication(), null,
                optionCode, BaseSeq103OperationStatistic.OPERATE_SUCCESS,
                null, null, null, null, null);
    }

    public static void uploadOperationStatisticDataFor103(String optionCode, String sender, String entrance, String tab) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(GoCommonEnv.INSTANCE.getApplication(), sender,
                optionCode, BaseSeq103OperationStatistic.OPERATE_SUCCESS,
                entrance, tab, null, null, null);
    }

    public static void uploadOperationStatisticDataFor103(String optionCode, String sender, String entrance, String tab, String position) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(GoCommonEnv.INSTANCE.getApplication(), sender,
                optionCode, BaseSeq103OperationStatistic.OPERATE_SUCCESS,
                entrance, tab, position, null, null);
    }

    public static void uploadOperationStatisticDataFor103(String optionCode, String sender, String entrance, String tab, String position, String associatedObj) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(GoCommonEnv.INSTANCE.getApplication(), sender,
                optionCode, BaseSeq103OperationStatistic.OPERATE_SUCCESS,
                entrance, tab, position, associatedObj, null);
    }

    public static void uploadOperationStatisticDataFor103(String optionCode, String sender, String entrance, String tab, String position, String associatedObj, String remark) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(GoCommonEnv.INSTANCE.getApplication(), sender,
                optionCode, BaseSeq103OperationStatistic.OPERATE_SUCCESS,
                entrance, tab, position, associatedObj, remark);
    }


    public static void uploadOperationStatisticDataFor103(String optionCode, String sender, String tabId) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(GoCommonEnv.INSTANCE.getApplication(), sender,
                optionCode, BaseSeq103OperationStatistic.OPERATE_SUCCESS,
                null, tabId, null, null, null);
    }

    /**
     * 上传操作统计数据
     *
     * @param context
     * @param funId         功能ID
     * @param sender        统计对象
     * @param optionCode    操作代码
     * @param optionResults 操作结果
     * @param entrance      入口
     * @param tabCategory   Tab分类
     * @param position      位置
     * @param associatedObj 关联对象
     * @param remark        备注
     */
    private static void uploadOperationStatisticDataFor103(final Context context, final int funId, final String sender,
                                                           final String optionCode, final int optionResults, final
                                                           String entrance, final String tabCategory,
                                                           final String position, final String associatedObj, final
                                                           String remark) {
        StringBuffer sb = new StringBuffer();
        sb.append("103统计--> funId:").append(FUNCTION_ID_DIY_103).append(" 统计对象:").append(sender).append(" 操作码:").append(optionCode).append(" 操作结果:").append(optionResults).append(" 入口：").append(entrance)
                .append(" Tab：").append(tabCategory).append(" 位置：").append(position).append(" 关联对象：").append(associatedObj).append(" Remark：").append(remark);
        LogUtil.d("Statistic", sb.toString());
        StringBuffer buffer = createDataBuffer(FUNCTION_ID_DIY_103, sender, optionCode, optionResults, entrance, tabCategory,
                position, associatedObj, remark);
        // 上传统计数据
        uploadStatisticData(context, OPERATION_LOG_SEQ_103, FUNCTION_ID_DIY_103, buffer);
    }


    private static StringBuffer createDataBuffer(Object... dataItems) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < dataItems.length - 1; i++) {
            buffer.append(dataItems[i]);
            buffer.append(STATISTICS_DATA_SEPARATE_STRING);
        }
        buffer.append(dataItems[dataItems.length - 1]);
        return buffer;
    }

}
