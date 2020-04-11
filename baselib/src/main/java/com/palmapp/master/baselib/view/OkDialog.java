package com.palmapp.master.baselib.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.palmapp.master.baselib.R;

/**
 * Created by huangweihao on 2019/8/16.
 */
public class OkDialog extends CommonDialog {


    public OkDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTvNegative.setVisibility(View.GONE);
    }
}
