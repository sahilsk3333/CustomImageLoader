package me.iamsahil.customimageloader.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponseItem(

    @SerialName("coverageURL")
    val coverageURL: String,
    @SerialName("id")
    val id: String,
    @SerialName("language")
    val language: String,
    @SerialName("mediaType")
    val mediaType: Int,
    @SerialName("publishedAt")
    val publishedAt: String,
    @SerialName("publishedBy")
    val publishedBy: String,
    @SerialName("thumbnail")
    val thumbnail: Thumbnail,
    @SerialName("title")
    val title: String
)

@Serializable
data class Thumbnail(
    @SerialName("aspectRatio")
    val aspectRatio: Double,
    @SerialName("basePath")
    val basePath: String,
    @SerialName("domain")
    val domain: String,
    @SerialName("id")
    val id: String,
    @SerialName("key")
    val key: String,
    @SerialName("qualities")
    val qualities: List<Int>,
    @SerialName("version")
    val version: Int
){
    fun getImageUrl() = "$domain/$basePath/0/$key"
}
