package com.palmapp.master.baselib.deamon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.cs.bd.daemon.DaemonClient;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 *
 * Created by matt
 */
public class Service1 extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("matt", "Service1::onCreate-->");

//        //测试代码，仅用于测试服务是否活着
//        CustomAlarmManager.getInstance(this).getAlarm("csdaemon").alarmRepeat(1, 1000, 3000, true, new CustomAlarm.OnAlarmListener() {
//            @Override
//            public void onAlarm(int i) {
//                Log.v("matt", "process 1:我还活着！");
//            }
//        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("matt", "Service1::onStartCommand-->");

        //统计守护效果
        DaemonClient.getInstance().statisticsDaemonEffect(this, intent);

        /**
         * 返回{@link Service#START_STICKY}，kill命令杀进程后可快速启动
         */
        return START_STICKY;
    }

}
