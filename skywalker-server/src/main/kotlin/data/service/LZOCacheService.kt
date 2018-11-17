package data.service

import domain.entity.FileEntity

/**
 * Created by v.shipugin on 03/11/2018
 */
class LZOCacheService : BaseCacheService() {

    companion object {
        const val TAG = "LZOCacheService"
    }

    override fun saveFile(file: FileEntity) {
        // TODO LZO pre-processing
        super.saveFile(file)
    }

    override fun loadFile(fileName: String): FileEntity? {
        return super.loadFile(fileName)
        // TODO LZO post-processing
    }
}