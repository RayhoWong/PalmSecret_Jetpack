package com.palmapp.master.baselib.bean.cnt

import com.palmapp.master.baselib.bean.StatusResult

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/20
 */

data class MatchingResponse(
    var status_result: StatusResult,
    var percent: Int,// 90
    var content: Content,
    var best_match1_list: List<Int>,
    var best_match2_list: List<Int>
)

data class MatchingResponse_Content(
    var category: Int,// 0
    var article: List<MatchingResponse_Article>
)

data class MatchingResponse_Article(
    var type: Int,// 3
    var text: String// This is perhaps the most passionate and fiery match in the zodiac. They will attract each other together, directly express their love and immediately enter the stage of love. Aries with Aries is a wonderful match, and a fabulous combination.This is what happens when you put two born leaders together that have the fire in their bellies to succeed in all areas of life. After getting along for a long time they will gradually be unable to tolerate each other's shortcomings.
)