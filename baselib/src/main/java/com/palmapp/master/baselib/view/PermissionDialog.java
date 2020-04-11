package com.palmapp.master.baselib.view;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.alibaba.android.arouter.launcher.ARouter;
import com.palmapp.master.baselib.R;
import com.palmapp.master.baselib.constants.RouterConstants;
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic;

/**
 * Created by huangweihao on 2019/8/16.
 */
public class PermissionDialog extends CommonDialog {


    public PermissionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.permission_f000);
    }

    @Override
    protected View getLayout() {
        return LayoutInflater.from(getContext()).inflate(R.layout.permission_dialog, null);
    }

    @Override
    protected void initView() {
        super.initView();
        TextView tv = findViewById(R.id.tv_policy);
        tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.permission_a000, "", "3");
                ARouter.getInstance().build(RouterConstants.ACTIVITY_MEPolicyActivity).withString("url", "http://d2prafqgniatg5.cloudfront.net/palmhoroscope/palmsecret_p2.html").navigation();
            }
        });
    }

    @Override
    protected void initEvent() {
        mTvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.permission_a000, "", "2");
                if (onClickBottomListener != null) {
                    onClickBottomListener.onNegativeClick();
                }
                dismiss();
            }
        });

        mTvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.permission_a000, "", "1");
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick();
                }
                dismiss();
            }
        });
    }
}
