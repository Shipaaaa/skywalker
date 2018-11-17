import core.utils.Logger
import data.service.LZ4CacheService
import data.service.LZOCacheService
import data.service.SnappyCacheService
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.services.ServiceConfiguration

/**
 * Created by v.shipugin on 03/11/2018
 */
class ServerInitializer {

    private lateinit var ignite: Ignite

    fun initLogger() {
        // TODO Добавить переключение в зависимости от типа сборки
        Logger.init(true)
    }

    fun initIgnite() {

        val lzOCacheService = ServiceConfiguration().apply {
            name = LZOCacheService.TAG
            service = LZOCacheService()
            maxPerNodeCount = 1
        }

        val lz4CacheService = ServiceConfiguration().apply {
            name = LZ4CacheService.TAG
            service = LZ4CacheService()
            maxPerNodeCount = 1
        }

        val snappyCacheService = ServiceConfiguration().apply {
            name = SnappyCacheService.TAG
            service = SnappyCacheService()
            maxPerNodeCount = 1
        }

        // TODO Поправить конфигурацию. Очень много предупреждений
        val igniteCfg = IgniteConfiguration().apply {
            isPeerClassLoadingEnabled = true
            setServiceConfiguration(
                lzOCacheService,
                lz4CacheService,
                snappyCacheService
            )
        }

        ignite = Ignition.start(igniteCfg)
    }

    fun destroyIgnite() {
        Ignition.stopAll(true)
    }
}