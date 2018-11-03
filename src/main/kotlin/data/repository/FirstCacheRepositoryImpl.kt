package data.repository

import data.service.FirstCacheService
import domain.entity.FileEntity
import org.apache.ignite.Ignite
import org.apache.ignite.lang.IgniteCallable
import org.apache.ignite.lang.IgniteRunnable
import org.apache.ignite.resources.ServiceResource

/**
 * Created by v.shipugin on 15/09/2018
 */
// TODO Переделать на generics
class FirstCacheRepositoryImpl(private val ignite: Ignite) : CacheRepository {

    override fun saveFile(file: FileEntity) {
        ignite.compute().run(object : IgniteRunnable {

            @ServiceResource(serviceName = FirstCacheService.TAG)
            private lateinit var cacheService: FirstCacheService

            override fun run() {
                cacheService.saveFile(file)
            }
        })
    }

    override fun loadFile(fileName: String): FileEntity? {
        return ignite.compute().call(object : IgniteCallable<FileEntity> {

            @ServiceResource(serviceName = FirstCacheService.TAG)
            private lateinit var cacheService: FirstCacheService

            override fun call(): FileEntity? {
                return cacheService.loadFile(fileName)
            }
        })
    }

    override fun deleteFile(fileName: String) {
        ignite.compute().run(object : IgniteRunnable {
            @ServiceResource(serviceName = FirstCacheService.TAG)
            private lateinit var cacheService: FirstCacheService

            override fun run() {
                cacheService.deleteFile(fileName)
            }
        })
    }
}