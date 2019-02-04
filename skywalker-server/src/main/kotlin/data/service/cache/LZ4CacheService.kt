package data.service.cache

import domain.entity.FileEntity
import net.jpountz.lz4.LZ4Factory

/**
 * Created by v.shipugin on 03/11/2018
 */
class LZ4CacheService : BaseCacheService() {

    companion object {
        const val TAG = "LZ4CacheService"
        private val lz4Factory = LZ4Factory.fastestInstance()
    }

    override fun saveFile(file: FileEntity) {
        val compressedBlob = lz4Factory.fastCompressor().compress(file.blob)
        super.saveFile(FileEntity(file.name, compressedBlob))
    }

    @Throws(NullPointerException::class)
    override fun loadFile(fileName: String, originalSize: Int): FileEntity {
        val compressedFile = super.loadFile(fileName, originalSize)
        val decompressedBlob = lz4Factory.fastDecompressor().decompress(compressedFile.blob, originalSize)
        return FileEntity(compressedFile.name, decompressedBlob)
    }
}