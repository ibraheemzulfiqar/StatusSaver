package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.security.MessageDigest


object ImageEnhancer {

    fun enhance(
        context: Context,
        path: String,
        outFile: File,
    ) {
        val transformation = EnhancerTransformation(
            saturation = 1.30f,
            scaleFactor = 1.5f
        )

        val bitmap = Glide.with(context)
            .asBitmap()
            .load(path)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transform(transformation)
            .submit()
            .get()

        FileOutputStream(outFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

}

class EnhancerTransformation(
    private val saturation: Float,
    private val scaleFactor: Float
) : BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val scaledWidth = (toTransform.width * scaleFactor).toInt()
        val scaledHeight = (toTransform.height * scaleFactor).toInt()
        val scaledBitmap = toTransform.scale(scaledWidth, scaledHeight)

        val bitmap = pool.get(scaledWidth, scaledHeight, toTransform.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val colorMatrix = ColorMatrix().apply {
            setSaturation(saturation)
        }
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

        return bitmap
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val byteArray = (ID + saturation + scaleFactor).toByteArray(Charset.forName("UTF-8"))
        messageDigest.update(byteArray)
    }

    override fun equals(other: Any?): Boolean {
        if (other is EnhancerTransformation) {
            return saturation == other.saturation && scaleFactor == other.scaleFactor
        }
        return false
    }

    override fun hashCode(): Int {
        return ID.hashCode() + 31 * saturation.hashCode() + 31 * scaleFactor.hashCode()
    }

    companion object {
        private const val ID = "statussaver.videodownloader.videoimagesaver.downloadstatus.EnhancerTransformation"
    }
}
