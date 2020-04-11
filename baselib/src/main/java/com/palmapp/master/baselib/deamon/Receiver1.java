package com.palmapp.master.baselib.deamon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.cs.bd.daemon.DaemonClient;

/**
 * DO NOT do anything in this Receiver!<br/>
 *
 * Created by matt
 */
public class Receiver1 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //统计守护效果
        DaemonClient.getInstance().statisticsDaemonEffect(this, intent);
    }
}
