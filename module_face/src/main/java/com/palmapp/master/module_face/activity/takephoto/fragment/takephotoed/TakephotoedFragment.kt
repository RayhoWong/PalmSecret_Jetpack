package com.palmapp.master.module_face.activity.takephoto.fragment.takephotoed;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.palmapp.master.baselib.BaseFragment
import com.palmapp.master.baselib.event.FaceAnimEvent
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.ui.FaceAnimActivityInject
import com.palmapp.master.module_face.R
import com.palmapp.master.module_face.activity.takephoto.fragment.DataObject
import com.palmapp.master.module_face.activity.takephoto.fragment.DataObject.FragmentInteractionListener
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import com.palmapp.master.module_imageloader.glide.ImageLoadingListener
import kotlinx.android.synthetic.main.face_fragment_takephotoed.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by zhangliang on 15-8-19.
 * 拍照后，人脸识别页面， 返回按钮统一返回到拍照页
 */
class TakephotoedFragment : BaseFragment<TakephotoedView, TakephotoedPresenter>(), TakephotoedView,
    View.OnClickListener {
    override fun enableClick() {
        face_ok.isClickable = true
    }

    private val ARG_PARAM1: String = "ARG_PARAM1"
    private val ARG_PARAM2: String = "ARG_PARAM2"
    private var param1: String? = null
    private var param2: String? = null
    private var listener: FragmentInteractionListener? = null

    private var contourPoints: FloatArray? = null
    private var bitmap: Bitmap? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFaceEvent(event: FaceAnimEvent) {
        if(event.isOk) {
            bitmap?.let { mPresenter?.faceDetectToOld(activity?.application, contourPoints, it) }
        } else {
            activity?.finish()
        }
    }

    override fun onDestroy() {
        contourPoints = null
        bitmap?.recycle()
        bitmap = null
        super.onDestroy()
    }


    override fun createPresenter(): TakephotoedPresenter {
        return TakephotoedPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.face_fragment_takephotoed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.face_scan_pro,
            "",
            "3",""
        )
        super.onViewCreated(view, savedInstanceState)
        face_cacel?.setOnClickListener(this)
        face_ok?.setOnClickListener(this)
        param1?.let {
            ImageLoaderUtils.displayImageNoCache(context, param1, object : ImageLoadingListener() {
                override fun onLoadingComplete(bitmap: Bitmap?) {
                    bitmap?.let {
                        if (face_image != null) {
                            face_image.setSource(it)
                        }
                    }
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.face_cacel -> listener?.onFragmentInteraction(
                Uri.Builder().appendPath("goto.TakephotoActivity").build(),
                null
            )
            R.id.face_ok -> {
                face_ok.isClickable = false
                val bitmap = face_image.getCropBitmap(false)
                if (bitmap != null) {
                    this.bitmap = bitmap
                    FaceAnimActivityInject.injectShowBitmap(activity,layout_face_anim as ViewGroup, bitmap, Runnable {
                        mPresenter?.faceDetectToContourPoint(bitmap)
                    })

                }
            }
        }
    }

    override fun startScanAnima() {
        face_takephoto_button_layout.visibility = View.INVISIBLE
    }

    override fun stopScanAnima() {
        face_takephoto_button_layout.visibility = View.VISIBLE
    }



    override fun fragmentInteraction(uri: Uri, data: DataObject?) {
        listener?.onFragmentInteraction(uri, data)
    }

    override fun showToast(resId: Int) {
        Toast.makeText(context, getString(resId), Toast.LENGTH_SHORT).show()
    }

    override fun faceDetectSuccess(contourPoints: MutableList<MutableList<PointF>>?, bitmap: Bitmap) {
        view?.let {
            this.contourPoints = FaceAnimActivityInject.toFloat(contourPoints)
            FaceAnimActivityInject.injectSuccess(activity, layout_face_anim as ViewGroup,bitmap, contourPoints)
        }
    }

    override fun faceDetectFail() {
        view?.let {
            FaceAnimActivityInject.injectFail(layout_face_anim as ViewGroup)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDestroyView() {
        if(layout_face_anim != null) {
            FaceAnimActivityInject.injectDetach(activity, layout_face_anim as ViewGroup)
        }
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
//        face_image.clearSource()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TakephotoedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}