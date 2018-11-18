package data.worker.callable

import data.service.cache.CacheService
import data.service.cache.LZOCacheService
import domain.entity.FileEntity
import org.apache.ignite.lang.IgniteCallable
import org.apache.ignite.resources.ServiceResource

/**
 * Created by v.shipugin on 18/11/2018
 */
class LZOCallable(
    private val action: (CacheService) -> FileEntity?
) : IgniteCallable<FileEntity> {

    // FIXME в анатации должна быть константа. Я не нашел как динамически менять значение в анатации.
    @ServiceResource(serviceName = LZOCacheService.TAG)
    private lateinit var cacheService: CacheService

    override fun call(): FileEntity? {
        return action(cacheService)
    }
}