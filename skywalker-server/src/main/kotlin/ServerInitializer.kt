import core.utils.Logger
import data.service.cache.BZip2CacheService
import data.service.cache.LZ4CacheService
import data.service.cache.SnappyCacheService
import data.service.metadata.MetadataServiceImpl
import domain.entity.FileMetadataEntity
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
        private const val METADATA_CACHE_NAME = "skywalker_metadata_service"

        private const val DATA_CACHE_NAME = "skywalker_data_cache_service"
    }

    private lateinit var ignite: Ignite

    fun initLogger() {
        // TODO Добавить переключение в зависимости от типа сборки
        Logger.init(true)
    }

    fun initIgnite() {

        // TODO Поправить конфигурацию. Очень много предупреждений
        val igniteCfg = IgniteConfiguration().apply {
            isPeerClassLoadingEnabled = true
        }


        val metadataCacheCfg = CacheConfiguration<String, FileMetadataEntity>(METADATA_CACHE_NAME).apply {
            cacheMode = CacheMode.REPLICATED
            atomicityMode = CacheAtomicityMode.ATOMIC
        }

        val metadataServiceConfiguration = ServiceConfiguration().apply {
            name = MetadataServiceImpl.TAG
            cacheName = METADATA_CACHE_NAME
            service = MetadataServiceImpl()
            maxPerNodeCount = 1
        }


        val dataCacheCfg = CacheConfiguration<String, String>(DATA_CACHE_NAME).apply {
            cacheMode = CacheMode.REPLICATED
            atomicityMode = CacheAtomicityMode.ATOMIC
        }

        val bZip2CacheServiceConfiguration = ServiceConfiguration().apply {
            name = BZip2CacheService.TAG
            cacheName = DATA_CACHE_NAME
            service = BZip2CacheService()
            maxPerNodeCount = 1
        }

        val lz4CacheServiceConfiguration = ServiceConfiguration().apply {
            name = LZ4CacheService.TAG
            cacheName = DATA_CACHE_NAME
            service = LZ4CacheService()
            maxPerNodeCount = 1
        }

        val snappyCacheServiceConfiguration = ServiceConfiguration().apply {
            name = SnappyCacheService.TAG
            cacheName = DATA_CACHE_NAME
            service = SnappyCacheService()
            maxPerNodeCount = 1
        }

        ignite = Ignition.start(igniteCfg).apply {
            getOrCreateCache(metadataCacheCfg)
            getOrCreateCache(dataCacheCfg)

            services().deployAll(
                listOf(
                    metadataServiceConfiguration,

                    bZip2CacheServiceConfiguration,
                    lz4CacheServiceConfiguration,
                    snappyCacheServiceConfiguration
                )
            )
        }
    }
}