package com.palmapp.master.baselib.statistics;

import android.content.Context;
import android.text.TextUtils;
import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.utils.LogUtil;

/**
 * GO音乐播放器操作统计协议（101-473）
 * wiki：http://wiki.3g.net.cn/pages/viewpage.action?pageId=17239914
 *
 * @author xiejianfeng
 */
public class BaseSeq101OperationStatistic extends AbsBaseStatistic {
    // 操作统计-日志序列
    public static final int OPERATION_LOG_SEQ = 101;

    //统计功能
    public static final String ENTRANCE = "entrance";
    public static final String POSITION = "position";
    // ======================功能点ID======================
    // 功能点ID(473:GO音乐播放器操作统计协议)
    public static final int FUNCTION_ID_DIY = 1992;

    //banner曝光
    public static final String banner_f000 = "banner_f000";
    //banner点击
    public static final String banner_a000 = "banner_a000";
    //tab点击
    public static final String tab_a000 = "tab_a000";


    // 修改个人信息页面曝光
    public static final String info_edit_f000 = "info_edit_f000";
    // 修改个人信息
    public static final String info_edit_a000 = "info_edit_a000";
    // 设置页面曝光 1.姓名 2.生日 3.性别
    public static final String setting_f000 = "setting_f000";
    // 设置页面点击曝光 1.隐私协议 2.用户条例 3.反馈
    public static final String setting_a000 = "setting_a000";
    //手相扫描
    public static final String palm_scan = "palm_scan";
    //手相扫描流程
    public static final String palm_scan_pro = "palm_scan_pro";

    //面部识别流程
    public static final String face_scan_pro = "face_scan_pro";

    //图片权限曝光
    public static final String permission_f000 = "permission_f000";
    //图片权限点击
    public static final String permission_a000 = "permission_a000";
    //相机权限曝光
    public static final String camera_f000 = "camera_f000";
    //相机权限点击
    public static final String camera_a000 = "camera_a000";
    //存储权限曝光
    public static final String storage_f000 = "storage_f000";
    //存储权限点击
    public static final String storage_a000 = "storage_a000";
    //手相匹配点击
    public static final String palm_match_a000  = "palm_match_a000";
    //手相匹配点击
    public static final String palm_match_result  = "palm_match_result";
    //伪装页展示结果
    public static final String show_result_a000 = "show_result_a000";

    public static final String baby_page_f000 = "baby_page_f000";
    public static final String gender_page_f000 = "gender_page_f000";
    //首页对应tab页面曝光
    public static final String tab_f000 = "tab_f000";
    //广告请求
    public static final String ad_request = "ad_request";
    //广告填充
    public static final String ad_request_re = "ad_request_re";
    //广告曝光
    public static final String ad_f000 = "ad_f000";
    //广告点击
    public static final String ad_a000 = "ad_a000";
    //激励视频广告曝光
    public static final String ad_channel_f000 = "ad_channel_f000";
    //激励视频广告点击
    public static final String ad_channel_a000 = "ad_channel_a000";
    //关闭订阅页
    public static final String subs_close_a000 = "subs_close_a000";

    //好评引导
    public static final String rating_f000 = "rating_f000";
    public static final String rating_a000 = "rating_a000";
    /**
     * 上传操作统计数据
     *
     * @param optionCode 操作代码
     */
    public static void uploadOperationStatisticData(String optionCode) {
        uploadOperationStatisticData(GoCommonEnv.INSTANCE.getApplication(), "", optionCode, OPERATE_SUCCESS, "", "", "", "", "");
    }


    /**
     * 上传操作统计数据
     *
     * @param optionCode 操作代码
     * @param sender     统计对象
     */
    public static void uploadOperationStatisticData(String optionCode, String sender) {
        uploadOperationStatisticData(GoCommonEnv.INSTANCE.getApplication(), sender, optionCode, OPERATE_SUCCESS, "", "", "", "",
                "");
    }


    /**
     * 上传操作统计数据
     *
     * @param sender     统计对象
     * @param optionCode 操作代码
     * @param entrance   入口
     */
    public static void uploadOperationStatisticData(String optionCode, String sender, String entrance) {
        uploadOperationStatisticData(GoCommonEnv.INSTANCE.getApplication(), sender, optionCode, OPERATE_SUCCESS, entrance, "",
                "", "", "");
    }

