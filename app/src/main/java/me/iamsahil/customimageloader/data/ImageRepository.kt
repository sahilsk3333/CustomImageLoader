package me.iamsahil.customimageloader.data

import io.ktor.client.HttpClient
import me.iamsahil.customimageloader.data.dto.ImageResponseItem
import me.iamsahil.customimageloader.util.get
import me.iamsahil.customimageloader.util.DataError
import me.iamsahil.customimageloader.util.Result
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val httpClient: HttpClient
) {

    suspend fun getImages(): Result<List<ImageResponseItem>, DataError.Network> {
        return httpClient.get<List<ImageResponseItem>>(
            route = "api/v2/content/misc/media-coverages",
            queryParameters = mapOf(
                "limit" to 100
            )
        )
    }

}