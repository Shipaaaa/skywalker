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

    companion object {
        private const val CACHE_NAME = "skywalker_archive_service"
    }

    @IgniteInstanceResource
    private lateinit var ignite: Ignite

    private lateinit var igniteCache: IgniteCache<String, String>

    private var name: String? = null

    override fun init(serviceContext: ServiceContext) {
        igniteCache = ignite.getOrCreateCache(CACHE_NAME)

        name = serviceContext.name()
    }

    override fun cancel(serviceContext: ServiceContext) {
        // TODO something?
    }

    override fun execute(serviceContext: ServiceContext) {
        // TODO something?
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
        return name + fileName
    }
}