    /**
     * 上传操作统计数据
     *
     * @param sender     统计对象
     * @param optionCode 操作代码
     * @param entrance   入口
     * @param tabId      tab分类
     */
    public static void uploadOperationStatisticData(String optionCode, String sender, String entrance, String tabId) {
        uploadOperationStatisticData(GoCommonEnv.INSTANCE.getApplication(), sender, optionCode, OPERATE_SUCCESS, entrance, tabId,
                "", "", "");
    }

    /**
     * 上传操作统计数据
     *
     * @param sender     统计对象
     * @param optionCode 操作代码
     * @param entrance   入口
     * @param tabId      tab分类
     * @param position   位置
     */
    public static void uploadOperationStatisticData(String optionCode, String sender, String entrance, String tabId,
                                                    String position) {
        uploadOperationStatisticData(GoCommonEnv.INSTANCE.getApplication(), sender, optionCode, OPERATE_SUCCESS, entrance, tabId,
                position, "", "");
    }

    /**
     * 上传操作统计数据
     *
     * @param sender        统计对象
     * @param optionCode    操作代码
     * @param entrance      入口
     * @param tabId         Tab分类
     * @param position      位置
     * @param associatedObj 关联对象
     */
    public static void uploadOperationStatisticData(String optionCode, String sender, String entrance, String tabId,
                                                    String position, String associatedObj) {
        uploadOperationStatisticData(GoCommonEnv.INSTANCE.getApplication(), sender, optionCode, OPERATE_SUCCESS, entrance, tabId,
                position, associatedObj, "");
    }

    /**
     * 上传操作统计数据
     *
     * @param sender        统计对象
     * @param optionCode    操作代码
     * @param entrance      入口
     * @param tabId         Tab分类
     * @param position      位置
     * @param associatedObj 关联对象
     * @param remark        备注
     */
    public static void uploadOperationStatisticData(String optionCode, String sender, String entrance, String tabId,
                                                    String position, String associatedObj, String remark) {
        if (TextUtils.isEmpty(sender)) {
            sender = "null";
        }
        if (TextUtils.isEmpty(entrance)) {
            entrance = "null";
        }
        if (TextUtils.isEmpty(tabId)) {
            tabId = "null";
        }
        if (TextUtils.isEmpty(position)) {
            position = "null";
        }
        if (TextUtils.isEmpty(associatedObj)) {
            associatedObj = "null";
        }
        if (TextUtils.isEmpty(remark)) {
            remark = "null";
        }
        uploadOperationStatisticData(GoCommonEnv.INSTANCE.getApplication(), sender, optionCode, OPERATE_SUCCESS, entrance, tabId,
                position, associatedObj, remark);
    }

    /**
     * 上传操作统计数据
     *
     * @param context
     * @param sender        统计对象
     * @param optionCode    操作代码
     * @param optionResults 操作结果
     * @param entrance      入口
     * @param tabCategory   Tab分类
     * @param position      位置
     * @param associatedObj 关联对象
     * @param remark        备注
     */
    public static void uploadOperationStatisticData(Context context, String sender, String optionCode,
                                                    int optionResults, String entrance, String tabCategory, String
                                                            position, String associatedObj, String remark) {
        uploadOperationStatisticData(context, FUNCTION_ID_DIY, sender, optionCode, optionResults, entrance,
                tabCategory, position, associatedObj, remark);
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
    public static void uploadOperationStatisticData(final Context context, final int funId, final String sender,
                                                    final String optionCode, final int optionResults, final String
                                                            entrance, final String tabCategory,
                                                    final String position, final String associatedObj, final String
                                                            remark) {
        StringBuffer sb = new StringBuffer();
        sb.append("101统计--> funId:").append(FUNCTION_ID_DIY).append(" 统计对象:").append(sender).append(" 操作码:").append(optionCode).append(" 操作结果:").append(optionResults).append(" 入口：").append(entrance)
                .append(" Tab：").append(tabCategory).append(" 位置：").append(position).append(" 关联对象：").append(associatedObj).append(" Remark：").append(remark);
        LogUtil.d("Statistic", sb.toString());
        StringBuffer buffer = createDataBuffer(FUNCTION_ID_DIY, sender, optionCode, optionResults, entrance, tabCategory,
                position, associatedObj, remark);
        // 上传统计数据
        uploadStatisticData(context, OPERATION_LOG_SEQ, FUNCTION_ID_DIY, buffer);
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
