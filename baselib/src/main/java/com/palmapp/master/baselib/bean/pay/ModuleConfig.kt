package com.palmapp.master.baselib.bean.pay

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/19
 */

class ModuleConfig {
    var id: Int = 0// 20031
    var name: String = ""// （测试）订阅配置
    var style: String = ""// 0
    var data_type: Int = 0// 0
    var type: Int = 0// 0
    var description: String = ""
    var contents: List<ModuleConfig> = emptyList()
    var extra: String = ""
    var banner: String = ""
}
