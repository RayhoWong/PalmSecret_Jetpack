package com.palmapp.master.module_transform.util

import android.graphics.Bitmap
import android.net.Uri

class DataObject {

    constructor(bitmaps:MutableList<Bitmap>?) {
        this.bitmaps = bitmaps
    }
    var bitmaps:MutableList<Bitmap>? = null

    interface FragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri, data:DataObject?)
    }
}