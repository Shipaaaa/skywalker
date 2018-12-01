package data.worker.runnable

import data.service.cache.CacheService
import data.service.cache.SnappyCacheService
import org.apache.ignite.lang.IgniteRunnable
import org.apache.ignite.resources.ServiceResource

/**
 * Created by v.shipugin on 18/11/2018
 */
class SnappyRunnable(
    private val action: (CacheService) -> Unit
) : IgniteRunnable {

    // FIXME в анатации должна быть константа. Я не нашел как динамически менять значение в анатации.
    @ServiceResource(serviceName = SnappyCacheService.TAG)
    private lateinit var cacheService: CacheService

    override fun run() {
        action(cacheService)
    }
}