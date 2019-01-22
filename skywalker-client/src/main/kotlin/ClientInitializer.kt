import core.utils.Logger
import data.repository.archive.ArchiveRepositoryImpl
import data.repository.metadata.MetadataRepositoryImpl
import data.repository.prediction.PredictionRepositoryImpl
import domain.usecase.CacheUseCaseImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import presentation.view.CacheView
import presentation.view.ConsoleView
import presentation.view.RestView
import java.util.*

/**
 * Created by v.shipugin on 03/11/2018
 */
class ClientInitializer {

    private lateinit var ignite: Ignite
    private lateinit var httpClient: HttpClient

    fun initLogger() {
        Logger.init(Configurations.ENABLE_LOGGING)
    }

    fun initIgnite() {
        val igniteCfg = IgniteConfiguration().apply {
            isPeerClassLoadingEnabled = true
            isClientMode = true
        }

        ignite = Ignition.start(igniteCfg)
    }

    fun initHttpClient() {
        httpClient = HttpClient(Apache) {
            engine {
                // Max time between TCP packets - default 10 seconds
                socketTimeout = 10_000
                // Max time to establish an HTTP connection - default 10 seconds
                connectTimeout = 10_000
                // Max time for the connection manager to start a request - 20 seconds
                connectionRequestTimeout = 20_000
            }
            install(Logging) {
                logger = io.ktor.client.features.logging.Logger.DEFAULT
                level = if (Configurations.ENABLE_LOGGING) LogLevel.ALL else LogLevel.NONE
            }
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
    }

    fun destroyHttpClient() {
        httpClient.close()
    }

    fun destroyIgnite() {
        Ignition.stop(ignite.name(), false)
    }

    fun startApp() {
        val scanner by lazy { Scanner(System.`in`) }

        val cacheUseCase by lazy {
            CacheUseCaseImpl(
                PredictionRepositoryImpl(httpClient),
                MetadataRepositoryImpl(ignite),
                ArchiveRepositoryImpl(ignite)
            )
        }

        @Suppress("ConstantConditionIf")
        val view: CacheView = if (Configurations.ENABLE_REST_API) {
            RestView(cacheUseCase)
        } else {
            ConsoleView(scanner, cacheUseCase)
        }

        view.start()
    }
}