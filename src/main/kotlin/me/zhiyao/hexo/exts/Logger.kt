package me.zhiyao.hexo.exts

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author WangZhiYao
 * @date 2020/12/20
 */
inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}