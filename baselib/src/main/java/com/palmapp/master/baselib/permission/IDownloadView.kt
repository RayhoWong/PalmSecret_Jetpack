package com.palmapp.master.baselib.permission

import com.palmapp.master.baselib.IView

/**
 *
 * @author :     xiemingrui
 * @since :      2020/4/8
 */
interface IDownloadView:IView {
    fun downloadSuccess()
    fun downloadFail()
}