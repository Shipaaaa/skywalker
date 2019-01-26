package data.repository.prediction

import Configurations
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

        private val predictServiceUrl = Configurations.PREDICTION_SERVICE_URL
    }

    override fun predictCompressionType(sample: String): CompressionType? {

        val result = runBlocking {
            httpClient.post<PredictResponse>(predictServiceUrl) {
                contentType(ContentType.Application.Json)
                body = sample
            }
        }

        Logger.log(TAG, "predict result: $result")

        return CompressionType.of(result.compressionType)
    }
}

