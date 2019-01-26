package data.repository.prediction

import domain.entity.CompressionType
import domain.entity.FileEntity

/**
 * Created by v.shipugin on 18/11/2018
 */
interface PredictionRepository {

    fun predictCompressionType(sample: String): CompressionType?

}