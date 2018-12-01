package data.repository.metadata

import domain.entity.CompressionType
import domain.entity.FileEntity
import domain.entity.FileMetadataEntity

/**
 * Created by v.shipugin on 18/11/2018
 */
interface MetadataRepository {

    fun saveFileMetadata(file: FileMetadataEntity)

    fun loadFileMetadata(fileName: String): FileMetadataEntity?

    fun deleteFileMetadata(fileName: String)
}