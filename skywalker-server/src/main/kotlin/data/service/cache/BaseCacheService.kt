package data.service.cache

import domain.entity.FileEntity
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.ServiceContext
import java.util.*

/**
 * Created by v.shipugin on 03/11/2018
 */
abstract class BaseCacheService : CacheService {

    private lateinit var name: String

    @IgniteInstanceResource
    private lateinit var ignite: Ignite

    private lateinit var cache: IgniteCache<String, String>

    override fun init(serviceContext: ServiceContext) {
        name = serviceContext.name()

        cache = ignite.getOrCreateCache(serviceContext.cacheName())
    }

    override fun execute(serviceContext: ServiceContext) {
        // Do nothing
    }

    override fun cancel(serviceContext: ServiceContext) {
        // Do nothing
    }

    override fun saveFile(file: FileEntity) {
        val key = file.name
        val encodedFile = Base64.getEncoder().encodeToString(file.blob)

        cache.put(key, encodedFile)
    }

    override fun loadFile(fileName: String): FileEntity? {
        return cache
            .get(fileName)
            ?.let { Base64.getDecoder().decode(it) }
            ?.let { FileEntity(fileName, it) }
    }

    override fun deleteFile(fileName: String) {
        cache.remove(fileName)
    }
}