package data.service.metadata

import domain.entity.FileMetadataEntity
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.ServiceContext

/**
 * Created by v.shipugin on 03/11/2018
 */
class MetadataServiceImpl : MetadataService {

    companion object {
        const val TAG = "MetadataServiceImpl"
    }

    private lateinit var name: String

    @IgniteInstanceResource
    private lateinit var ignite: Ignite

    private lateinit var cache: IgniteCache<String, FileMetadataEntity>

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

    override fun saveFileMetadata(file: FileMetadataEntity) {
        cache.put(file.fileName, file)
    }

    override fun loadFileMetadata(fileName: String): FileMetadataEntity? {
        return cache.get(fileName)
    }

    override fun deleteFileMetadata(fileName: String) {
        cache.remove(fileName)
    }
}