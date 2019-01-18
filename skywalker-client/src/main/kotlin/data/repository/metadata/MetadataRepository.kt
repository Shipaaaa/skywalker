package data.repository.metadata

import domain.entity.FileMetadataEntity

/**
 * Created by v.shipugin on 18/11/2018
 */
interface MetadataRepository {

    fun saveFileMetadata(file: FileMetadataEntity)

    fun loadFileMetadata(fileName: String): FileMetadataEntity?

    fun deleteFileMetadata(fileName: String)

    fun loadAllMetadata(): List<FileMetadataEntity>
}