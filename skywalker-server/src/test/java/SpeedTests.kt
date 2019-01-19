import core.utils.Logger
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.util.*


/**
 * Created by v.shipugin on 2019-01-17
 */

private const val TAG = "SpeedTests"
private const val TEST_DATA_PATH: String = "/Users/v.shipugin/Documents/GitHub/skywalker/testFiles"
private const val SEPARATOR = ','

private val sb = StringBuilder()

fun main() {
    Logger.init(true)

    val resultFile = PrintWriter(File("speed_test.csv"))



    sb.append("number").append(SEPARATOR)
    sb.append("name").append(SEPARATOR)
    sb.append("original size (KB)").append(SEPARATOR)

    sb.append("LZ4 size (KB)").append(SEPARATOR)
    sb.append("LZ4 compression time").append(SEPARATOR)
    sb.append("LZ4 decompression time").append(SEPARATOR)
    sb.append("LZ4 total time").append(SEPARATOR)

    sb.append("Snappy size (KB)").append(SEPARATOR)
    sb.append("Snappy compression time").append(SEPARATOR)
    sb.append("Snappy decompression time").append(SEPARATOR)
    sb.append("Snappy total time").append(SEPARATOR)

    sb.append("Bzip2 size (KB)").append(SEPARATOR)
    sb.append("Bzip2 compression time").append(SEPARATOR)
    sb.append("Bzip2 decompression time").append(SEPARATOR)
    sb.append("Bzip2 total time").append(SEPARATOR)
    sb.append('\n')


    var number = 0

    for (fileEntry in File(TEST_DATA_PATH).listFiles()) {
        if (fileEntry.isFile) {
            sb.append(++number).append(SEPARATOR)

            sb.append(fileEntry.name).append(SEPARATOR)
            Logger.log(TAG, "File name: ${fileEntry.name}")

            val fileBlob = Files.readAllBytes(fileEntry.toPath())
            val fileSizeInKB = fileBlob.size / 1000F
            sb.append(fileSizeInKB).append(SEPARATOR)
            Logger.log(TAG, "File size: $fileSizeInKB")

            testLZ4(fileBlob)
            testSnappy(fileBlob)
            testBzip2(fileBlob)

            sb.append('\n')
        }
    }

    resultFile.write(sb.toString())
    resultFile.close()
}

/**
 * Тест производительности алгоритма LZ4
 */
fun testLZ4(byteArray: ByteArray) {
    Logger.log(TAG, "LZ4")

    val startTime = System.currentTimeMillis()
    val compressedBlob = lz4Compress(byteArray)
    val compressedTime = System.currentTimeMillis()
    val decompressedBlob = lz4Decompress(compressedBlob)
    val decompressedTime = System.currentTimeMillis()

    val compressedSize = compressedBlob.size / 1000F
    sb.append(compressedSize).append(SEPARATOR)
    Logger.log(TAG, "LZ4 compressedSize: $compressedSize")

    val compressionTime = compressedTime - startTime
    sb.append(compressionTime).append(SEPARATOR)
    Logger.log(TAG, "LZ4 compressionTime: $compressionTime")

    val decompressionTime = decompressedTime - compressedTime
    sb.append(decompressionTime).append(SEPARATOR)
    Logger.log(TAG, "LZ4 decompressionTime: $decompressionTime")

    val totalTime = decompressedTime - startTime
    sb.append(totalTime).append(SEPARATOR)
    Logger.log(TAG, "LZ4 totalTime: $totalTime")

    if (!Arrays.equals(byteArray, decompressedBlob)) throw Exception("Error in compression")
}

fun lz4Compress(byteArray: ByteArray): ByteArray {
    val blobInputStream = ByteArrayInputStream(byteArray)

    val resultOutputStream = ByteArrayOutputStream()
    val lz4OutputStream = CompressorStreamFactory().createCompressorOutputStream(
        CompressorStreamFactory.LZ4_FRAMED,
        resultOutputStream.buffered()
    )

    blobInputStream.use { input ->
        lz4OutputStream.use { snappyOutput ->
            input.copyTo(snappyOutput)
        }
    }

    return resultOutputStream.toByteArray()
}

fun lz4Decompress(compressedByteArray: ByteArray): ByteArray {
    val compressedOutputStream = ByteArrayInputStream(compressedByteArray).buffered()
    val lz4InputStream = CompressorStreamFactory().createCompressorInputStream(
        CompressorStreamFactory.LZ4_FRAMED,
        compressedOutputStream
    )

    val resultOutputStream = ByteArrayOutputStream()

    lz4InputStream.use { snappyInput ->
        resultOutputStream.use { output ->
            snappyInput.copyTo(output)
        }
    }

    return resultOutputStream.toByteArray()
}


/**
 * Тест производительности алгоритма Snappy
 */
