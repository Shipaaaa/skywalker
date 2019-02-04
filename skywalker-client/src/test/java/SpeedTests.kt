@file:Suppress("ConstantConditionIf")

import core.utils.Logger
import domain.entity.FileEntity
import domain.usecase.PredictionUseCaseImpl
import net.jpountz.lz4.LZ4Factory
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.xerial.snappy.Snappy
import java.io.*
import java.nio.file.Files
import java.util.*


/**
 * Created by v.shipugin on 2019-01-17
 */

private const val TAG = "SpeedTests"
private const val TEST_DATA_PATH: String = "D:\\dataset"
private const val RESULT_FILE_NAME = "speed_test_2.csv"
private const val SEPARATOR = ','

private const val NEED_APPEND = true
private const val NEED_ADD_HEADER = false

private const val NEED_CALC_SAMPLE = true
private const val NEED_SHOW_COMPRESSION_TIME = true
private const val NEED_SHOW_DECOMPRESSION_TIME = true

private const val FIRST_FILE_NUMBER = 1
private const val LAST_FILE_NUMBER = 10300

private val sb = StringBuilder()

private val predictionUseCase = PredictionUseCaseImpl()

private val lz4Factory = LZ4Factory.fastestInstance()

fun main() {
    Logger.init(true)

    val resultFile = PrintWriter(FileWriter(File(RESULT_FILE_NAME), NEED_APPEND))

    if (NEED_ADD_HEADER) addHeader()

    var fileNumber = 0

    val files = File(TEST_DATA_PATH).listFiles().toList()
    Collections.sort<File>(
        files,
        Collections.reverseOrder { f1, f2 ->
            f1.length().compareTo(f2.length())
        }
    )

    for (fileEntry in files.drop(1)) {
        if (fileEntry.isFile) {
            fileNumber++
            if (fileNumber < FIRST_FILE_NUMBER) continue
            if (fileNumber != FIRST_FILE_NUMBER || NEED_APPEND) sb.append('\n')
            if (fileNumber > LAST_FILE_NUMBER) break

            sb.append(fileNumber).append(SEPARATOR)

            val fileName = fileEntry.name
            val fileBlob = Files.readAllBytes(fileEntry.toPath())
            if (NEED_CALC_SAMPLE) {
                val fileSample = predictionUseCase.getSampleDataFromFile(FileEntity(fileName, fileBlob))

                sb.append(fileSample).append(SEPARATOR)
            }

            sb.append(fileName).append(SEPARATOR)
            Logger.log(TAG, "File name: $fileName")

            sb.append(fileEntry.extension).append(SEPARATOR)
            Logger.log(TAG, "File extension: ${fileEntry.extension}")

            val originalFileSizeInKB = fileBlob.size / 1000F
            sb.append(originalFileSizeInKB).append(SEPARATOR)
            Logger.log(TAG, "File size: $originalFileSizeInKB")

            val lz4Result = testLZ4(fileBlob, originalFileSizeInKB)
            val snappyResult = testSnappy(fileBlob, originalFileSizeInKB)
            val bzip2Result = testBZIP2(fileBlob, originalFileSizeInKB)

            val maxResult = listOf(
                Triple("lz4", 1, lz4Result),
                Triple("snappy", 2, snappyResult),
                Triple("bzip2", 3, bzip2Result)
            )
                .filter { it.third > 0f }
                .minBy { it.third }
                ?: Triple("none", 0, 0f)


            Logger.log(TAG, "Max result: $maxResult")

            sb.append(maxResult.third).append(SEPARATOR)
            sb.append(maxResult.first).append(SEPARATOR)
            sb.append(maxResult.second)

            resultFile.write(sb.toString())
            sb.clear()
        }
    }

    resultFile.close()
    Logger.destroy()
}

private fun addHeader() {
    sb.append("number").append(SEPARATOR)
    if (NEED_CALC_SAMPLE) sb.append("sample").append(SEPARATOR)
    sb.append("name").append(SEPARATOR)
    sb.append("extension").append(SEPARATOR)
    sb.append("original size (KB)").append(SEPARATOR)


    sb.append("LZ4 size (KB)").append(SEPARATOR)
    sb.append("LZ4 compression ratio").append(SEPARATOR)
    if (NEED_SHOW_COMPRESSION_TIME) sb.append("LZ4 compression time").append(SEPARATOR)
    if (NEED_SHOW_DECOMPRESSION_TIME) sb.append("LZ4 decompression time").append(SEPARATOR)
    sb.append("LZ4 total time").append(SEPARATOR)
    sb.append("LZ4 result").append(SEPARATOR)


    sb.append("Snappy size (KB)").append(SEPARATOR)
    sb.append("Snappy compression ratio").append(SEPARATOR)
    if (NEED_SHOW_COMPRESSION_TIME) sb.append("Snappy compression time").append(SEPARATOR)
    if (NEED_SHOW_DECOMPRESSION_TIME) sb.append("Snappy decompression time").append(SEPARATOR)
    sb.append("Snappy total time").append(SEPARATOR)
    sb.append("Snappy result").append(SEPARATOR)


    sb.append("Bzip2 size (KB)").append(SEPARATOR)
    sb.append("Bzip2 compression ratio").append(SEPARATOR)
    if (NEED_SHOW_COMPRESSION_TIME) sb.append("Bzip2 compression time").append(SEPARATOR)
    if (NEED_SHOW_DECOMPRESSION_TIME) sb.append("Bzip2 decompression time").append(SEPARATOR)
    sb.append("Bzip2 total time").append(SEPARATOR)
    sb.append("Bzip2 result").append(SEPARATOR)


    sb.append("best result").append(SEPARATOR)
    sb.append("best compressor").append(SEPARATOR)
    sb.append("best compressor id")
    sb.append('\n')
}

