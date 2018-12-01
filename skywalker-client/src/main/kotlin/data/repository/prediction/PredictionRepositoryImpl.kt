package data.repository.prediction

import domain.entity.CompressionType

/**
 * Created by v.shipugin on 18/11/2018
 */
class PredictionRepositoryImpl : PredictionRepository {

    override fun predictCompressionType(fileName: String): CompressionType {
        return when (fileName) {
            "lz4.txt" -> CompressionType.LZ4
            "bzip2.txt" -> CompressionType.BZIP2
            "snappy.txt" -> CompressionType.SNAPPY
            else -> CompressionType.SNAPPY
        }
    }
}


