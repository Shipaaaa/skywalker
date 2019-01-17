package domain.usecase

import data.repository.archive.ArchiveRepository
import data.repository.metadata.MetadataRepository
import data.repository.prediction.PredictionRepository
import domain.entity.FileEntity
import domain.entity.FileMetadataEntity
import presentation.model.LoadingFileResult
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * Created by v.shipugin on 15/09/2018
 */
class CacheUseCaseImpl(
    private val predictionRepository: PredictionRepository,
    private val metadataRepository: MetadataRepository,
    private val archiveRepository: ArchiveRepository
) : CacheUseCase {

    @Throws(IOException::class, NullPointerException::class)
    override fun saveFile(fileName: String, filePath: String) {

        val file = File(filePath)

        val blob = Files.readAllBytes(file.toPath())

        val fileEntity = FileEntity(fileName, blob)

        // TODO добавить кастомную ошибку
        val compressionType = predictionRepository.predictCompressionType(fileEntity)
            ?: throw NullPointerException("CompressionType not found")

        val metadata = FileMetadataEntity(fileName, filePath, compressionType, blob.size)

        archiveRepository.saveFileWithCompression(fileEntity, compressionType)
        metadataRepository.saveFileMetadata(metadata)
    }

    override fun loadFile(fileName: String): LoadingFileResult? {
        val fileMetadata = metadataRepository.loadFileMetadata(fileName) ?: return null

        // TODO Something?
        val fileEntity = archiveRepository.loadFileWithDecompression(fileName, fileMetadata.compressionType)

        return fileEntity?.let { LoadingFileResult(fileMetadata.fileName, fileMetadata.filePath, it) }
    }

    override fun deleteFile(fileName: String) {
        metadataRepository
            .loadFileMetadata(fileName)
            ?.compressionType
            ?.let { archiveRepository.deleteFile(fileName, it) }
            ?.also { metadataRepository.deleteFileMetadata(fileName) }
    }
}