package me.zhiyao.hexo.network

import com.google.gson.annotations.SerializedName

/**
 *
 * @author WangZhiYao
 * @date 2020/12/20
 */
data class Image(
    @SerializedName("enddate")
    val endDate: String,
    val url: String,
    val copyright: String,
    val title: String
)
