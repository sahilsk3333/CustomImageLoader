package me.iamsahil.customimageloader.util


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.SerializationException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Makes a GET request using HttpClient with optional query parameters and handles common network-related errors.
 *
 * @param route The API route to make the GET request to.
 * @param queryParameters Optional query parameters to include in the request.
 * @return A [Result] object representing the success or failure of the network call.
 * @throws DataError.Network.NO_INTERNET If there is no internet connection.
 * @throws DataError.Network.SERIALIZATION If there is an error during serialization/deserialization.
 * @throws DataError.Network.UNKNOWN If there is an unknown network error.
 */
suspend inline fun <reified Response: Any> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> {
    return safeCall {
        get {
            url(route)
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }
}


/**
 * Executes a network call using the provided function and handles common network-related errors.
 *
 * @param execute The function that executes the network call.
 * @return A [Result] object representing the success or failure of the network call.
 * @throws DataError.Network.NO_INTERNET If there is no internet connection.
 * @throws DataError.Network.SERIALIZATION If there is an error during serialization/deserialization.
 * @throws DataError.Network.UNKNOWN If there is an unknown network error.
 */
suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch(e: UnresolvedAddressException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.SERIALIZATION)
    } catch(e: Exception) {
        if(e is CancellationException) throw e
        e.printStackTrace()
        return Result.Error(DataError.Network.UNKNOWN)
    }

    return responseToResult(response)
}


/**
 * Converts an HttpResponse to a Result object based on HTTP status codes and handles common network-related errors.
 *
 * @param response The HttpResponse to convert.
 * @return A [Result] object representing the success or failure of the network call.
 * @throws DataError.Network.UNAUTHORIZED If the request is unauthorized.
 * @throws DataError.Network.REQUEST_TIMEOUT If the request times out.
 * @throws DataError.Network.CONFLICT If there is a conflict in the request.
 * @throws DataError.Network.PAYLOAD_TOO_LARGE If the payload is too large.
 * @throws DataError.Network.TOO_MANY_REQUESTS If there are too many requests.
 * @throws DataError.Network.SERVER_ERROR If there is a server error.
 * @throws DataError.Network.UNKNOWN If there is an unknown network error.
 */
suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> {
    return when(response.status.value) {
        in 200..299 -> Result.Success(response.body<T>())
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

