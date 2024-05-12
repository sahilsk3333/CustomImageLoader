package me.iamsahil.customimageloader.image_loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

/**
 * Manages a disk cache for bitmap images.
 * @author Sahil Khan
 */
object DiskCache {

    /**
     * Retrieves a bitmap from disk cache.
     * @param context The application context.
     * @param url The URL of the image as cache key.
     * @return A bitmap if found, null otherwise.
     */
    fun getBitmapFromDisk(context: Context, url: String): Bitmap? {
        val file = File(context.cacheDir, url.hashCode().toString())
        return if (file.exists()) BitmapFactory.decodeFile(file.path) else null
    }

    /**
     * Saves a bitmap to disk cache.
     * @param context The application context.
     * @param url The URL of the image as cache key.
     * @param bitmap The bitmap to save.
     */
    fun saveBitmapToDisk(context: Context, url: String, bitmap: Bitmap) {
        val file = File(context.cacheDir, url.hashCode().toString())
        FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
    }
}