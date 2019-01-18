package domain.usecase

import domain.entity.FileMetadataEntity
import presentation.model.LoadingFileResult
import java.io.IOException

/**
 * Created by v.shipugin on 15/09/2018
 */
interface CacheUseCase {

    @Throws(IOException::class)
    fun saveFile(fileName: String, filePath: String)

    fun loadFile(fileName: String): LoadingFileResult?

    fun deleteFile(fileName: String)

    fun getAllInfo(): List<FileMetadataEntity>
}