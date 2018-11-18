package data.repository.prediction

import domain.entity.CompressionType

/**
 * Created by v.shipugin on 18/11/2018
 */
interface PredictionRepository {

    fun predictCompressionType(fileName: String): CompressionType
}