package domain.usecase

import domain.entity.FileEntity

/**
 * Created by v.shipugin on 15/09/2018
 */
interface ArchiveUseCase {

    fun zipFile(file: FileEntity)

    fun unzipFile(fileName: String): FileEntity?
}