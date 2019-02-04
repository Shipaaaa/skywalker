package data.service.cache

import domain.entity.FileEntity
import org.xerial.snappy.Snappy


/**
 * Created by v.shipugin on 03/11/2018
 */
class SnappyCacheService : BaseCacheService() {

    companion object {
        const val TAG = "SnappyCacheService"
    }

    override fun saveFile(file: FileEntity) {
        val compressedBlob = Snappy.compress(file.blob)
        super.saveFile(FileEntity(file.name, compressedBlob))
    }

    @Throws(NullPointerException::class)
    override fun loadFile(fileName: String, originalSize: Int): FileEntity {
        val compressedFile = super.loadFile(fileName, originalSize)
        val decompressedBlob = Snappy.uncompress(compressedFile.blob)
        return FileEntity(compressedFile.name, decompressedBlob)
    }
}