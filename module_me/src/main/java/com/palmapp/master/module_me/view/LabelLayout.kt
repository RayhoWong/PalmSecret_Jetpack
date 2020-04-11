package com.palmapp.master.module_me.view

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.view.LabelTextView
import com.palmapp.master.module_me.R
import kotlinx.android.synthetic.main.me_fragment_me.*

class LabelLayout : ConstraintLayout {

    private var padding: Int
    private var maxW: Int
    private var circleRadius: Int
    private var colorArray: Array<LinearGradientColor>
    private val dataList: ArrayList<String> = arrayListOf()


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        padding = resources.getDimensionPixelSize(R.dimen.change_14px)
        maxW = AppUtil.getScreenW(context) / 5
        circleRadius = AppUtil.getScreenW(context) / 3

        colorArray = arrayOf(
            LinearGradientColor(R.color.color_0bc88c, R.color.color_187edc),
            LinearGradientColor(R.color.color_ff56cd, R.color.color_ff3787),
            LinearGradientColor(R.color.color_3dbdff, R.color.color_623dff),
            LinearGradientColor(R.color.color_aa37fc, R.color.color_6c37fc),
            LinearGradientColor(R.color.color_ffbb59, R.color.color_fc774c)
        )
    }

    fun setLabelItem(args: MutableCollection<String>?) {
        LogUtil.d("childCount = " + childCount)
        val childs = arrayListOf<View>()
        for (view in children) {
            if (view.id != R.id.me_gender_image && view.id != R.id.me_flag_no_flag) {
                childs.add(view)
            }
        }

        repeat(childs.size) {
            removeView(childs[it])
        }

        LogUtil.d("remove childCount = " + childCount)
        args?.let {
            val iterator = it.iterator()
            var index = 0
            while (iterator.hasNext() && index < 8) {
                addView(getLabelView(iterator.next(), index * 45))
                addView(getImageView(index * 45))
                index += 1
            }
        }
        LogUtil.d("addviewed childCount = " + childCount)
    }
//    android:padding="5dp"
//
//    android:layout_width="wrap_content"
//
//    android:maxLines="3"
//    android:layout_gravity="center"
//    android:gravity="center"
//    android:maxWidth="@dimen/change_180px"
//    android:layout_height="wrap_content"
//    android:textColor="@android:color/white"
//    android:text="afafafafafafafafafafafa"
//    android:textSize="14.0dip"
//    app:layout_constraintCircleAngle="45"
//    app:layout_constraintCircle="@id/me_circle_image"
//    app:layout_constraintCircleRadius="@dimen/change_300px"
//    app:layout_constraintTop_toTopOf="parent"
//    app:layout_constraintLeft_toLeftOf="parent"

    private fun getLabelView(string: String, angle: Int): LabelTextView {
        val random = (0..colorArray.size - 1).random()
        val label = LabelTextView(context)
        label.text = string
        label.setTextSize(COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.change_33px).toFloat())
        label.setTextColor(Color.WHITE)
        label.setPadding(padding, padding, padding, padding)
        label.maxLines = 3
        label.maxWidth = maxW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            label.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        label.srokeWidth = resources.getDimensionPixelSize(R.dimen.change_3px).toFloat()
        label.radius = -1.0f
        label.startColor = getColor(colorArray[random].startColor())
        label.endColor = getColor(colorArray[random].endColor())

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.circleAngle = angle.toFloat()
        layoutParams.circleConstraint = R.id.me_gender_image
        layoutParams.circleRadius = circleRadius
        layoutParams.topToTop = R.id.me_circle_layout
        layoutParams.leftToLeft = R.id.me_circle_layout
        label.layoutParams = layoutParams
        label.tag = string + angle

        return label
    }

    //    android:layout_width="5dp"
    //    android:layout_height="5dp"
    //    android:src="@mipmap/ic_launcher"
    //    app:layout_constraintCircleAngle="0"
    //    app:layout_constraintCircle="@id/me_circle_image"
    //    app:layout_constraintCircleRadius="@dimen/change_150px"
    //    app:layout_constraintTop_toTopOf="parent"
    //    app:layout_constraintLeft_toLeftOf="parent"
    private fun getImageView(angle: Int): ImageView {
        val imageView = ImageView(context)
        imageView.setImageResource(R.mipmap.me_pic_point)
        val layoutParams = ConstraintLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.circleAngle = angle.toFloat()
        layoutParams.circleConstraint = R.id.me_gender_image
        layoutParams.circleRadius = circleRadius / 2
        layoutParams.topToTop = R.id.me_circle_layout
        layoutParams.leftToLeft = R.id.me_circle_layout
        imageView.layoutParams = layoutParams
        imageView.tag = "circle_" + angle
        return imageView
    }

    //    <ImageView
//    android:id="@+id/me_gender_image"
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:src="@mipmap/me_pic_boy"
//    app:layout_constraintBottom_toBottomOf="parent"
//    app:layout_constraintTop_toTopOf="parent"
//    app:layout_constraintLeft_toLeftOf="parent"
//    app:layout_constraintRight_toRightOf="parent"
//
//    />
//    private fun getGenderView(): ImageView {
//        val imageView = ImageView(context)
//        imageView.id = R.id.me_gender_image
//        val layoutParams = ConstraintLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//        layoutParams.circleConstraint = R.id.me_gender_image
//        layoutParams.bottomToBottom = LayoutParams.PARENT_ID
//        layoutParams.topToTop = LayoutParams.PARENT_ID
//        layoutParams.leftToLeft = LayoutParams.PARENT_ID
//        layoutParams.rightToRight = LayoutParams.PARENT_ID
//        imageView.layoutParams = layoutParams
//        return imageView
//
//    }


    private fun getColor(color: Int): Int {
        return resources.getColor(color)
    }

    class LinearGradientColor constructor(startColor: Int, endColor: Int) {
        private val startColor: Int
        private val endColor: Int

        init {
            this.startColor = startColor
            this.endColor = endColor
        }

        fun startColor(): Int {
            return startColor
        }

        fun endColor(): Int {
            return endColor
        }
    }

}