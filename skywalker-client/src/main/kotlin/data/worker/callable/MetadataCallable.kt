package data.worker.callable

import data.service.metadata.MetadataService
import data.service.metadata.MetadataServiceImpl
import domain.entity.FileMetadataEntity
import org.apache.ignite.lang.IgniteCallable
import org.apache.ignite.resources.ServiceResource

/**
 * Created by v.shipugin on 18/11/2018
 */
class MetadataCallable(
    private val action: (MetadataService) -> FileMetadataEntity?
) : IgniteCallable<FileMetadataEntity> {

    // FIXME в анатации должна быть константа. Я не нашел как динамически менять значение в анатации.
    @ServiceResource(serviceName = MetadataServiceImpl.TAG)
    private lateinit var metadataService: MetadataService

    override fun call(): FileMetadataEntity? {
        return action(metadataService)
    }
}