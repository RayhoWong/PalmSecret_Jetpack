package com.palmapp.master.baselib.bean.cnt

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse
import com.palmapp.master.baselib.bean.StatusResult

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/13
 */

data class ForecastResponse(
    var status_result: StatusResult,
    var forecast_infos: List<Forecast_info>
)

data class Forecast_info(
    var id: Int,// 0
    var constellation_id: Int,// 1
    var forecast_type: String,// day
    var content: Content,
    var date: Long,// 1565691502940
    var rating: Rating
)

data class Rating(val love: Int, val health: Int, val wealth: Int, val career: Int)

data class Content(
    var category: Int,// 1
    var article: List<Article>
)

data class Article(
    var type: Int,// 3
    var text: String// Your solutions are revolutionary now. The energy is open and pathways for action appear as you assume leadership with the spirit of excellence. However balance is necessary when dealing with others; be steadfast in your approach while remaining non-resistant. Dissolve the self-defeating patterns which are masks for deeper insecurities. You can step boldly forward as an influencer without compromising your ideals. To think for the future is to embody love; allow your guilt to fall away. What you innovate today is what the world will clamor for tomorrow.
)