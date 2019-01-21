package presentation.model

import domain.entity.FileEntity
import domain.entity.FileMetadataEntity

/**
 * Created by v.shipugin on 15/09/2018
 */
data class LoadingFileResult(
    val fileMetadataEntity: FileMetadataEntity,
    val fileEntity: FileEntity
)