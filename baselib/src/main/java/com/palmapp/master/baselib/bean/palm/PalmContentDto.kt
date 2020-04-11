package com.palmapp.master.baselib.bean.palm

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/17
 */
class PalmContentDto() : Parcelable {
    @SerializedName("code")
    var code: String = ""
    @SerializedName("content")
    var content: String = ""

    constructor(source: Parcel) : this(
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PalmContentDto> = object : Parcelable.Creator<PalmContentDto> {
            override fun createFromParcel(source: Parcel): PalmContentDto = PalmContentDto(source)
            override fun newArray(size: Int): Array<PalmContentDto?> = arrayOfNulls(size)
        }
    }
}