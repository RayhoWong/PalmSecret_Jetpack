package com.palmapp.master.baselib.deamon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * DO NOT do anything in this Service!<br/>
 *
 * Created by matt
 */
public class Service2 extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("matt", "Service2::onCreate-->");

//        //测试代码，仅用于测试服务是否活着
//        CustomAlarmManager.getInstance(this).getAlarm("csdaemon").alarmRepeat(2, 1000, 3000, true, new CustomAlarm.OnAlarmListener() {
//            @Override
//            public void onAlarm(int i) {
//                Log.v("matt", "process 2:我还活着！");
//            }
//        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("matt", "Service2::onStartCommand-->");

        /**
         * 返回{@link Service#START_STICKY}，kill命令杀进程后可快速启动
         */
        return START_STICKY;
    }

}
