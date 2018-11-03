import core.utils.Logger
import data.repository.FirstCacheRepositoryImpl
import data.repository.SecondCacheRepositoryImpl
import data.service.FirstCacheService
import data.service.SecondCacheService
import domain.usecase.ArchiveUseCase
import domain.usecase.ArchiveUseCaseImpl
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.services.ServiceConfiguration
import presentation.ConsoleView
import java.util.*

/**
 * Created by v.shipugin on 03/11/2018
 */
class Initializer {

    private lateinit var ignite: Ignite

    fun initLogger() {
        // TODO Добавить переключение в зависимости от типа сборки
        Logger.init(true)
    }

    fun initIgnite() {
        // TODO Поправить конфигурацию
        val firstServiceConfiguration = ServiceConfiguration().apply {
            name = FirstCacheService.TAG
            service = FirstCacheService()
            maxPerNodeCount = 1
        }

        // TODO Поправить конфигурацию
        val secondServiceConfiguration = ServiceConfiguration().apply {
            name = SecondCacheService.TAG
            service = SecondCacheService()
            maxPerNodeCount = 1
        }

        // TODO Поправить конфигурацию. Очень много предупреждений
        val igniteCfg = IgniteConfiguration().apply {
            setServiceConfiguration(
                firstServiceConfiguration,
                secondServiceConfiguration
            )
        }

        ignite = Ignition.start(igniteCfg)
    }

    fun destroyIgnite() {
        Ignition.stopAll(true)
    }

    fun startApp() {
        // TODO Перенести в service locator
        val archiveUseCase: ArchiveUseCase by lazy {
            ArchiveUseCaseImpl(
                FirstCacheRepositoryImpl(ignite),
                SecondCacheRepositoryImpl(ignite)
            )
        }

        // TODO Перенести в service locator
        val scanner by lazy { Scanner(System.`in`) }

        ConsoleView(scanner, archiveUseCase).showMenu()
    }
}