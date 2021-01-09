package me.zhiyao.hexo.post

import me.zhiyao.hexo.exts.logger
import me.zhiyao.hexo.network.BingImage
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.text.NumberFormat

/**
 *
 * @author WangZhiYao
 * @date 2020/12/20
 */
object PostMaker {

    private const val POST_FILE_PATH = "/hexo/bing.339.im/source/_posts"

    private val logger = logger()

    fun make(bingImage: BingImage): File? {
        val nf = NumberFormat.getInstance()
        nf.minimumIntegerDigits = 2

        val postContent = ArrayList<String>()
        postContent.add("---")
        postContent.add(
            "title: ${bingImage.year}.${nf.format(bingImage.month)}.${nf.format(bingImage.day)} - ${
                bingImage.title.replace(
                    ": ",
                    " - "
                )
            }"
        )
        postContent.add("date: ${bingImage.year}.${nf.format(bingImage.month)}.${nf.format(bingImage.day)} 00:00:00")
        postContent.add(
            "cover_index: /images/thumbs/${
                bingImage.fileName.replace(
                    "1920x1080",
                    "533x300"
                )
            }"
        )
        postContent.add("cover_detail: /images/${bingImage.fileName}")
        postContent.add("---")

        if (!bingImage.description.isNullOrBlank()) {
            postContent.add("")
            postContent.add(bingImage.description)
        }

        if (!bingImage.location.isNullOrBlank()) {
            postContent.add("")
            postContent.add(bingImage.location)
        }

        postContent.add("")
        postContent.add("![${bingImage.fileName.split("_")[0]}](/images/${bingImage.fileName})")

        return try {
            Files.write(
                Paths.get(
                    POST_FILE_PATH,
                    "${bingImage.year}-${nf.format(bingImage.month)}-${nf.format(bingImage.day)}.md"
                ),
                postContent,
                StandardOpenOption.CREATE
            ).toFile()
        } catch (ex: IOException) {
            logger.error("create hexo post failed", ex)
            null
        }
    }
}