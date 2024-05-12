package me.iamsahil.customimageloader.image_loader


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext




/**
 * Utility object for downloading images via network using OkHttpClient.
 * @author Sahil Khan
 */
object NetworkUtils {
    private val client = OkHttpClient()

    /**
     * Downloads a bitmap image from the specified URL.
     * @param url The URL from which to download the image.
     * @return A bitmap if successful, null if an error occurs.
     */
    suspend fun downloadBitmapFromNetwork(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("ImageLoader", "Failed to download image: ${response.message}")
                    throw IOException("Failed to download image: ${response.message}")
                }
                return@use response.body?.byteStream()?.use(BitmapFactory::decodeStream)
            }
        } catch (e: IOException) {
            Log.e("ImageLoader", "Error downloading image: ${e.message}")
            null
        }
    }
}