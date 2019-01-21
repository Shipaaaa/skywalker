package presentation.presenter

import domain.entity.FileEntity
import domain.entity.FileMetadataEntity
import domain.usecase.CacheUseCase
import presentation.model.LoadingFileResult
import java.io.IOException

/**
 * Created by v.shipugin on 03/11/2018
 */
class CachePresenter(private val cacheUseCase: CacheUseCase) {

    @Throws(
        IOException::class,
        IllegalArgumentException::class,
        NullPointerException::class
    )
    fun saveFile(fileEntity: FileEntity) {
        cacheUseCase.saveFile(fileEntity)
    }

    @Throws(
        NullPointerException::class,
        IOException::class,
        IllegalArgumentException::class
    )
    fun updateFile(fileEntity: FileEntity) {
        cacheUseCase.updateFile(fileEntity)
    }

    @Throws(NullPointerException::class)
    fun loadFile(fileName: String): LoadingFileResult {
        return cacheUseCase.loadFile(fileName)
    }

    fun deleteFile(fileName: String) {
        cacheUseCase.deleteFile(fileName)
    }

    @Throws(NullPointerException::class)
    fun loadAllInfo(): List<FileMetadataEntity> {
        return cacheUseCase.loadAllInfo()
    }
}