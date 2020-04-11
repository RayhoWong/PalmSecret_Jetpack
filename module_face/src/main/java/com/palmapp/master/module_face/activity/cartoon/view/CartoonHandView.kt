package com.palmapp.master.module_face.activity.cartoon.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.palmapp.master.baselib.view.AnimationsContainer
import com.palmapp.master.module_face.R


/**
 *    author : liwenjun
 *    date   : 2020/4/9 17:12
 *    desc   :
 */
class CartoonHandView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private var fallingView: FallingView
    private var cartoonView: CartoonView
    private var faceIv: ImageView
    private var animation: AnimationsContainer.FramesSequenceAnimation? = null
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.face_layout_cartoon_anim_scan, null)
        addView(view)
        cartoonView = findViewById(R.id.cartoon_view)
        faceIv = view.findViewById(R.id.iv)
        //初始化一个雪花样式的fallObject
        fallingView = findViewById<View>(R.id.falling_view) as FallingView
        val builder = FallObject.Builder(resources.getDrawable(R.drawable.face_white_circle))
        val fallObject = builder
            .setSpeed(7, true)
            .setSize(24, 24, true)
            .setWind(5, true, true)
            .build()
        fallingView.addFallObject(fallObject, 100) //添加50个下落物体对象
    }

    fun getCartoonView() : CartoonView {
        return cartoonView
    }

    fun startAnim(runnable: Runnable) {
        if(animation == null) {
            val anims = IntArray(16)
            for (i in 5..20) {
                // 获取Drawable文件夹下的图片文件
                val id = resources.getIdentifier("magic_000${String.format("%02d", i)}", "mipmap", this.context.packageName)
                anims[i - 5] = id
            }
            animation = AnimationsContainer.getInstance().createAnim(faceIv, 40, anims)
            animation?.registerCallback {
                fallingView.visibility = View.VISIBLE
                runnable.run()
                fallingView.startAnim(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        fallingView.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })
                faceIv.setImageResource(anims[0])
                cartoonView.showAnimation()
            }
            animation?.start()
        } else {
            stopAnim()
            runnable.run()
            cartoonView.postInvalidate()
        }
    }

    fun stopAnim() {
        animation?.stop()
        cartoonView.stopAnimation()
        fallingView?.stopAnim()
    }
}