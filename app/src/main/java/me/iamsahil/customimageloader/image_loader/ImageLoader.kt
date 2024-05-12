package me.iamsahil.customimageloader.image_loader

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Provides functionality to load images with caching.
 */
object ImageLoader {
    /**
     * Attempts to load an image from cache or network.
     * @param context The application context.
     * @param url The URL of the image to load.
     * @return A bitmap if successful, null otherwise.
     */
    suspend fun loadImage(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
        ImageCache.get(url) ?: DiskCache.getBitmapFromDisk(context, url)?.also {
            ImageCache.put(url, it)
        } ?: run {
            try {
                NetworkUtils.downloadBitmapFromNetwork(url)?.also { bitmap ->
                    DiskCache.saveBitmapToDisk(context, url, bitmap)
                    ImageCache.put(url, bitmap)
                }
            } catch (e: IOException) {
                Log.e("ImageLoader", "Failed to load image: ${e.message}")
                null
            }
        }
    }
}