package data.repository

import domain.entity.FileEntity

/**
 * Created by v.shipugin on 15/09/2018
 */
interface CacheRepository {

    fun saveFile(file: FileEntity)

    fun loadFile(fileName: String): FileEntity

    fun deleteFile(fileName: String)
}