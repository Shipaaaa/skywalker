package domain.usecase

import domain.entity.FileEntity
import domain.entity.FileMetadataEntity
import presentation.model.LoadingFileResult
import java.io.IOException

/**
 * Created by v.shipugin on 15/09/2018
 */
interface CacheUseCase {

    @Throws(
        IOException::class,
        IllegalArgumentException::class,
        NullPointerException::class
    )
    fun saveFile(fileEntity: FileEntity)

    @Throws(
        NullPointerException::class,
        IOException::class,
        IllegalArgumentException::class
    )
    fun updateFile(fileEntity: FileEntity)

    fun loadFile(fileName: String): LoadingFileResult

    fun deleteFile(fileName: String)

    fun getAllInfo(): List<FileMetadataEntity>
}