package domain.usecase

import data.repository.archive.ArchiveRepository
import data.repository.metadata.MetadataRepository
import data.repository.prediction.PredictionRepository
import domain.entity.FileEntity
import domain.entity.FileMetadataEntity
import presentation.model.LoadingFileResult
import java.io.IOException

/**
 * Created by v.shipugin on 15/09/2018
 */
class CacheUseCaseImpl(
    private val predictionUseCase: PredictionUseCaseImpl,
    private val predictionRepository: PredictionRepository,
    private val metadataRepository: MetadataRepository,
    private val archiveRepository: ArchiveRepository
) : CacheUseCase {

    @Throws(
        IOException::class,
        IllegalArgumentException::class,
        NullPointerException::class
    )
    override fun saveFile(fileEntity: FileEntity) {
        val fileMetadata = metadataRepository.loadFileMetadata(fileEntity.name)
        if (fileMetadata != null) throw IllegalArgumentException("File ${fileEntity.name} already exist")

        val fileSample = predictionUseCase.getSampleDataFromFile(fileEntity)
        val compressionType = predictionRepository.predictCompressionType(fileSample)
            ?: throw NullPointerException("CompressionType not found")

        val metadata = FileMetadataEntity(fileEntity.name, compressionType, fileEntity.blob.size)

        archiveRepository.saveFileWithCompression(fileEntity, compressionType)
        metadataRepository.saveFileMetadata(metadata)
    }

    @Throws(
        NullPointerException::class,
        IOException::class,
        IllegalArgumentException::class
    )
    override fun updateFile(fileEntity: FileEntity) {
        metadataRepository.loadFileMetadata(fileEntity.name)
            ?: throw NullPointerException("File ${fileEntity.name} not found")

        val fileSample = predictionUseCase.getSampleDataFromFile(fileEntity)
        val compressionType = predictionRepository.predictCompressionType(fileSample)
            ?: throw NullPointerException("CompressionType not found")

        val metadata = FileMetadataEntity(fileEntity.name, compressionType, fileEntity.blob.size)

        archiveRepository.saveFileWithCompression(fileEntity, compressionType)
        metadataRepository.saveFileMetadata(metadata)
    }

    @Throws(NullPointerException::class)
    override fun loadFile(fileName: String): LoadingFileResult {
        val fileMetadata = metadataRepository.loadFileMetadata(fileName)
            ?: throw NullPointerException("File $fileName not found")

        val fileEntity = archiveRepository.loadFileWithDecompression(
            fileName = fileName,
            compressionType = fileMetadata.compressionType,
            originalSize = fileMetadata.fullSize
        )
        return LoadingFileResult(fileMetadata, fileEntity)
    }

    override fun deleteFile(fileName: String) {
        metadataRepository
            .loadFileMetadata(fileName)
            ?.compressionType
            ?.let { archiveRepository.deleteFile(fileName, it) }
            ?.also { metadataRepository.deleteFileMetadata(fileName) }
            ?: throw NullPointerException("File $fileName not found")
    }

    override fun loadAllInfo(): List<FileMetadataEntity> {
        return metadataRepository.loadAllMetadata()
    }
}