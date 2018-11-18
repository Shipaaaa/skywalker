import core.utils.Logger
import data.repository.archive.ArchiveRepositoryImpl
import data.repository.metadata.MetadataRepositoryImpl
import data.repository.prediction.PredictionRepositoryImpl
import domain.usecase.CacheUseCaseImpl
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import presentation.ConsoleView
import java.util.*

/**
 * Created by v.shipugin on 03/11/2018
 */
class ClientInitializer {

    private lateinit var ignite: Ignite

    fun initLogger() {
        // TODO Добавить переключение в зависимости от типа сборки
        Logger.init(true)
    }

    fun initIgnite() {
        val igniteCfg = IgniteConfiguration().apply {
            isPeerClassLoadingEnabled = true
            isClientMode = true
        }

        ignite = Ignition.start(igniteCfg)
    }

    fun destroyIgnite() {
        Ignition.stop(ignite.name(), false)
    }

    fun startApp() {
        val scanner by lazy { Scanner(System.`in`) }
        val archiveUseCase by lazy {
            CacheUseCaseImpl(
                PredictionRepositoryImpl(),
                MetadataRepositoryImpl(ignite),
                ArchiveRepositoryImpl(ignite)
            )
        }

        ConsoleView(scanner, archiveUseCase).start()
    }
}