package me.iamsahil.customimageloader.image_loader

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import java.io.IOException


/**
 * Composable function to display an image from a URL with states for loading and errors.
 * Allows setting of image scale type.
 * @param url The URL of the image to display.
 * @param modifier Modifiers to apply to the image component.
 * @param contentScale Type of scaling to apply to the image (e.g., crop, fit).
 * @param loadingComposable Composable displayed during the loading.
 * @param errorComposable Composable displayed on error.
 * @author Sahil Khan
 */
@Composable
fun RemoteImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop, // Default scale type is crop
    loadingComposable: @Composable (BoxScope.() -> Unit) = { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) },
    errorComposable: @Composable (BoxScope.() -> Unit) = { Text("Error loading image", color = Color.Red) }
) {
    val scope = rememberCoroutineScope()
    var job: Job? = null
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val image by produceState<Bitmap?>(initialValue = null, key1 = url) {
        job = scope.launch(Dispatchers.IO) {
            try {
                value = ImageLoader.loadImage(context, url)
                isLoading = false
            } catch (e: IOException) {
                Log.e("RemoteImage", "Error setting image: ${e.message}")
                value = null
                isLoading = false
                isError = true
            }
        }
    }

    DisposableEffect(key1 = url) {
        onDispose {
            job?.cancel()  // Cancel the job when the composable is not on screen anymore
        }
    }

    Box(modifier = modifier) {
        when {
            isLoading -> loadingComposable()
            isError -> errorComposable()
            image != null -> Image(
                bitmap = image!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(), // Ensures the image fills the container
                contentScale = contentScale // Apply the selected content scale
            )
            else -> errorComposable()  // Fallback to error composable when no conditions are met
        }
    }
}