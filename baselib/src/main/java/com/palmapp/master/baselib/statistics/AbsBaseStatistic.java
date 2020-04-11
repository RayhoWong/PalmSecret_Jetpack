package com.palmapp.master.baselib.statistics;

import android.content.Context;
import com.cs.statistic.StatisticsManager;
import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.constants.AppConstants;

/**
 * @author xiejianfeng
 */
public abstract class AbsBaseStatistic {
    public static final int CID2 = 109; //19统计协议 对应的产品id？
    //LOG_TA
    protected static final String LOG_TAG = AbsBaseStatistic.class.getName();
    //操作结果---操作成功
    public final static int OPERATE_SUCCESS = 1;
    //操作结果---操作失败
    public final static int OPERATE_FAIL = 0;
    //拼装上传参数分隔符
    protected static final String STATISTICS_DATA_SEPARATE_STRING = "||";
    //保存统计数据的分割符
    protected static final String STATISTICS_DATA_SEPARATE_ITEM = "#";

    protected static void initStatisticBaseInfo() {
        StatisticsManager.initBasicInfo(GoCommonEnv.INSTANCE.getApplicationId(), GoCommonEnv.INSTANCE.getInnerChannel());
    }

    protected static void upLoadBasicInfoStaticData(final String s, final String uid, final boolean b, final boolean b1, final String user, final boolean isnew, final String s1) {
        try {
            initStatisticBaseInfo();
            StatisticsManager.getInstance(GoCommonEnv.INSTANCE.getApplication()).upLoadBasicInfoStaticData(s, uid, b, b1, user, isnew, s1);

        } catch (Exception e) {

        }
    }

    /**
     * 27协议使用
     * @param data
     */
    protected static void upLoadStaticData(String data) {
        try {
            initStatisticBaseInfo();
            StatisticsManager.getInstance(GoCommonEnv.INSTANCE.getApplication()).upLoadStaticData(data);
        } catch (Exception e) {

        }
    }

    /**
     * 45协议使用
     * @param logid
     * @param funid
     * @param data
     */
    protected static void upLoadStaticData(int logid, int funid, String data) {
        try {
            initStatisticBaseInfo();
            StatisticsManager.getInstance(GoCommonEnv.INSTANCE.getApplication()).upLoadStaticData(logid, funid, data);
        } catch (Exception e) {

        }
    }


    /**
     * 调用统计SDK上传统计数据
     *
     * @param context
     * @param logSequence 日志序列号
     * @param data        不保护基础数据(在SDK中做了处理)的统计数据
     */
    protected static void uploadStatisticData(final Context context, int logSequence, int funId, final StringBuffer data) {
        // 上传统计数据
        try {
            initStatisticBaseInfo();
            StatisticsManager.getInstance(context).uploadStaticData(logSequence, funId, toString(data));
        } catch (Exception e) {
        }
    }


    /**
     * 将Object类型转换为String,且去除空格
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if (obj == null) {
            obj = "";
        }
        return obj.toString().trim();
    }
}
