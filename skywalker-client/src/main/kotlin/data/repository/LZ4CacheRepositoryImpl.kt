package data.repository

import data.service.LZ4CacheService
import data.service.LZOCacheService
import domain.entity.FileEntity
import org.apache.ignite.Ignite
import org.apache.ignite.lang.IgniteCallable
import org.apache.ignite.lang.IgniteRunnable
import org.apache.ignite.resources.ServiceResource

/**
 * Created by v.shipugin on 15/09/2018
 */
class LZ4CacheRepositoryImpl(
    private val ignite: Ignite
) : CacheRepository {

    companion object {
        @Suppress("unused")
        const val SERVICE_NAME = LZ4CacheService.TAG
    }

    override fun saveFile(file: FileEntity) {
        ignite.compute().run(object : IgniteRunnable {

            @ServiceResource(serviceName = SERVICE_NAME)
            private lateinit var cacheService: LZ4CacheService

            override fun run() {
                cacheService.saveFile(file)
            }
        })
    }

    override fun loadFile(fileName: String): FileEntity? {
        return ignite.compute().call(object : IgniteCallable<FileEntity> {

            @ServiceResource(serviceName = SERVICE_NAME)
            private lateinit var cacheService: LZ4CacheService

            override fun call(): FileEntity? {
                return cacheService.loadFile(fileName)
            }
        })
    }

    override fun deleteFile(fileName: String) {
        ignite.compute().run(object : IgniteRunnable {

            @ServiceResource(serviceName = SERVICE_NAME)
            private lateinit var cacheService: LZ4CacheService

            override fun run() {
                cacheService.deleteFile(fileName)
            }
        })
    }
}