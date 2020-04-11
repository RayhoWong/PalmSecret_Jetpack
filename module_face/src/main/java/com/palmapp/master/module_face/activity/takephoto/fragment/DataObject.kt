package com.palmapp.master.module_face.activity.takephoto.fragment

import android.graphics.Bitmap
import android.net.Uri

class DataObject {

    constructor(bitmaps:MutableList<Bitmap>?,bitmap: Bitmap) {
        this.bitmaps = bitmaps
        this.bitmap = bitmap
    }
    var bitmaps:MutableList<Bitmap>? = null
    var bitmap: Bitmap? = null

    interface FragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri, data:DataObject?)
    }
}