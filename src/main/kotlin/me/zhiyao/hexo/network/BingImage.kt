package me.zhiyao.hexo.network

/**
 *
 * @author WangZhiYao
 * @date 2020/12/20
 */
data class BingImage(
    val year: Int,
    val month: Int,
    val day: Int,
    val title: String,
    val description: String?,
    val location: String?,
    val fileName: String
)