/**
 * Тест производительности алгоритма LZ4
 */
private fun testLZ4(fileBlob: ByteArray, originalFileSizeInKB: Float): Float {
    Logger.log(TAG, "LZ4")

    val lz4StartTime = getCurrentTime()
    val lz4CompressedBlob = lz4Compress(fileBlob)
    val lz4CompressedTime = getCurrentTime()
    val lz4DecompressedBlob = lz4Decompress(lz4CompressedBlob, fileBlob.size)
    val lz4DecompressedTime = getCurrentTime()

    val lz4CompressedSize = lz4CompressedBlob.size / 1000F
    sb.append(lz4CompressedSize).append(SEPARATOR)
    Logger.log(TAG, "LZ4 compressedSize: $lz4CompressedSize")

    val lz4CompressionRatio = calcCompressionRatio(lz4CompressedSize, originalFileSizeInKB)
    sb.append(lz4CompressionRatio).append(SEPARATOR)
    Logger.log(TAG, "LZ4 compression ratio: $lz4CompressionRatio")

    val lz4CompressionTime = lz4CompressedTime - lz4StartTime
    if (NEED_SHOW_COMPRESSION_TIME) sb.append(lz4CompressionTime).append(SEPARATOR)
    if (NEED_SHOW_COMPRESSION_TIME) Logger.log(TAG, "LZ4 compressionTime: $lz4CompressionTime")

    val lz4DecompressionTime = lz4DecompressedTime - lz4CompressedTime
    if (NEED_SHOW_DECOMPRESSION_TIME) sb.append(lz4DecompressionTime).append(SEPARATOR)
    if (NEED_SHOW_DECOMPRESSION_TIME) Logger.log(TAG, "LZ4 decompressionTime: $lz4DecompressionTime")

    val lz4TotalTime = lz4DecompressedTime - lz4StartTime
    sb.append(lz4TotalTime).append(SEPARATOR)
    Logger.log(TAG, "LZ4 totalTime: $lz4TotalTime")

    val lz4Result = calcResult(
        compressedSize = lz4CompressedSize,
        compressionRatio = lz4CompressionRatio,
        totalTime = lz4TotalTime
    )
    sb.append(lz4Result).append(SEPARATOR)
    Logger.log(TAG, "LZ4 result: $lz4Result")

    if (!Arrays.equals(fileBlob, lz4DecompressedBlob)) throw Exception("Error in compression")
    return lz4Result
}

/**
 * Тест производительности алгоритма Snappy
 */
private fun testSnappy(fileBlob: ByteArray, originalFileSizeInKB: Float): Float {
    Logger.log(TAG, "Snappy")

    val snappyStartTime = getCurrentTime()
    val snappyCompressedBlob = snappyCompress(fileBlob)
    val snappyCompressedTime = getCurrentTime()
    val snappyDecompressedBlob = snappyDecompress(snappyCompressedBlob)
    val snappyDecompressedTime = getCurrentTime()

    val snappyCompressedSize = snappyCompressedBlob.size / 1000F
    sb.append(snappyCompressedSize).append(SEPARATOR)
    Logger.log(TAG, "Snappy compressedSize: $snappyCompressedSize")

    val snappyCompressionRatio = calcCompressionRatio(snappyCompressedSize, originalFileSizeInKB)
    sb.append(snappyCompressionRatio).append(SEPARATOR)
    Logger.log(TAG, "Snappy compression ratio: $snappyCompressionRatio")

    val snappyCompressionTime = snappyCompressedTime - snappyStartTime
    if (NEED_SHOW_COMPRESSION_TIME) sb.append(snappyCompressionTime).append(SEPARATOR)
    if (NEED_SHOW_COMPRESSION_TIME) Logger.log(TAG, "Snappy compressionTime: $snappyCompressionTime")

    val snappyDecompressionTime = snappyDecompressedTime - snappyCompressedTime
    if (NEED_SHOW_DECOMPRESSION_TIME) sb.append(snappyDecompressionTime).append(SEPARATOR)
    if (NEED_SHOW_DECOMPRESSION_TIME) Logger.log(TAG, "Snappy decompressionTime: $snappyDecompressionTime")

    val snappyTotalTime = snappyDecompressedTime - snappyStartTime
    sb.append(snappyTotalTime).append(SEPARATOR)
    Logger.log(TAG, "Snappy totalTime: $snappyTotalTime")

    val snappyResult = calcResult(
        compressedSize = snappyCompressedSize,
        compressionRatio = snappyCompressionRatio,
        totalTime = snappyTotalTime
    )
    sb.append(snappyResult).append(SEPARATOR)
    Logger.log(TAG, "Snappy result: $snappyResult")

    if (!Arrays.equals(fileBlob, snappyDecompressedBlob)) throw Exception("Error in compression")
    return snappyResult
}

