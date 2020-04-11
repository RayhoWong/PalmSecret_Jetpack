package com.palmapp.master.baselib.bean.palm

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/9
 */
const val HANDLEINFO_LOVE = "LOVE"
const val HANDLEINFO_WISDOM = "WISDOM"
const val HANDLEINFO_LIFE = "LIFE"

class HandleInfo() : Parcelable {
    @SerializedName("hand_line")
    var hand_line: String? = null
    @SerializedName("score")
    var score: Float = 0f
    @SerializedName("contents")
    var contents: List<String>? = null

    constructor(parcel: Parcel) : this() {
        hand_line = parcel.readString()
        score = parcel.readValue(Float::class.java.classLoader) as Float
        contents = parcel.createStringArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hand_line)
        parcel.writeValue(score)
        parcel.writeStringList(contents)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HandleInfo> {
        override fun createFromParcel(parcel: Parcel): HandleInfo {
            return HandleInfo(parcel)
        }

        override fun newArray(size: Int): Array<HandleInfo?> {
            return arrayOfNulls(size)
        }
    }

    fun getTotalScore(): Float = score * 100f / 5f
}