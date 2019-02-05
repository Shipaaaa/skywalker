package data.service.cache

import domain.entity.FileEntity

/**
 * Created by v.shipugin on 03/11/2018
 */
class ClearCacheService : BaseCacheService() {

    companion object {
        const val TAG = "ClearCacheService"
    }

    override fun saveFile(file: FileEntity) {
        super.saveFile(FileEntity(file.name, file.blob))
    }

    @Throws(NullPointerException::class)
    override fun loadFile(fileName: String, originalSize: Int): FileEntity {
        val loadedFile = super.loadFile(fileName, originalSize)
        return FileEntity(loadedFile.name, loadedFile.blob)
    }
}