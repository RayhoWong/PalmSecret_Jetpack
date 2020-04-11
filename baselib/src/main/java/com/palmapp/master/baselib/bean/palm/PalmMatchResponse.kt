package com.palmapp.master.baselib.bean.palm

import java.io.Serializable

/**
 *
 * @author :     xiemingrui
 * @since :      2019/9/25
 */

data class PalmMatchResponse(
		var id: Int,// 303
		var sign1: Int,// 1
		var sign2: Int,// 2
		var content: PalmMatchResponse_Content,
		var type: Int,// 1
		var score: Int,// 65
		var lang: String,// ZH
		var intimacy: Int,// 61
		var suitable: Int,// 74
		var marry_age: String,// 22, 25, 28
		var children_count: String// 2
):Serializable

data class PalmMatchResponse_Content(
		var category: Int,// 31
		var article: List<PalmMatchResponse_Article>
):Serializable

data class PalmMatchResponse_Article(
		var type: Int,// 3
		var text: String// 你的熱情可以幫助振作有時懶惰的伴侶。你傾向於比你的伴侶更加焦躁不安，而伴侶是由愛所統治的。您可能需要在許多活動中起帶頭作用。
):Serializable