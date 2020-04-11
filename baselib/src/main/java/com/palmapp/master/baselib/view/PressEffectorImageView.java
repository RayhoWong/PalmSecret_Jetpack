package com.palmapp.master.baselib.view;

import android.content.Context;
import android.util.AttributeSet;
import com.palmapp.master.baselib.R;

/**
 * Created by gejiashu on 18-2-2.
 */

public class PressEffectorImageView extends PalmImageView {
    public PressEffectorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getMPressColor() == 0) {
            setMPressColor(context.getResources().getColor(R.color._4c000000));
        }
    }
}
