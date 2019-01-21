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
        val lz4OutputStream = CompressorStreamFactory().createCompressorOutputStream(
            CompressorStreamFactory.LZ4_FRAMED,
            resultOutputStream.buffered()
        )

        blobInputStream.use { input ->
            lz4OutputStream.use { snappyOutput ->
                input.copyTo(snappyOutput)
            }
        }

        val compressedBlob = resultOutputStream.toByteArray()

        super.saveFile(FileEntity(file.name, compressedBlob))
    }

    @Throws(NullPointerException::class)
    override fun loadFile(fileName: String): FileEntity {
        val compressedFile = super.loadFile(fileName)

        val compressedOutputStream = ByteArrayInputStream(compressedFile.blob).buffered()
        val lz4InputStream = CompressorStreamFactory().createCompressorInputStream(
            CompressorStreamFactory.LZ4_FRAMED,
            compressedOutputStream
        )

        val resultOutputStream = ByteArrayOutputStream()

        lz4InputStream.use { snappyInput ->
            resultOutputStream.use { output ->
                snappyInput.copyTo(output)
            }
        }

        return FileEntity(compressedFile.name, resultOutputStream.toByteArray())
    }
}