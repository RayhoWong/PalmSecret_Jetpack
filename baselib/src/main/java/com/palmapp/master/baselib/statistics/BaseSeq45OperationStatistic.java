package com.palmapp.master.baselib.statistics;

import android.content.Context;
import android.text.TextUtils;
import com.cs.statistic.StatisticsManager;
import com.cs.statistic.utiltool.Machine;
import com.cs.statistic.utiltool.UtilTool;
import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.utils.AppUtil;
import com.palmapp.master.baselib.utils.VersionController;

/**
 * 45协议 合作方推GO桌面统计协议（45-476）
 * wiki: http://wiki.3g.net.cn/pages/viewpage.action?pageId=17956882
 *
 * @author xiejianfeng
 */
public class BaseSeq45OperationStatistic extends AbsBaseStatistic {


    private static final String PROTOCOL_DIVIDER = "||";

    private final static int LOG_SEQUENCE = 45;

    public final static int FUNCTION_ID = 1991;

    private final static String OPERATION_CODE = "k001";

    private final static int RESULT_CODE = 1;


    /**
     * 普通传GA链接时候调用
     *
     * @param context
     * @param ga
     */
    public static void uploadStatisticData(Context context, String ga) {
        uploadStatisticData(context, ga, null);
    }

    /**
     * 部分需要上传备份信息  如:  adwords需要上传agency信息。
     *
     * @param context
     * @param ga
     * @param mark
     */
    public static void uploadStatisticData(Context context, String ga, String mark) {
        String upload = makeInfo(context, ga, null, null, null, mark);
        upLoadStaticData(45, 96, upload);
    }


    private static String makeInfo(Context context, String ga, String entrance,
                                   String tab, String position, String mark) {
        StringBuilder sb = new StringBuilder(20);
        sb.append(LOG_SEQUENCE).append(PROTOCOL_DIVIDER); // 日志序列s
        sb.append(Machine.getAndroidId(context)).append(PROTOCOL_DIVIDER); // Android
        // ID
        sb.append(UtilTool.getBeiJinTime(System.currentTimeMillis())).append(
                PROTOCOL_DIVIDER); // 日志打印时间
        sb.append(FUNCTION_ID).append(PROTOCOL_DIVIDER); // 功能点ID
        sb.append(ga).append(PROTOCOL_DIVIDER); // 统计对象
        sb.append(OPERATION_CODE).append(PROTOCOL_DIVIDER); // 操作代码
        sb.append(RESULT_CODE).append(PROTOCOL_DIVIDER); // 操作结果
        sb.append(Machine.getSimCountryIso(context, true)).append(PROTOCOL_DIVIDER); // 国家
        sb.append(GoCommonEnv.INSTANCE.getInnerChannel()).append(PROTOCOL_DIVIDER); // 渠道

        sb.append(VersionController.INSTANCE.getVersionCode()) // 版本号
                .append(PROTOCOL_DIVIDER);
        sb.append(VersionController.INSTANCE.getVersionName()).append(PROTOCOL_DIVIDER);  // 版本名
        if (!TextUtils.isEmpty(entrance)) { // 入口
            sb.append(entrance).append(PROTOCOL_DIVIDER);
        } else {
            sb.append("").append(PROTOCOL_DIVIDER);
        }
        if (!TextUtils.isEmpty(tab)) { // tab分类
            sb.append(tab).append(PROTOCOL_DIVIDER);
        } else {
            sb.append("").append(PROTOCOL_DIVIDER);
        }
        if (!TextUtils.isEmpty(position)) { // 位置
            sb.append(position).append(PROTOCOL_DIVIDER);
        } else {
            sb.append("").append(PROTOCOL_DIVIDER);
        }
        sb.append("").append(PROTOCOL_DIVIDER);
        sb.append(StatisticsManager.getUserId(context)).append(PROTOCOL_DIVIDER); // goid
        sb.append("").append(PROTOCOL_DIVIDER); // 关联对象
        // 当被推的对象为主题时，此字段上传主题的包名；当被推的对象为主程序时，此字段可以为空；
        if (!TextUtils.isEmpty(mark)) { // 备注
            sb.append(mark).append(PROTOCOL_DIVIDER);
        } else {
            sb.append("").append(PROTOCOL_DIVIDER);
        }
        sb.append(AppUtil.getGoogleAdvertisingId()); // GADID
        return sb.toString();
    }

}
