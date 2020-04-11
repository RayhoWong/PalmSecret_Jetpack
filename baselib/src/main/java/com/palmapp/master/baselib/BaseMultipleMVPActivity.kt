//package com.palmapp.master.baselib
//
//import android.content.Context
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.palmapp.master.baselib.utils.LogUtil
//
//
//abstract class BaseMultipleMVPActivity : BaseActivity(), IView {
//    protected var mActivity: AppCompatActivity? = null
//    private val mPresenters = arrayListOf<IMultipleBasePresenter>()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        addPresenters()
//        mActivity = this
//        mPresenters.forEach { it.onAttach(this, mActivity!!) }
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mPresenters.forEach { it.onDetach() }
//    }
//
//    override fun finish() {
//        mPresenters.forEach {
//            if (it.onFinishEvent()) {
//                LogUtil.d("BaseMultipleMVPActivity", "${it}拦截了返回事件")
//                return
//            }
//        }
//        super.finish()
//    }
//
//    abstract fun addPresenters()
//
//    protected fun <V : IView> addToPresenter(presenter: IMultipleBasePresenter<V>) {
//        mPresenters.add(presenter)
//    }
//
//    override fun getContext(): Context? {
//        return this
//    }
//}