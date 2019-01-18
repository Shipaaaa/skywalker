package data.repository.metadata

import data.service.metadata.MetadataService
import data.service.metadata.MetadataServiceImpl
import domain.entity.FileMetadataEntity
import org.apache.ignite.Ignite

/**
 * Created by v.shipugin on 18/11/2018
 */
class MetadataRepositoryImpl(private val ignite: Ignite) : MetadataRepository {

    override fun saveFileMetadata(file: FileMetadataEntity) {
        getServiceProxy().saveFileMetadata(file)
    }

    override fun loadFileMetadata(fileName: String): FileMetadataEntity? {
        return getServiceProxy().loadFileMetadata(fileName)
    }

    override fun loadAllMetadata(): List<FileMetadataEntity> {
        return getServiceProxy().loadAllMetadata()
    }

    override fun deleteFileMetadata(fileName: String) {
        getServiceProxy().deleteFileMetadata(fileName)
    }

    private fun getServiceProxy(): MetadataService {
        return ignite.services().serviceProxy(MetadataServiceImpl.TAG, MetadataService::class.java, false)
    }
}


