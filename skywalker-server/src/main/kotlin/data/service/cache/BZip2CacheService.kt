package data.service.cache

import domain.entity.FileEntity
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by v.shipugin on 03/11/2018
 */
class BZip2CacheService : BaseCacheService() {

    companion object {
        const val TAG = "BZip2CacheService"
    }

    override fun saveFile(file: FileEntity) {
        val blobInputStream = ByteArrayInputStream(file.blob)

        val resultOutputStream = ByteArrayOutputStream()
        val bzip2OutputStream = CompressorStreamFactory().createCompressorOutputStream(
            CompressorStreamFactory.BZIP2,
            resultOutputStream.buffered()
        )

        blobInputStream.use { input ->
            bzip2OutputStream.use { snappyOutput ->
                input.copyTo(snappyOutput)
            }
        }

        val compressedBlob = resultOutputStream.toByteArray()

        super.saveFile(FileEntity(file.name, compressedBlob))
    }

    override fun loadFile(fileName: String): FileEntity? {
        val compressedFile = super.loadFile(fileName) ?: return null

        val compressedOutputStream = ByteArrayInputStream(compressedFile.blob).buffered()
        val bzip2InputStream = CompressorStreamFactory().createCompressorInputStream(
            CompressorStreamFactory.BZIP2,
            compressedOutputStream
        )

        val resultOutputStream = ByteArrayOutputStream()

        bzip2InputStream.use { snappyInput ->
            resultOutputStream.use { output ->
                snappyInput.copyTo(output)
            }
        }

        return FileEntity(compressedFile.name, resultOutputStream.toByteArray())
    }
}