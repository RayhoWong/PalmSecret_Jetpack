package com.palmapp.master.module_face.activity.animal

import android.graphics.Bitmap
import android.graphics.Point
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import java.io.Serializable
import java.util.HashMap

/**
 *
 * @author :     xiemingrui
 * @since :      2020/4/7
 */
class AnimalImageInfo(val faces: List<FirebaseVisionFace>?, val src: Bitmap?) : Serializable {
    companion object {
        const val LEFT_EYEBROW_TOP = "LEFT_EYEBROW_TOP"
        const val RIGHT_EYEBROW_TOP = "RIGHT_EYEBROW_TOP"
        const val LEFT_EYE = "LEFT_EYE"
        const val RIGHT_EYE = "RIGHT_EYE"
        const val NOSE_BRIDGE = "NOSE_BRIDGE"
        const val NOSE_BOTTOM = "NOSE_BOTTOM"
        const val UPPER_LIP_BOTTOM = "UPPER_LIP_BOTTOM"
        const val FACE_OVAL = "FACE_OVAL"

        val EYE_BROW_FILTER_INDEXES = intArrayOf(1)
        val EYE_FILTER_INDEXES = intArrayOf(3, 5, 7, 9, 12, 14)
        val FACE_FILTER_INDEXES = intArrayOf(3, 5, 8, 13, 15, 17, 19, 21, 23, 28, 31)
        val KEY_ARRAY = arrayOf(
            LEFT_EYEBROW_TOP,
            RIGHT_EYEBROW_TOP,
            LEFT_EYE,
            RIGHT_EYE,
            NOSE_BRIDGE,
            NOSE_BOTTOM,
            UPPER_LIP_BOTTOM,
            FACE_OVAL)
    }
}