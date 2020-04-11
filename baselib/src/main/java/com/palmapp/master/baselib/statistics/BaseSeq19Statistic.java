package com.palmapp.master.baselib.statistics;

import com.palmapp.master.baselib.constants.AppConstants;

/**
 * Created by zhangliang on 18-5-11.
 */

public class BaseSeq19Statistic extends AbsBaseStatistic {


    public static void uploadBaseStatistic(String uid, boolean b, boolean b1, String user, boolean isnew, String s1) {
        upLoadBasicInfoStaticData(String.valueOf(AppConstants.APP_CID2), uid, b, b1, user, isnew, s1);

    }
}
