package me.zhiyao.hexo.utils

import me.zhiyao.hexo.exts.logger
import net.coobird.thumbnailator.Thumbnails
import java.io.IOException

/**
 *
 * @author WangZhiYao
 * @date 2020/12/19
 */
object ImageUtils {

    private val logger = logger()

    fun resizeKeepAspectRatioTo(input: String, output: String, height: Int): Boolean {
        return try {
            Thumbnails.of(input)
                .height(height)
                .keepAspectRatio(true)
                .toFile(output)
            true
        } catch (ex: IOException) {
            logger.error("resize image failed", ex)
            false
        }
    }
}