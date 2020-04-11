
package com.palmapp.master.baselib.statistics;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import com.cs.statistic.StatisticsManager;
import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.constants.AppConstants;
import com.palmapp.master.baselib.constants.PreConstants;
import com.palmapp.master.baselib.manager.*;
import com.palmapp.master.baselib.manager.config.ConfigManager;
import com.palmapp.master.baselib.proxy.BillingServiceProxy;
import com.palmapp.master.baselib.proxy.GoogleServiceProxy;
import com.palmapp.master.baselib.utils.LogUtil;
import com.palmapp.master.baselib.utils.MachineUtil;
import com.palmapp.master.baselib.utils.VersionController;

import java.util.HashMap;

/**
 * @author yangguanxiang
 */
public class ScheduleTaskHandler extends BroadcastReceiver {
    private final static String TAG = ScheduleTaskHandler.class.getSimpleName();
    private Context mContext;
    private AlarmManager mAlarmManager;
    private boolean mTasksHasStarted;
    private final static long UPDATE_INTERVAL = 8 * 60 * 60 * 1000; // 每隔
    private final static long FIRST_RUN_UPLOAD_TIME = 15 * 1000; // 启动后15上传日志
    public static final int PRODUCT_ID = AppConstants.APP_CID2;


    public ScheduleTaskHandler(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_AUTO_CHECK_UPDATE);
        mContext.registerReceiver(this, filter);
    }

    /**
     * <br>功能简述:
     * 添加方法时，记得注释第一次请求时间
     * <br>功能详细描述:
     * <br>注意:
     */
    public void startAllTasks() {
        if (mTasksHasStarted) {
            return;
        }

        mTasksHasStarted = true;
        ThreadExecutorProxy.INSTANCE.runOnAsyncThread(new Runnable() {

            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //基础info统计 协议19 15s
                startBasicInfoStaticTask();
            }

        }, 0);
    }

    /**
     * <br>
     * 功能简述:协议19 <br>
     * 功能详细描述: 新用户会在15秒上传，然后三分钟，然后八小时，老用户一直都会是8小时<br>
     * 注意:
     */
    private void startBasicInfoStaticTask() {
        LogUtil.d(TAG, "startBasicInfoStaticTask ");
        long now = System.currentTimeMillis();
        long nextCheckTime = UPDATE_INTERVAL; // 下一次上传间隔时间
        long lastCheckUpdate = getLastCheckedTime(PreConstants.Schedule.KEY_CHECK_TIME); // 上一次的检查时间
        if (lastCheckUpdate == 0L) {
            setLastCheckedTime(PreConstants.Schedule.KEY_CHECK_TIME, 1L);
            nextCheckTime = FIRST_RUN_UPLOAD_TIME;
            LogUtil.d(TAG, "lastCheckUpdate == 0L next CheckTime ==" + nextCheckTime);
        } else if (lastCheckUpdate == 1L) {
            // 三分钟
            // 保存本次检查的时长
            upLoadBasicInfo();
            setLastCheckedTime(PreConstants.Schedule.KEY_CHECK_TIME, now);
            LogUtil.d(TAG, "lastCheckUpdate == 1L next CheckTime ==" + nextCheckTime);
        } else if ((now - lastCheckUpdate >= UPDATE_INTERVAL)
                || (now - lastCheckUpdate <= 0L)) {
            BillingServiceProxy.INSTANCE.queryVip();
            ConfigManager.INSTANCE.requestAbConfig();
            // 八小时
            upLoadBasicInfo();
            // 保存本次检查的时长
            setLastCheckedTime(PreConstants.Schedule.KEY_CHECK_TIME, now);
            LogUtil.d(TAG, "lastCheckUpdate == 3 next CheckTime ==" + nextCheckTime);
        } else {
            // 动态调整下一次的间隔时间
            nextCheckTime = UPDATE_INTERVAL - (now - lastCheckUpdate);
            LogUtil.d(TAG, "lastCheckUpdate == 4 next CheckTime ==" + nextCheckTime);
        }

        final long tiggertTime = System.currentTimeMillis() + nextCheckTime;
        Intent updateIntent = new Intent(AppConstants.ACTION_AUTO_CHECK_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, pendingIntent);
    }

    private void upLoadBasicInfo() {
        ThreadExecutorProxy.INSTANCE.runOnAsyncThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String uid = GoCommonEnv.INSTANCE.getInnerChannel();
                String lan = MachineUtil.getLanguage(mContext);

                boolean isnew = VersionController.INSTANCE.isNewUser() && GoPrefManager.INSTANCE.getDefault().getBoolean(PreConstants.Schedule.KEY_IS_FIRST_RUN_NEW_UESR, true);

                if (isnew || VersionController.INSTANCE.isNewUser()) {
                    GoPrefManager.INSTANCE.getDefault().putBoolean(PreConstants.Schedule.KEY_IS_FIRST_RUN_NEW_UESR, false).commit();
                }
                String user = AbTestUserManager.INSTANCE.getTestUser();
                LogUtil.d(TAG, "定时上传19协议,是否为新用户 == " + isnew + " 上传方案标识：" + user);
                BaseSeq19Statistic.uploadBaseStatistic(
                        uid,
                        false,
                        false,
                        user,
                        isnew,
                        GoogleServiceProxy.INSTANCE.getGoogleAdvertisingId() + "||" + lan);
            }
        }, 0);
    }


    private long getLastCheckedTime(String key) {
        GoPrefProxy pref = GoPrefManager.INSTANCE.getDefault();
        long lastCheckedTime = 0L;
        if (pref != null) {
            lastCheckedTime = pref.getLong(key, 0L);
        }
        return lastCheckedTime;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
    }


    private void setLastCheckedTime(String key, long checkedTime) {
        GoPrefProxy pref = GoPrefManager.INSTANCE.getDefault();
        if (pref != null) {
            pref.putLong(key, checkedTime);
            pref.commit();
        }
    }


}
