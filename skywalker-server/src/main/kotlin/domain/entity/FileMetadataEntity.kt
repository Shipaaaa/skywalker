package domain.entity

/**
 * Created by v.shipugin on 15/09/2018
 */
data class FileMetadataEntity(
    val fileName: String,
    val filePath: String,
    val compressionType: CompressionType,
    val fullSize: Int
)