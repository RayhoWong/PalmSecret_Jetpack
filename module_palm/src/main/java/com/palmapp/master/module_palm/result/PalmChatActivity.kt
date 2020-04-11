package com.palmapp.master.module_palm.result

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_palm.PalmLeadingMarginSpan
import com.palmapp.master.module_palm.R
import com.palmapp.master.module_palm.scan.PalmResultCache
import kotlinx.android.synthetic.main.palm_activity_chat.*
import kotlin.math.roundToInt

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/18
 */
@Route(path = RouterConstants.ACTIVITY_PALM_CHAT)
class PalmChatActivity : BaseMultipleMVPActivity(), PalmChatView {
    val textAppearanceSpan = TextAppearanceSpan(GoCommonEnv.getApplication(), R.style.palm_style_chat_title)
    @JvmField
    @Autowired(name = "handleinfos")
    var handleinfos: PalmResultCache? = null

    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""

    private val list = ArrayList<PalmDataHolder>()
    private val adapter = MyAdapter()

    private val palmChatPresenter = PalmChatPresenter()
    override fun addPresenters() {
        addToPresenter(palmChatPresenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.palm_scan_pro,
            if (newPlan == "1") "1" else "2",
            "4", ""
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_activity_chat)
        palmChatPresenter.start(handleinfos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.palm_daily_title)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener {
            onBackPressed()
        }
    }

    private inner class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == 0) {
                return NormalHolder(LayoutInflater.from(this@PalmChatActivity).inflate(R.layout.palm_item_chat_result_normal, parent, false))
            } else {
                return ImageHolder(LayoutInflater.from(this@PalmChatActivity).inflate(R.layout.palm_item_chat_result_image, parent, false))
            }
        }

        override fun getItemViewType(position: Int): Int {
            return list.get(position).type
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = list.get(position)
            if (holder is NormalHolder) {
                if (data.content.indexOf("###") == -1) {
                    holder.content.text = data.content
                } else {
                    val title = handleinfos?.palm_style
                    val temp = data.content.split("###")
                    val span = SpannableStringBuilder()
                    span.appendWithSpan(title, CharacterStyle.wrap(textAppearanceSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n")
                    span.appendWithSpan(temp[1], CharacterStyle.wrap(textAppearanceSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n")
                    span.append(temp[2]).append("\n")
                    span.appendWithSpan(temp[3], CharacterStyle.wrap(textAppearanceSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n")
                    span.append(temp[4]).append("\n")
                    span.appendWithSpan(temp[5], CharacterStyle.wrap(textAppearanceSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n")
                    span.append(temp[6])
                    holder.content.text = span
                }
                holder.expert.setImageResource(handleinfos?.expertIcon ?: R.mipmap.palm_portrait_1)
            } else if (holder is ImageHolder) {
                holder.title.text = data.title
                holder.image.setImageResource(data.id)
                var lines = 0
                val finalHeight = resources.getDimensionPixelSize(R.dimen.change_186px)
                lines = (finalHeight / holder.content.lineHeight.toFloat()).roundToInt()
                var content = data.content
                content = content.replace("\\n", "")
                content = content.replace("\n", "")

                val span = PalmLeadingMarginSpan(lines, finalHeight + resources.getDimensionPixelOffset(R.dimen.change_18px))
                val spannableString = SpannableString(content)
                spannableString.setSpan(span, 0, content.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                holder.content.text = spannableString
                holder.expert.setImageResource(handleinfos?.expertIcon ?: R.mipmap.palm_portrait_1)
            }
        }
    }

    fun SpannableStringBuilder.appendWithSpan(text: CharSequence?, what: Any?, flags: Int): SpannableStringBuilder {
        val start: Int = length
        append(text)
        setSpan(what, start, length, flags)
        return this
    }

    private inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content = itemView.findViewById<TextView>(R.id.tv_palmchat_result)
        val expert = itemView.findViewById<ImageView>(R.id.iv_palmchat_portrait)
    }

    private inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content = itemView.findViewById<TextView>(R.id.tv_palmchat_result)
        val title = itemView.findViewById<TextView>(R.id.tv_palmchat_title)
        val image = itemView.findViewById<ImageView>(R.id.iv_palmchat)
        val expert = itemView.findViewById<ImageView>(R.id.iv_palmchat_portrait)
    }

    override fun showResult(datas: List<PalmDataHolder>) {
        list.clear()
        list.addAll(datas)
        adapter.notifyDataSetChanged()
    }

    override fun addResult(data: PalmDataHolder) {
        list.add(data)
        adapter.notifyItemInserted(adapter.itemCount)
        recyclerView.smoothScrollToPosition(adapter.itemCount)
    }

    private fun String.splitFromChar(char: String): String {
        return try {
            val result = this.substring(this.indexOf(char) + 1, this.lastIndexOf(char))
            result
        } catch (e: Exception) {
            ""
        }
    }
}