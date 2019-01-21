package data.service.cache

import domain.entity.FileEntity
import org.apache.ignite.services.Service

/**
 * Created by v.shipugin on 03/11/2018
 */
interface CacheService : Service {

    fun saveFile(file: FileEntity)

    @Throws(NullPointerException::class)
    fun loadFile(fileName: String): FileEntity

    fun deleteFile(fileName: String)
}