fun testSnappy(byteArray: ByteArray) {
    Logger.log(TAG, "Snappy")

    val startTime = System.currentTimeMillis()
    val compressedBlob = snappyCompress(byteArray)
    val compressedTime = System.currentTimeMillis()
    val decompressedBlob = snappyDecompress(compressedBlob)
    val decompressedTime = System.currentTimeMillis()

    val compressedSize = compressedBlob.size / 1000F
    sb.append(compressedSize).append(SEPARATOR)
    Logger.log(TAG, "Snappy compressedSize: $compressedSize")

    val compressionTime = compressedTime - startTime
    sb.append(compressionTime).append(SEPARATOR)
    Logger.log(TAG, "Snappy compressionTime: $compressionTime")

    val decompressionTime = decompressedTime - compressedTime
    sb.append(decompressionTime).append(SEPARATOR)
    Logger.log(TAG, "Snappy decompressionTime: $decompressionTime")

    val totalTime = decompressedTime - startTime
    sb.append(totalTime).append(SEPARATOR)
    Logger.log(TAG, "Snappy totalTime: $totalTime")

    if (!Arrays.equals(byteArray, decompressedBlob)) throw Exception("Error in compression")
}

fun snappyCompress(byteArray: ByteArray): ByteArray {
    val blobInputStream = ByteArrayInputStream(byteArray)

    val resultOutputStream = ByteArrayOutputStream()
    val snappyOutputStream = CompressorStreamFactory().createCompressorOutputStream(
        CompressorStreamFactory.SNAPPY_FRAMED,
        resultOutputStream.buffered()
    )

    blobInputStream.use { input ->
        snappyOutputStream.use { snappyOutput ->
            input.copyTo(snappyOutput)
        }
    }

    return resultOutputStream.toByteArray()
}

fun snappyDecompress(compressedByteArray: ByteArray): ByteArray {
    val compressedOutputStream = ByteArrayInputStream(compressedByteArray).buffered()
    val snappyInputStream = CompressorStreamFactory().createCompressorInputStream(
        CompressorStreamFactory.SNAPPY_FRAMED,
        compressedOutputStream
    )

    val resultOutputStream = ByteArrayOutputStream()

    snappyInputStream.use { snappyInput ->
        resultOutputStream.use { output ->
            snappyInput.copyTo(output)
        }
    }

    return resultOutputStream.toByteArray()
}


/**
 * Тест производительности алгоритма Bzip2
 */
fun testBzip2(byteArray: ByteArray) {
    Logger.log(TAG, "Bzip2")

    val startTime = System.currentTimeMillis()
    val compressedBlob = bzip2Compress(byteArray)
    val compressedTime = System.currentTimeMillis()
    val decompressedBlob = bzip2Decompress(compressedBlob)
    val decompressedTime = System.currentTimeMillis()

    val compressedSize = compressedBlob.size / 1000F
    sb.append(compressedSize).append(SEPARATOR)
    Logger.log(TAG, "Bzip2 compressedSize: $compressedSize")

    val compressionTime = compressedTime - startTime
    sb.append(compressionTime).append(SEPARATOR)
    Logger.log(TAG, "Bzip2 compressionTime: $compressionTime")

    val decompressionTime = decompressedTime - compressedTime
    sb.append(decompressionTime).append(SEPARATOR)
    Logger.log(TAG, "Bzip2 decompressionTime: $decompressionTime")

    val totalTime = decompressedTime - startTime
    sb.append(totalTime).append(SEPARATOR)
    Logger.log(TAG, "Bzip2 totalTime: $totalTime")

    if (!Arrays.equals(byteArray, decompressedBlob)) throw Exception("Error in compression")
}

fun bzip2Compress(byteArray: ByteArray): ByteArray {
    val blobInputStream = ByteArrayInputStream(byteArray)

    val resultOutputStream = ByteArrayOutputStream()
    val bzip2OutputStream = CompressorStreamFactory().createCompressorOutputStream(
        CompressorStreamFactory.BZIP2,
        resultOutputStream.buffered()
    )

    blobInputStream.use { input ->
        bzip2OutputStream.use { snappyOutput ->
            input.copyTo(snappyOutput)
        }
    }

    return resultOutputStream.toByteArray()
}

fun bzip2Decompress(compressedByteArray: ByteArray): ByteArray {
    val compressedOutputStream = ByteArrayInputStream(compressedByteArray).buffered()
    val bzip2InputStream = CompressorStreamFactory().createCompressorInputStream(
        CompressorStreamFactory.BZIP2,
        compressedOutputStream
    )

    val resultOutputStream = ByteArrayOutputStream()

    bzip2InputStream.use { snappyInput ->
        resultOutputStream.use { output ->
            snappyInput.copyTo(output)
        }
    }

    return resultOutputStream.toByteArray()
}