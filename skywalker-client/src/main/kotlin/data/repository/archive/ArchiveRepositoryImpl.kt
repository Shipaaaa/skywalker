package data.repository.archive

import data.service.cache.BZip2CacheService
import data.service.cache.CacheService
import data.service.cache.LZ4CacheService
import data.service.cache.SnappyCacheService
import domain.entity.CompressionType
import domain.entity.FileEntity
import org.apache.ignite.Ignite

/**
 * Created by v.shipugin on 15/09/2018
 */
class ArchiveRepositoryImpl(private val ignite: Ignite) : ArchiveRepository {

    override fun saveFileWithCompression(file: FileEntity, compressionType: CompressionType) {
        getServiceProxyByCompressionType(compressionType).saveFile(file)
    }

    override fun loadFileWithDecompression(fileName: String, compressionType: CompressionType): FileEntity {
        return getServiceProxyByCompressionType(compressionType).loadFile(fileName)
    }

    override fun deleteFile(fileName: String, compressionType: CompressionType) {
        getServiceProxyByCompressionType(compressionType).deleteFile(fileName)
    }

    private fun getServiceProxyByCompressionType(compressionType: CompressionType): CacheService {
        val serviceName = when (compressionType) {
            CompressionType.LZ4 -> LZ4CacheService.TAG
            CompressionType.BZIP2 -> BZip2CacheService.TAG
            CompressionType.SNAPPY -> SnappyCacheService.TAG
        }

        return ignite.services().serviceProxy(serviceName, CacheService::class.java, false)
    }
}


