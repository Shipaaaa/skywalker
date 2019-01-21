package domain.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.io.InputStream
import kotlinx.io.OutputStream

/**
 * Created by v.shipugin on 16/09/2018
 */
fun String.getFileExtension(): String {
    val lastIndexOf = this.lastIndexOf(".")
    return if (lastIndexOf == -1) {
        ""
    } else {
        this.substring(lastIndexOf)
    }
}

/**
 * Utility boilerplate method that suspending,
 * copies a [this] [InputStream] into an [out] [OutputStream] in a separate thread.
 *
 * [bufferSize] and [yieldSize] allows to control how and when the suspending is performed.
 * The [dispatcher] allows to specify where will be this executed (for example a specific thread pool).
 */
suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = kotlin.io.DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO
): Long {
    return kotlinx.coroutines.withContext(dispatcher) {
        val buffer = kotlin.ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                kotlinx.coroutines.yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}