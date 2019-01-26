package domain.usecase

import Configurations
import core.utils.Logger
import domain.entity.FileEntity
import java.util.*

class PredictionUseCaseImpl {

    companion object {
        private const val TAG = "PredictionUseCaseImpl"

        private val ENABLE_COUNT_OF_BLOCKS = Configurations.ENABLE_COUNT_OF_BLOCKS

        private val COUNT_OF_BLOCKS_FOR_SAMPLE = Configurations.COUNT_OF_BLOCKS_FOR_SAMPLE
        private val SIZE_OF_BLOCKS_IN_KB = Configurations.SIZE_OF_BLOCKS_IN_KB

        private val SIZE_OF_SAMPLE_IN_KB = Configurations.SIZE_OF_SAMPLE_IN_KB
    }

    fun getSampleDataFromFile(fileEntity: FileEntity): String {
        val fileBytes = fileEntity.blob

        val sizeDiff = SIZE_OF_SAMPLE_IN_KB - fileBytes.size

        val roundedFileBytes = if (sizeDiff > 0) {
            ByteArray(SIZE_OF_SAMPLE_IN_KB).apply {
                fileBytes.forEachIndexed { index, byte ->
                    this[index] = byte
                }
            }
        } else {
            fileBytes
        }

        @Suppress("ConstantConditionIf")
        val sampleParts = if (ENABLE_COUNT_OF_BLOCKS) {
            getPartsByCountOfBlocks(roundedFileBytes)
        } else {
            getPartsBySizeOfBlocks(roundedFileBytes)
        }

        var sampleBytes = ByteArray(0)
        sampleParts.forEach { sampleBytes = sampleBytes.plus(it) }

        val encodedBlob = Base64.getEncoder().encodeToString(sampleBytes)

        Logger.log(TAG, "Sample data size: ${sampleBytes.size}")
        Logger.log(TAG, "Sample data: ${sampleBytes.joinToString(",")}")
        Logger.log(TAG, "Encoded sample data: $encodedBlob")

        return encodedBlob
    }

    private fun getPartsByCountOfBlocks(fileBytes: ByteArray): List<ByteArray> {
        val sampleParts: MutableList<ByteArray> = mutableListOf()

        val sizeOfBlocks = SIZE_OF_SAMPLE_IN_KB / COUNT_OF_BLOCKS_FOR_SAMPLE
        val totalCountOfBlocks = fileBytes.size / sizeOfBlocks
        val step = totalCountOfBlocks / COUNT_OF_BLOCKS_FOR_SAMPLE

        var startIndex = 0
        (0 until COUNT_OF_BLOCKS_FOR_SAMPLE).forEach {
            sampleParts.add(
                Arrays.copyOfRange(
                    fileBytes,
                    startIndex,
                    startIndex + sizeOfBlocks
                )
            )
            startIndex += step * sizeOfBlocks
        }

        return sampleParts
    }

    private fun getPartsBySizeOfBlocks(fileBytes: ByteArray): List<ByteArray> {
        val sampleParts: MutableList<ByteArray> = mutableListOf()

        val totalCountOfBlocks = fileBytes.size / SIZE_OF_BLOCKS_IN_KB
        val countOfBlocks = SIZE_OF_SAMPLE_IN_KB / SIZE_OF_BLOCKS_IN_KB
        val step = totalCountOfBlocks / countOfBlocks

        var startIndex = 0
        (0 until countOfBlocks).forEach {
            sampleParts.add(
                Arrays.copyOfRange(
                    fileBytes,
                    startIndex,
                    startIndex + SIZE_OF_BLOCKS_IN_KB
                )
            )
            startIndex += step * SIZE_OF_BLOCKS_IN_KB
        }

        return sampleParts
    }
}