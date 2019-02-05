package data.repository.archive

import data.service.cache.*
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

    override fun loadFileWithDecompression(fileName: String, compressionType: CompressionType, originalSize: Int): FileEntity {
        return getServiceProxyByCompressionType(compressionType).loadFile(fileName, originalSize)
    }

    override fun deleteFile(fileName: String, compressionType: CompressionType) {
        getServiceProxyByCompressionType(compressionType).deleteFile(fileName)
    }

    private fun getServiceProxyByCompressionType(compressionType: CompressionType): CacheService {
        val serviceName = when (compressionType) {
            CompressionType.NONE -> ClearCacheService.TAG
            CompressionType.LZ4 -> LZ4CacheService.TAG
            CompressionType.BZIP2 -> BZip2CacheService.TAG
            CompressionType.SNAPPY -> SnappyCacheService.TAG
        }

        return ignite.services().serviceProxy(serviceName, CacheService::class.java, false)
    }
}


