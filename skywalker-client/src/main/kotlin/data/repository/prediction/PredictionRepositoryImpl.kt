package data.repository.prediction

import core.utils.Logger
import data.model.PredictResponse
import domain.entity.CompressionType
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking

/**
 * Created by v.shipugin on 18/11/2018
 */
class PredictionRepositoryImpl(private val httpClient: HttpClient) : PredictionRepository {

    companion object {
        private const val TAG = "PredictionRepositoryImpl"

        private val predictServiceUrl = System.getenv("PREDICT_SERVICE_URL")
            ?: "http://localhost/skywalker/predict"
    }

    override fun predictCompressionType(fileName: String): CompressionType? {

        val result = runBlocking {
            httpClient.post<PredictResponse>(predictServiceUrl) {
                contentType(ContentType.Application.Json)
                body = fileName
            }
        }

        Logger.log(TAG, "predict result: $result")

        return CompressionType.of(result.compressionType)
    }
}

