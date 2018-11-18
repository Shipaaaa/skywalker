package data.service.cache

import data.service.cache.BaseCacheService
import domain.entity.FileEntity

/**
 * Created by v.shipugin on 03/11/2018
 */
class LZ4CacheService : BaseCacheService() {

    companion object {
        const val TAG = "LZ4CacheService"
    }

    override fun saveFile(file: FileEntity) {
        // TODO LZ4 pre-processing
        super.saveFile(file)
    }

    override fun loadFile(fileName: String): FileEntity? {
        return super.loadFile(fileName)
        // TODO LZ4 post-processing
    }
}