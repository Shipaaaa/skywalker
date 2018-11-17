import core.utils.Logger
import data.repository.LZ4CacheRepositoryImpl
import data.repository.LZOCacheRepositoryImpl
import data.repository.SnappyCacheRepositoryImpl
import domain.usecase.ArchiveUseCase
import domain.usecase.ArchiveUseCaseImpl
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
        // TODO Перенести в service locator
        val archiveUseCase: ArchiveUseCase by lazy {
            ArchiveUseCaseImpl(
                LZOCacheRepositoryImpl(ignite),
                LZ4CacheRepositoryImpl(ignite),
                SnappyCacheRepositoryImpl(ignite)
            )
        }

        // TODO Перенести в service locator
        val scanner by lazy { Scanner(System.`in`) }

        ConsoleView(scanner, archiveUseCase).showMenu()
    }
}