/**
 * Тест производительности алгоритма Bzip2
 */
private fun testBZIP2(fileBlob: ByteArray, originalFileSizeInKB: Float): Float {
    Logger.log(TAG, "Bzip2")

    val bzip2StartTime = getCurrentTime()
    val bzip2CompressedBlob = bzip2Compress(fileBlob)
    val bzip2CompressedTime = getCurrentTime()
    val bzip2DecompressedBlob = bzip2Decompress(bzip2CompressedBlob)
    val bzip2DecompressedTime = getCurrentTime()

    val bzip2CompressedSize = bzip2CompressedBlob.size / 1000F
    sb.append(bzip2CompressedSize).append(SEPARATOR)
    Logger.log(TAG, "Bzip2 compressedSize: $bzip2CompressedSize")

    val bzip2CompressionRatio = calcCompressionRatio(bzip2CompressedSize, originalFileSizeInKB)
    sb.append(bzip2CompressionRatio).append(SEPARATOR)
    Logger.log(TAG, "Bzip2 compression ratio: $bzip2CompressionRatio")

    val bzip2CompressionTime = bzip2CompressedTime - bzip2StartTime
    if (NEED_SHOW_COMPRESSION_TIME) sb.append(bzip2CompressionTime).append(SEPARATOR)
    if (NEED_SHOW_COMPRESSION_TIME) Logger.log(TAG, "Bzip2 compressionTime: $bzip2CompressionTime")

    val bzip2DecompressionTime = bzip2DecompressedTime - bzip2CompressedTime
    if (NEED_SHOW_DECOMPRESSION_TIME) sb.append(bzip2DecompressionTime).append(SEPARATOR)
    if (NEED_SHOW_DECOMPRESSION_TIME) Logger.log(TAG, "Bzip2 decompressionTime: $bzip2DecompressionTime")

    val bzip2TotalTime = bzip2DecompressedTime - bzip2StartTime
    sb.append(bzip2TotalTime).append(SEPARATOR)
    Logger.log(TAG, "Bzip2 totalTime: $bzip2TotalTime")

    val bzip2Result = calcResult(
        compressedSize = bzip2CompressedSize,
        compressionRatio = bzip2CompressionRatio,
        totalTime = bzip2TotalTime
    )
    sb.append(bzip2Result).append(SEPARATOR)
    Logger.log(TAG, "Bzip2 result: $bzip2Result")

    if (!Arrays.equals(fileBlob, bzip2DecompressedBlob)) throw Exception("Error in compression")
    return bzip2Result
}

private fun getCurrentTime() = System.currentTimeMillis()

private fun calcCompressionRatio(compressedSize: Float, originalFileSizeInKB: Float): Float {
    val ratio = originalFileSizeInKB / compressedSize
    return Math.round(ratio * 100) / 100f
}

private fun calcResult(compressedSize: Float, compressionRatio: Float, totalTime: Long): Float {
    val result = if (compressionRatio > 1) (totalTime * compressedSize) / 1000 else 0f
    return Math.round(result * 100) / 100f
}

fun lz4Compress(byteArray: ByteArray): ByteArray {
    return lz4Factory.fastCompressor().compress(byteArray)
}

fun lz4Decompress(compressedByteArray: ByteArray, originalSize: Int): ByteArray {
    return lz4Factory.fastDecompressor().decompress(compressedByteArray, originalSize)
}

fun snappyCompress(byteArray: ByteArray): ByteArray = Snappy.compress(byteArray)

fun snappyDecompress(compressedByteArray: ByteArray): ByteArray = Snappy.uncompress(compressedByteArray)

fun bzip2Compress(byteArray: ByteArray): ByteArray {
    val blobInputStream = ByteArrayInputStream(byteArray)

    val resultOutputStream = ByteArrayOutputStream()
    val bzip2OutputStream = CompressorStreamFactory().createCompressorOutputStream(
        CompressorStreamFactory.BZIP2,
        resultOutputStream.buffered()
    )

    blobInputStream.use { input ->
        bzip2OutputStream.use { bzip2Output ->
            input.copyTo(bzip2Output)
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

    bzip2InputStream.use { bzip2Input ->
        resultOutputStream.use { output ->
            bzip2Input.copyTo(output)
        }
    }

    return resultOutputStream.toByteArray()
}