package data.service.metadata

import domain.entity.FileMetadataEntity
import org.apache.ignite.services.Service

/**
 * Created by v.shipugin on 03/11/2018
 */
interface MetadataService : Service {

    fun saveFileMetadata(file: FileMetadataEntity)

    fun loadFileMetadata(fileName: String): FileMetadataEntity?

    fun deleteFileMetadata(fileName: String)
}