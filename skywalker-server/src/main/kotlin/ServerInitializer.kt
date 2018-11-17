import core.utils.Logger
import data.service.LZ4CacheService
import data.service.LZOCacheService
import data.service.SnappyCacheService
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.services.ServiceConfiguration

/**
 * Created by v.shipugin on 03/11/2018
 */
class ServerInitializer {

    companion object {
        private const val CACHE_NAME = "skywalker_archive_service"
    }

    private lateinit var ignite: Ignite

    fun initLogger() {
        // TODO Добавить переключение в зависимости от типа сборки
        Logger.init(true)
    }

    fun initIgnite() {

        val lzOCacheService = ServiceConfiguration().apply {
            name = LZOCacheService.TAG
            cacheName = CACHE_NAME
            service = LZOCacheService()
            maxPerNodeCount = 1
        }

        val lz4CacheService = ServiceConfiguration().apply {
            name = LZ4CacheService.TAG
            cacheName = CACHE_NAME
            service = LZ4CacheService()
            maxPerNodeCount = 1
        }

        val snappyCacheService = ServiceConfiguration().apply {
            name = SnappyCacheService.TAG
            cacheName = CACHE_NAME
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

        val cacheCfg = CacheConfiguration<String, String>(CACHE_NAME).apply {
            cacheMode = CacheMode.REPLICATED
            atomicityMode = CacheAtomicityMode.ATOMIC
        }

        ignite = Ignition.start(igniteCfg).apply {
            getOrCreateCache(cacheCfg)
        }
    }

    // TODO It is necessary?
    fun destroyIgnite() {
        Ignition.stopAll(true)
    }
}