package data.repository

import domain.entity.FileEntity
import org.apache.ignite.Ignition

/**
 * Created by v.shipugin on 15/09/2018
 */
class SecondCacheRepositoryImpl : CacheRepository {

    private val igniteCache by lazy { Ignition.start().getOrCreateCache<String, String>("second_skywalker_archive") }

    override fun saveFile(file: FileEntity) {
        igniteCache.put(file.name, file.path)
    }

    override fun loadFile(fileName: String) = FileEntity(fileName, igniteCache.get(fileName))

    override fun deleteFile(fileName: String) {
        TODO("not implemented")
    }
}