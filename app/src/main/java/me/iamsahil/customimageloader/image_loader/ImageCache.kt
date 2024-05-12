package me.iamsahil.customimageloader.image_loader

import android.graphics.Bitmap

/**
 * Manages an in-memory cache of bitmap images.
 * @author Sahil Khan
 */
object ImageCache {
    private const val CACHE_SIZE = 4 * 1024 * 1024 // 4MiB
    private val memoryCache = androidx.collection.LruCache<String, Bitmap>(CACHE_SIZE)

    fun get(url: String): Bitmap? = memoryCache.get(url)

    fun put(url: String, bitmap: Bitmap) {
        memoryCache.put(url, bitmap)
    }
}