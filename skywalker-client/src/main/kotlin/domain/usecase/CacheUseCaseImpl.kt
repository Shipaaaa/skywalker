package domain.usecase

import data.repository.archive.ArchiveRepository
import data.repository.metadata.MetadataRepository
import data.repository.prediction.PredictionRepository
import domain.entity.FileEntity
import domain.entity.FileMetadataEntity
import presentation.model.LoadingFileResult

/**
 * Created by v.shipugin on 15/09/2018
 */
class CacheUseCaseImpl(
    private val predictionRepository: PredictionRepository,
    private val metadataRepository: MetadataRepository,
    private val archiveRepository: ArchiveRepository
) : CacheUseCase {

    override fun saveFile(fileName: String, filePath: String) {
        // TODO get blob
        val blob = fileName.toByteArray(Charsets.UTF_8)
        val fileEntity = FileEntity(fileName, blob)

        val compressionType = predictionRepository.predictCompressionType(fileName)
        val metadata = FileMetadataEntity(fileName, filePath, compressionType, blob.size)

        archiveRepository.saveFileWithCompression(fileEntity, compressionType)
        metadataRepository.saveFileMetadata(metadata)
    }

    override fun loadFile(fileName: String): LoadingFileResult? {
        val fileMetadata = metadataRepository.loadFileMetadata(fileName) ?: return null

        // TODO Something?
        val fileEntity = archiveRepository.loadFileWithDecompression(fileName, fileMetadata.compressionType)

        return LoadingFileResult(fileMetadata.fileName, fileMetadata.filePath)
    }

    override fun deleteFile(fileName: String) {
        metadataRepository
            .loadFileMetadata(fileName)
            ?.compressionType
            ?.let { archiveRepository.deleteFile(fileName, it) }
            ?.also { metadataRepository.deleteFileMetadata(fileName) }
    }
}