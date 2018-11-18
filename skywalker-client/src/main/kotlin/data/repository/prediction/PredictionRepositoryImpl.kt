package data.repository.prediction

import domain.entity.CompressionType
import domain.utils.getFileExtension

/**
 * Created by v.shipugin on 18/11/2018
 */
class PredictionRepositoryImpl : PredictionRepository {

    override fun predictCompressionType(fileName: String): CompressionType {
        return when (fileName.getFileExtension()) {
            "txt",
            "exe" -> CompressionType.LZO
            "png" -> CompressionType.LZ4
            else -> CompressionType.SNAPPY
        }
    }
}


