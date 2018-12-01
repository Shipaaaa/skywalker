package data.repository.archive

import data.service.cache.CacheService
import data.worker.callable.LZ4Callable
import data.worker.callable.BZip2Callable
import data.worker.callable.SnappyCallable
import data.worker.runnable.LZ4Runnable
import data.worker.runnable.BZip2Runnable
import data.worker.runnable.SnappyRunnable
import domain.entity.CompressionType
import domain.entity.FileEntity
import org.apache.ignite.Ignite

/**
 * Created by v.shipugin on 15/09/2018
 */
class ArchiveRepositoryImpl(private val ignite: Ignite) : ArchiveRepository {

    override fun saveFileWithCompression(file: FileEntity, compressionType: CompressionType) {
        val action = { cacheService: CacheService -> cacheService.saveFile(file) }

        val runnable = when (compressionType) {
            CompressionType.LZ4 -> LZ4Runnable(action)
            CompressionType.BZIP2 -> BZip2Runnable(action)
            CompressionType.SNAPPY -> SnappyRunnable(action)
        }

        ignite.compute().run(runnable)
    }

    override fun loadFileWithDecompression(fileName: String, compressionType: CompressionType): FileEntity? {
        val action = { cacheService: CacheService -> cacheService.loadFile(fileName) }

        val callable = when (compressionType) {
            CompressionType.LZ4 -> LZ4Callable(action)
            CompressionType.BZIP2 -> BZip2Callable(action)
            CompressionType.SNAPPY -> SnappyCallable(action)
        }

        return ignite.compute().call(callable)
    }

    override fun deleteFile(fileName: String, compressionType: CompressionType) {
        val action = { cacheService: CacheService -> cacheService.deleteFile(fileName) }

        val runnable = when (compressionType) {
            CompressionType.LZ4 -> LZ4Runnable(action)
            CompressionType.BZIP2 -> BZip2Runnable(action)
            CompressionType.SNAPPY -> SnappyRunnable(action)
        }

        ignite.compute().run(runnable)
    }
}


