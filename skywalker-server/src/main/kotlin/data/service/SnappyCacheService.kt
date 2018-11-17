package data.service

import domain.entity.FileEntity

/**
 * Created by v.shipugin on 03/11/2018
 */
class SnappyCacheService : BaseCacheService() {

    companion object {
        const val TAG = "SnappyCacheService"
    }

    override fun saveFile(file: FileEntity) {
        // TODO Snappy pre-processing
        super.saveFile(file)
    }

    override fun loadFile(fileName: String): FileEntity? {
        return super.loadFile(fileName)
        // TODO Snappy post-processing
    }
}