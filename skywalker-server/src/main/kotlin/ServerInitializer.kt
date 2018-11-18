import core.utils.Logger
import data.service.cache.LZ4CacheService
import data.service.cache.LZOCacheService
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

        private const val LZO_CACHE_NAME = "skywalker_lzo_cache_service"
        private const val LZ4_CACHE_NAME = "skywalker_lz4_cache_service"
        private const val SNAPPY_CACHE_NAME = "skywalker_snappy_cache_service"
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


        // region Metadata
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
        // endregion


        // region LZO
        val lzoCacheCfg = CacheConfiguration<String, String>(LZO_CACHE_NAME).apply {
            cacheMode = CacheMode.REPLICATED
            atomicityMode = CacheAtomicityMode.ATOMIC
        }

        val lzoCacheServiceConfiguration = ServiceConfiguration().apply {
            name = LZOCacheService.TAG
            cacheName = LZO_CACHE_NAME
            service = LZOCacheService()
            maxPerNodeCount = 1
        }
        // endregion


        // region Snappy
        val lz4CacheCfg = CacheConfiguration<String, String>(LZ4_CACHE_NAME).apply {
            cacheMode = CacheMode.REPLICATED
            atomicityMode = CacheAtomicityMode.ATOMIC
        }

        val lz4CacheServiceConfiguration = ServiceConfiguration().apply {
            name = LZ4CacheService.TAG
            cacheName = LZ4_CACHE_NAME
            service = LZ4CacheService()
            maxPerNodeCount = 1
        }
        // endregion


        // region Snappy
        val snappyCacheCfg = CacheConfiguration<String, String>(SNAPPY_CACHE_NAME).apply {
            cacheMode = CacheMode.REPLICATED
            atomicityMode = CacheAtomicityMode.ATOMIC
        }

        val snappyCacheServiceConfiguration = ServiceConfiguration().apply {
            name = SnappyCacheService.TAG
            cacheName = SNAPPY_CACHE_NAME
            service = SnappyCacheService()
            maxPerNodeCount = 1
        }
        // endregion

        ignite = Ignition.start(igniteCfg).apply {
            getOrCreateCache(metadataCacheCfg)

            getOrCreateCache(lzoCacheCfg)
            getOrCreateCache(lz4CacheCfg)
            getOrCreateCache(snappyCacheCfg)

            services().deployAll(
                listOf(
                    metadataServiceConfiguration,

                    lzoCacheServiceConfiguration,
                    lz4CacheServiceConfiguration,
                    snappyCacheServiceConfiguration
                )
            )
        }
    }
}