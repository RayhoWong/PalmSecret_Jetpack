package com.palmapp.master.baselib.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class S3ImageInfo: Serializable {

    var key: String? = null
    var etag: String? = null
    @SerializedName("image_width")
    var imageWidth: Int = 0
    @SerializedName("image_height")
    var imageHeight: Int = 0
}
