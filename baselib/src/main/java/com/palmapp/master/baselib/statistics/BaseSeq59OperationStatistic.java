package com.palmapp.master.baselib.statistics;

import android.content.Context;
import android.text.TextUtils;

import com.cs.statistic.StatisticsManager;
import com.cs.statistic.utiltool.Machine;
import com.cs.statistic.utiltool.UtilTool;
import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.buychannel.BuyChannelProxy;
import com.palmapp.master.baselib.manager.config.ConfigManager;
import com.palmapp.master.baselib.manager.config.PayConfig;
import com.palmapp.master.baselib.utils.AppUtil;
import com.palmapp.master.baselib.utils.LogUtil;
import com.palmapp.master.baselib.utils.VersionController;

/**
 * Created by huangjianfeng on 2017/11/20.
 * 付费相关59统计
 * http://wiki.3g.net.cn/pages/viewpage.action?pageId=6914386
 */

public class BaseSeq59OperationStatistic extends AbsBaseStatistic {


    private static final String PROTOCOL_DIVIDER = "||";

    private final static int LOG_SEQUENCE = 59;

    public final static String FUNCTION_ID = GoCommonEnv.INSTANCE.getApplicationId();

    //付费查询或验证不需要重复上传购买成功的日志，通过“p001”操作上传
    public static final String OPERATION_GOOGLE_PLAY_BUY = "j005";

    //用于查询帐号是否付费过，验证成功上传
    public static final String OPERATION_PURCHASE_VERIFICATION = "p001";

    public static final String OPERATION_F000_VIP = "f000";

    public static final String OPERATION_P001 = "p001";

    //j005、j006、j007 操作［0：购买点击（下单），1：购买成功］
    public static final String OP_GP_BUY_CLICK = "0";
    public static final String OP_GP_BUY_SUCC = "1";
    //其他操作［0：未成功，1：成功 （默认成功） ］
    public static final String OP_OTHER_FAIL = "0";
    public static final String OP_OTHER_SUCC = "1";

    public static final String ENTRANCE_SIDE_BAR = "601";

    //1：普通内购，2：订阅
    public static final String ORDER_TYPE_SUB = "2";
    public static final String ORDER_TYPE_IN_APP = "1";


    public static final String TAB_CREDIT = "CREDIT";
    public static final String TAB_GOOGLE = "GOOGLE";

    public static void uploadStatisticPaySuccess(Context context, String skuId, String entrance, String style) {
        String upload = makeInfo(context, skuId, entrance, null, null, null, OPERATION_P001, null, null, null, null, style);
        upLoadStaticData(upload);
    }

    public static void uploadStatisticPayShow(Context context, String skuId, String entrance, String style) {

        String upload = makeInfo(context, skuId, entrance, null, null, null, OPERATION_F000_VIP, "1", null, null, null, style);
        upLoadStaticData(upload);
    }


    public static void uploadStatisticPayClick(Context context, String skuId, String entrance, String style, String result, String orderId) {
        String upload = makeInfo(context, skuId, entrance, null, orderId, null, OPERATION_GOOGLE_PLAY_BUY, result, null, ORDER_TYPE_SUB, null, style);
        upLoadStaticData(upload);
    }

    /**
     * @param context
     * @param sender         统计对象
     * @param entrance       入口
     * @param tab            tab分 类
     * @param position       位置(重点字段) (付费成功的订单id)
     * @param mark           备 注 (gmail账号)
     * @param operation_code 操作代码
     * @param result_code    操作结果
     * @param orderType      订单类型
     * @return
     */
    private static String makeInfo(Context context, String sender, String entrance,
                                   String tab, String position, String mark, String operation_code, String result_code, String associatedObj, String orderType, String mark1, String mark2) {
        PayConfig config = ConfigManager.INSTANCE.getConfig(PayConfig.class);

        String abTestId = String.valueOf(config == null ? "" : config.getAbtest_id());
        mark1 = abTestId;
        StringBuffer sb1 = new StringBuffer();
        if (!mark2.contains("_")) {
            mark2 = BuyChannelProxy.INSTANCE.isBuyUser() ? "B_" + mark2 : "N_" + mark2;
        }
        sb1.append("59统计--> funId:").append(FUNCTION_ID).append(" 统计对象:").append(sender).append(" 操作码:").append(operation_code).append(" 操作结果:").append(result_code).append(" 入口：").append(entrance)
                .append(" Tab：").append(tab).append(" 位置：").append(position).append(" 关联对象：").append(associatedObj).append(" 备注1：").append(mark1).append(" 备注2：").append(mark2);
        LogUtil.d("Statistic", sb1.toString());
        StringBuilder sb = new StringBuilder(20);
        sb.append(LOG_SEQUENCE).append(PROTOCOL_DIVIDER); // 日志序列
        sb.append(Machine.getAndroidId(context)).append(PROTOCOL_DIVIDER); // Android ID
        sb.append(UtilTool.getBeiJinTime(System.currentTimeMillis())).append(
                PROTOCOL_DIVIDER); // 日志打印时间
        sb.append(FUNCTION_ID).append(PROTOCOL_DIVIDER); // 功能点ID
        sb.append(sender).append(PROTOCOL_DIVIDER); // 统计对象
        sb.append(operation_code).append(PROTOCOL_DIVIDER); // 操作代码
        sb.append(result_code).append(PROTOCOL_DIVIDER); // 操作结果
        sb.append(Machine.getSimCountryIso(context, true)).append(PROTOCOL_DIVIDER); // 国家
        sb.append(GoCommonEnv.INSTANCE.getInnerChannel()).append(PROTOCOL_DIVIDER); // 渠道

        sb.append(VersionController.INSTANCE.getVersionCode()).append(PROTOCOL_DIVIDER); // 版本号


        // 版本名
        sb.append(VersionController.INSTANCE.getVersionName()).append(PROTOCOL_DIVIDER);

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

        //IMEI 传账号ID
        sb.append("").append(PROTOCOL_DIVIDER);


        sb.append(StatisticsManager.getUserId(context)).append(PROTOCOL_DIVIDER); // goid
        if (!TextUtils.isEmpty(associatedObj)) {
            sb.append(associatedObj).append(PROTOCOL_DIVIDER); // 关联对象
        } else {
            sb.append("").append(PROTOCOL_DIVIDER); // 关联对象
        }

        // 当被推的对象为主题时，此字段上传主题的包名；当被推的对象为主程序时，此字段可以为空；
        if (!TextUtils.isEmpty(mark)) { // 备注
            sb.append(mark).append(PROTOCOL_DIVIDER);
        } else {
            sb.append("").append(PROTOCOL_DIVIDER);
        }

        // *订单类型
        if (!TextUtils.isEmpty(orderType)) {
            sb.append(orderType).append(PROTOCOL_DIVIDER);
        } else {
            sb.append(PROTOCOL_DIVIDER);
        }
        //GAID/IDFA
        sb.append(AppUtil.getGoogleAdvertisingId()).append(PROTOCOL_DIVIDER); // GADID

        // 备注1
        if (!TextUtils.isEmpty(mark1)) {
            sb.append(mark1).append(PROTOCOL_DIVIDER);
        } else {
            sb.append("").append(PROTOCOL_DIVIDER);
        }

        // 备注2
        if (!TextUtils.isEmpty(mark2)) {
            sb.append(mark2).append(PROTOCOL_DIVIDER);
        } else {
            sb.append("").append(PROTOCOL_DIVIDER);
        }


        return sb.toString();
    }
}
