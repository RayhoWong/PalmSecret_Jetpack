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
public class CommonDialog extends Dialog {

    private View layout;

    //标题
    protected TextView mTvMessage;
    //否定按钮
    protected TextView mTvNegative;
    //确定按钮
    protected TextView mTvPositive;

    private String title;//标题
    private String positive, negative;//按钮名称

    private boolean isPositiveVisible = true;
    private boolean isNegativeVisible = true;

    protected OnClickBottomListener onClickBottomListener;


    public CommonDialog(@NonNull Context context) {
        super(context, R.style.CommonDialog);
        layout = getLayout();
    }

    protected View getLayout() {
        return LayoutInflater.from(getContext()).inflate(R.layout.common_dialog, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        //设置dialog的宽高
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) getContext().getResources().getDimension(R.dimen.change_840px);
        window.setAttributes(lp);

        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }

    protected void initView() {
        mTvMessage = layout.findViewById(R.id.tv_message);
        mTvNegative = layout.findViewById(R.id.tv_negative);
        mTvPositive = layout.findViewById(R.id.tv_positive);

        if (!TextUtils.isEmpty(title)) {
            mTvMessage.setText(title);
        }
        if (!TextUtils.isEmpty(positive)) {
            mTvPositive.setText(positive);
        }
        if (!TextUtils.isEmpty(negative)) {
            mTvNegative.setText(negative);
        }


        if (isPositiveVisible) {
            mTvPositive.setVisibility(View.VISIBLE);
        } else {
            mTvPositive.setVisibility(View.INVISIBLE);
        }


        if (isNegativeVisible) {
            mTvNegative.setVisibility(View.VISIBLE);
        } else {
            mTvNegative.setVisibility(View.INVISIBLE);
        }

    }

    protected void initEvent() {
        mTvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onNegativeClick();
                }
                dismiss();
            }
        });

        mTvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick();
                }
                dismiss();
            }
        });
    }

    public CommonDialog setNegativeVisibilty(boolean visible) {
        isNegativeVisible = visible;
        return this;
    }

    public CommonDialog setPositiveVisibilty(boolean visible) {
        isPositiveVisible = visible;
        return this;
    }


    public String getTitle() {
        return title;
    }

    public CommonDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPositive() {
        return positive;
    }

    public CommonDialog setPositive(String positive) {
        this.positive = positive;
        return this;
    }

    public String getNegative() {
        return negative;
    }

    public CommonDialog setNegative(String negative) {
        this.negative = negative;
        return this;
    }

    public CommonDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    public interface OnClickBottomListener {
        /**
         * 点击确定按钮事件
         */
        void onPositiveClick();

        void onNegativeClick();
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
