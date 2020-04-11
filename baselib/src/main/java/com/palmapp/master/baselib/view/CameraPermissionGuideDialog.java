package com.palmapp.master.baselib.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.palmapp.master.baselib.R;
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic;

/**
 * Created by huangweihao on 2019/8/16.
 */
public class CameraPermissionGuideDialog extends CommonDialog {


    public CameraPermissionGuideDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.camera_f000);
    }

    @Override
    protected View getLayout() {
        return LayoutInflater.from(getContext()).inflate(R.layout.permission_camera_guide_dialog, null);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initEvent() {
        mTvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.camera_a000, "", "2");
                if (onClickBottomListener != null) {
                    onClickBottomListener.onNegativeClick();
                }
                dismiss();
            }
        });

        mTvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.camera_a000, "", "1");
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick();
                }
                dismiss();
            }
        });
    }
}
