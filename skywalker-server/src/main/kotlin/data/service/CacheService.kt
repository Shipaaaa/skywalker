package data.service

import domain.entity.FileEntity
import org.apache.ignite.services.Service

/**
 * Created by v.shipugin on 03/11/2018
 */
interface CacheService : Service {

    fun saveFile(file: FileEntity)

    fun loadFile(fileName: String): FileEntity?

    fun deleteFile(fileName: String)
}