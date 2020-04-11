package com.palmapp.master.baselib.deamon;

import android.content.Context;
import com.cs.bd.daemon.DaemonClient;
import com.cs.bd.daemon.DaemonConfigurations;
import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.constants.AppConstants;
import com.palmapp.master.baselib.manager.DebugProxy;

/**
 * 守护进程代理
 * Created by zhangliang on 18-5-19.
 */

public class DaemonProxy {
    /**
     * 初始化
     * 必须要在{@link android.app.Application attachBaseContext(Context)}方法中执行
     *
     * @param context
     */
    public static void init(Context context) {
        if (DebugProxy.INSTANCE.isDebug()) {
            // 设置测试模式，打开LOG
            DaemonClient.getInstance().setDebugMode();
        }
        /* 初始化DaemonClient */
        //构建被守护进程配置信息
        DaemonConfigurations.DaemonConfiguration configuration1 =
                new DaemonConfigurations.DaemonConfiguration(
                        GoCommonEnv.INSTANCE.getApplicationId(),
                        Service1.class.getCanonicalName(),
                        Receiver1.class.getCanonicalName());
        //构建辅助进程配置信息
        DaemonConfigurations.DaemonConfiguration configuration2 =
                new DaemonConfigurations.DaemonConfiguration(
                        GoCommonEnv.INSTANCE.getApplicationId()+":assistant",
                        Service2.class.getCanonicalName(),
                        Receiver2.class.getCanonicalName());
        DaemonConfigurations configs = new DaemonConfigurations(configuration1, configuration2, null);
        //添加其它常驻进程
        //        configs.addPersistentService(Service3.class.getCanonicalName());
        //        configs.addPersistentService(Service4.class.getCanonicalName());
        //开启守护效果统计
//        configs.setStatisticsDaemonEffect(true);
        //        //设置唤醒常驻服务轮询时长
        //        configs.setDaemonWatchInterval(60);
        //        //设置唤醒常驻服务轮询时长(android版本21以上)
        //        configs.setDaemonWatchIntervalAboveAPI21(60);
        //        //设置多长时间后，第一次唤醒其它常驻进程
        //        configs.setDaemonOtherPersistentProcessTrigger(10);
        //        //设置唤醒其它常驻进程的监控时间间隔
        //        configs.setDaemonOtherPersistentProcessInterval(60);
        //设置唤醒常驻服务轮询时长
        //        configs.setDaemonWatchInterval(3);
        //监听，无关紧要
        //        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        DaemonClient.getInstance().init(configs);
        DaemonClient.getInstance().onAttachBaseContext(context);
    }

    /**
     * 设置守护进程开关
     *
     * @param context
     * @param isSwitchOn false：若守护已启动则关停；true：要下次{@link android.app.Application attachBaseContext(Context)}被调用才生效
     */
    public static void setDaemonSwitch(Context context, boolean isSwitchOn) {
        if (context == null) {
            return;
        }
        DaemonClient.getInstance().setDaemonPermiiting(context, isSwitchOn);
    }
}
