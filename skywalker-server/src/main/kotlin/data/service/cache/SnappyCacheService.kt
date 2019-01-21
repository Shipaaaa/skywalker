package data.service.cache

import domain.entity.FileEntity
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


/**
 * Created by v.shipugin on 03/11/2018
 */
class SnappyCacheService : BaseCacheService() {

    companion object {
        const val TAG = "SnappyCacheService"
    }

    override fun saveFile(file: FileEntity) {

        val blobInputStream = ByteArrayInputStream(file.blob)

        val resultOutputStream = ByteArrayOutputStream()
        val snappyOutputStream = CompressorStreamFactory().createCompressorOutputStream(
            CompressorStreamFactory.SNAPPY_FRAMED,
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

    @Throws(NullPointerException::class)
    override fun loadFile(fileName: String): FileEntity {
        val compressedFile = super.loadFile(fileName)

        val compressedOutputStream = ByteArrayInputStream(compressedFile.blob).buffered()
        val snappyInputStream = CompressorStreamFactory().createCompressorInputStream(
            CompressorStreamFactory.SNAPPY_FRAMED,
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