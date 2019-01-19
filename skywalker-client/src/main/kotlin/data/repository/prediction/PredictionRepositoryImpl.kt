package data.repository.prediction

import core.utils.Logger
import data.model.PredictResponse
import domain.entity.CompressionType
import domain.entity.FileEntity
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import java.util.*


/**
 * Created by v.shipugin on 18/11/2018
 */
class PredictionRepositoryImpl(private val httpClient: HttpClient) : PredictionRepository {

    companion object {
        private const val TAG = "PredictionRepositoryImpl"

        private const val NEED_USE_SIZE = false

        private const val COUNT_OF_BLOCKS = 10
        private const val SIZE_OF_BLOCKS_IN_KB = 200

        private const val SIZE_OF_SAMPLE_IN_KB = 80

        private val predictServiceUrl = System.getenv("PREDICT_SERVICE_URL")
            ?: "http://localhost/skywalker/predict"
    }

    override fun predictCompressionType(fileEntity: FileEntity): CompressionType? {

        val result = runBlocking {
            httpClient.post<PredictResponse>(predictServiceUrl) {
                contentType(ContentType.Application.Json)
                body = getSampleDataFromFile(fileEntity)
            }
        }

        Logger.log(TAG, "predict result: $result")

        return CompressionType.of(result.compressionType)
    }

    private fun getSampleDataFromFile(fileEntity: FileEntity): String {
        val fileBytes = fileEntity.blob

        val sizeDiff = SIZE_OF_SAMPLE_IN_KB - fileBytes.size

        val roundingFileBytes = if (sizeDiff > 0) {
            ByteArray(SIZE_OF_SAMPLE_IN_KB).apply {
                fileBytes.forEachIndexed { index, byte ->
                    this[index] = byte
                }
            }
        } else {
            fileBytes
        }

        val sampleParts: MutableList<ByteArray> = mutableListOf()

        @Suppress("ConstantConditionIf")
        if (NEED_USE_SIZE) {
            val totalCountOfBlocks = roundingFileBytes.size / SIZE_OF_BLOCKS_IN_KB
            val countOfBlocks = SIZE_OF_SAMPLE_IN_KB / SIZE_OF_BLOCKS_IN_KB
            val step = totalCountOfBlocks / countOfBlocks

            var startIndex = 0
            (0 until countOfBlocks).forEach {
                sampleParts.add(
                    Arrays.copyOfRange(
                        roundingFileBytes,
                        startIndex,
                        startIndex + SIZE_OF_BLOCKS_IN_KB
                    )
                )
                startIndex += step * SIZE_OF_BLOCKS_IN_KB
            }
        } else {
            val sizeOfBlocks = SIZE_OF_SAMPLE_IN_KB / COUNT_OF_BLOCKS
            val totalCountOfBlocks = roundingFileBytes.size / sizeOfBlocks
            val step = totalCountOfBlocks / COUNT_OF_BLOCKS

            var startIndex = 0
            (0 until COUNT_OF_BLOCKS).forEach {
                sampleParts.add(
                    Arrays.copyOfRange(
                        roundingFileBytes,
                        startIndex,
                        startIndex + sizeOfBlocks
                    )
                )
                startIndex += step * sizeOfBlocks
            }
        }

        var sampleBytes = ByteArray(0)
        sampleParts.forEach { sampleBytes = sampleBytes.plus(it) }

        val encodedBlob = Base64.getEncoder().encodeToString(sampleBytes)

        Logger.log(TAG, "Sample data size: ${sampleBytes.size}")
        Logger.log(TAG, "Sample data: ${sampleBytes.joinToString(",")}")
        Logger.log(TAG, "Encoded sample data: $encodedBlob")

        return encodedBlob
    }
}

