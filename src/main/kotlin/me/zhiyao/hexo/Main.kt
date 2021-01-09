package me.zhiyao.hexo

import me.zhiyao.hexo.exts.logger
import me.zhiyao.hexo.network.BingCrawler
import me.zhiyao.hexo.post.PostMaker

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val logger = logger()

            val bingImage = BingCrawler.getImage()
            if (bingImage == null) {
                logger.error("failed to get bing image")
                return
            }

            val postFile = PostMaker.make(bingImage)
            if (postFile == null) {
                logger.error("failed to maker post")
                return
            }
        }
    }
}