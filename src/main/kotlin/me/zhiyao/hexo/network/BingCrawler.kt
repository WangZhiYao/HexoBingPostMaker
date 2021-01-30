package me.zhiyao.hexo.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import me.zhiyao.hexo.exts.logger
import me.zhiyao.hexo.utils.ImageUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.concurrent.TimeUnit

/**
 *
 * @author WangZhiYao
 * @date 2020/12/20
 */
object BingCrawler {

    private const val DEFAULT_TIME_OUT = 60L
    private const val BASE_IMAGE_DIR = "/hexo/bing.339.im/source/images"

    private val logger = logger()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
        .build()

    private val request = Request.Builder()
        .url("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN")
        .get()
        .build()

    fun getImage(): BingImage? {
        var response: Response? = null

        try {
            response = okHttpClient.newCall(request).execute()
        } catch (ex: IOException) {
            logger.error("get bing image failed: ", ex)
        }

        if (response == null || !response.isSuccessful) {
            logger.error("get bing image failed, response is null or not successful: {}", response?.code)
            return null
        }

        val responseStr = try {
            response.body?.string()
        } catch (ex: IOException) {
            logger.error("get bing image failed, get response string error", ex)
            return null
        }

        if (responseStr.isNullOrBlank()) {
            logger.error("get bing image failed, response string is null or blank")
            return null
        }

        logger.info(responseStr)

        val imageArchive: HPImageArchive?
        try {
            imageArchive = Gson().fromJson(responseStr, HPImageArchive::class.java)
        } catch (ex: JsonSyntaxException) {
            logger.error("get bing image failed, parse response to json error", ex)
            return null
        }

        val images = imageArchive.images

        if (images.isNullOrEmpty()) {
            logger.error("get bing image failed, images is empty")
            return null
        }

        val image = images[0]

        val url = image.url
        val date = image.endDate

        val localDate = if (date.isBlank()) {
            LocalDate.now()
        } else {
            try {
                LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE)
            } catch (ex: DateTimeParseException) {
                logger.error("parse date failed", ex)
                LocalDate.now()
            }
        }

        if (!localDate.isEqual(LocalDate.now())) {
            logger.info("seems date from response is not correct, sleep 10s and try it later...")
            try {
                TimeUnit.SECONDS.sleep(10)
            } catch (ex: InterruptedException) {
                logger.error("something wrong in thread sleep")
            }
            return getImage()
        }

        val copyright = image.copyright
        val description = image.title

        val imageFileName = download(url, localDate)
        if (imageFileName.isNullOrBlank()) {
            logger.error("download bing image failed")
            return null
        }

        val thumbImageFilePath = "$BASE_IMAGE_DIR/thumbs/" +
                imageFileName.replace("1920x1080", "533x300")

        val resizeSuccess = ImageUtils.resizeKeepAspectRatioTo(
            "$BASE_IMAGE_DIR/$imageFileName",
            thumbImageFilePath,
            300
        )

        if (!resizeSuccess) {
            return null
        }

        return BingImage(
            localDate.year,
            localDate.month.value,
            localDate.dayOfMonth,
            copyright,
            description,
            null,
            imageFileName
        )
    }

    private fun download(url: String, localDate: LocalDate): String? {
        val imageRequest = Request.Builder()
            .get()
            .url("https://cn.bing.com$url")
            .build()

        var imageResponse: Response? = null
        try {
            imageResponse = okHttpClient.newCall(imageRequest).execute()
        } catch (ex: IOException) {
            logger.error("download image failed", ex)
        }

        if (imageResponse == null || !imageResponse.isSuccessful) {
            logger.error("download image failed, response is null or not successful: {}", imageResponse?.code)
            return null
        }

        val responseByteArray = try {
            imageResponse.body?.bytes()
        } catch (ex: IOException) {
            logger.error("read image byte array error", ex)
            return null
        }

        if (responseByteArray == null || responseByteArray.isEmpty()) {
            logger.error("image byte array is null or empty")
            return null
        }

        val fileName = try {
            url.split("OHR.")[1].split("&")[0]
        } catch (ex: Exception) {
            logger.error("split filename failed", ex)
            "${localDate.year}-${localDate.month.value}-${localDate.dayOfMonth}_1920x1080.jpg"
        }

        val filePath = try {
            Files.write(
                Paths.get(BASE_IMAGE_DIR, fileName),
                responseByteArray,
                StandardOpenOption.CREATE
            )
        } catch (ex: IOException) {
            logger.error("save to image file failed", ex)
            return null
        }

        return filePath.toFile().name
    }
}