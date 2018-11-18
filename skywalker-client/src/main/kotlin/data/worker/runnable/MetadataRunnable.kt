package data.worker.runnable

import data.service.metadata.MetadataService
import data.service.metadata.MetadataServiceImpl
import org.apache.ignite.lang.IgniteRunnable
import org.apache.ignite.resources.ServiceResource

/**
 * Created by v.shipugin on 18/11/2018
 */
class MetadataRunnable(
    private val action: (MetadataService) -> Unit
) : IgniteRunnable {

    // FIXME в анатации должна быть константа. Я не нашел как динамически менять значение в анатации.
    @ServiceResource(serviceName = MetadataServiceImpl.TAG)
    private lateinit var metadataService: MetadataService

    override fun run() {
        action(metadataService)
    }
}