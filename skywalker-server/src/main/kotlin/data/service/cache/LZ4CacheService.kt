package data.service.cache

import domain.entity.FileEntity
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by v.shipugin on 03/11/2018
 */
class LZ4CacheService : BaseCacheService() {

    companion object {
        const val TAG = "LZ4CacheService"
    }

    override fun saveFile(file: FileEntity) {

        val blobInputStream = ByteArrayInputStream(file.blob)

        val resultOutputStream = ByteArrayOutputStream()
        val snappyOutputStream = CompressorStreamFactory().createCompressorOutputStream(
            CompressorStreamFactory.LZ4_FRAMED,
            resultOutputStream.buffered()
        )

        blobInputStream.use { input ->
            snappyOutputStream.use { snappyOutput ->
                input.copyTo(snappyOutput)
            }
        }

        val compressedBlob = resultOutputStream.toByteArray()

        super.saveFile(FileEntity(file.name, compressedBlob))
    }

    override fun loadFile(fileName: String): FileEntity? {
        val compressedFile = super.loadFile(fileName) ?: return null

        val compressedOutputStream = ByteArrayInputStream(compressedFile.blob).buffered()
        val snappyInputStream = CompressorStreamFactory().createCompressorInputStream(
            CompressorStreamFactory.LZ4_FRAMED,
            compressedOutputStream
        )

        val resultOutputStream = ByteArrayOutputStream()

        snappyInputStream.use { snappyInput ->
            resultOutputStream.use { output ->
                snappyInput.copyTo(output)
            }
        }

        return FileEntity(compressedFile.name, resultOutputStream.toByteArray())
    }
}