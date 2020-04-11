package com.palmapp.master.baselib.bean.quiz

import android.os.Parcel
import android.os.Parcelable
import com.palmapp.master.baselib.bean.StatusResult
import java.io.Serializable

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/15
 */

data class AnswerResponse(
    var status_result: StatusResult,
    var quzi_answer: Quzi_answer
)

data class Quzi_answer(
    var answer_id: Int,// 145
    var title: String,// The dependence on LOVE is 75%!
    var description: String,// The dependence on LOVE is 75%! You are blind in love. You often put the other person in the first place in your mind. If lover asks you to go out, you will disregard everything immediately and run to him! But this kind of enthusiasm comes and goes quickly.
    var nature: String,// Love Master
    var nature_id: String// 3
) : Serializable