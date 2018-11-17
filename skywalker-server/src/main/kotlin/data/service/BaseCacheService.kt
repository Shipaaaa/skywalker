package data.service

import domain.entity.FileEntity
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.ServiceContext

/**
 * Created by v.shipugin on 03/11/2018
 */
abstract class BaseCacheService : CacheService {

    @IgniteInstanceResource
    private lateinit var ignite: Ignite

    private lateinit var igniteCache: IgniteCache<String, String>

    private lateinit var serviceName: String

    override fun init(serviceContext: ServiceContext) {
        serviceName = serviceContext.name()

        igniteCache = ignite.getOrCreateCache(serviceContext.cacheName())
    }

    override fun execute(serviceContext: ServiceContext) {
        // Do nothing
    }

    override fun cancel(serviceContext: ServiceContext) {
        // Do nothing
    }

    override fun saveFile(file: FileEntity) {
        igniteCache.put(calculateFileKey(file.name), file.path)
    }

    override fun loadFile(fileName: String): FileEntity? {
        return igniteCache
            .get(calculateFileKey(fileName))
            ?.let { FileEntity(fileName, it) }
    }

    override fun deleteFile(fileName: String) {
        igniteCache.remove(calculateFileKey(fileName))
    }

    private fun calculateFileKey(fileName: String): String {
        return serviceName + fileName
    }
}