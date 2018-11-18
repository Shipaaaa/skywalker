package domain.usecase

import presentation.model.LoadingFileResult

/**
 * Created by v.shipugin on 15/09/2018
 */
interface CacheUseCase {

    fun saveFile(fileName: String, filePath: String)

    fun loadFile(fileName: String): LoadingFileResult?

    fun deleteFile(fileName: String)
}