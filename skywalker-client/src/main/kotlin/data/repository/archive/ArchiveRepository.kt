package data.repository.archive

import domain.entity.CompressionType
import domain.entity.FileEntity

/**
 * Created by v.shipugin on 15/09/2018
 */
interface ArchiveRepository {

    fun saveFileWithCompression(file: FileEntity, compressionType: CompressionType)

    fun loadFileWithDecompression(fileName: String, compressionType: CompressionType, originalSize: Int): FileEntity

    fun deleteFile(fileName: String, compressionType: CompressionType)
}