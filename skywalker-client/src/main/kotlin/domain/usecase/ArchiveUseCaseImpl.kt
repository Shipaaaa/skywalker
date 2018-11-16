package domain.usecase

import data.repository.CacheRepository
import domain.entity.FileEntity
import domain.utils.getFileExtension

/**
 * Created by v.shipugin on 15/09/2018
 */
class ArchiveUseCaseImpl(
    private val firstCacheRepository: CacheRepository,
    private val secondCacheRepository: CacheRepository
) : ArchiveUseCase {

    override fun zipFile(file: FileEntity) {
        getRepo(file.name).saveFile(file)
    }

    override fun unzipFile(fileName: String): FileEntity? {
        return getRepo(fileName).loadFile(fileName)
    }

    private fun getRepo(fileName: String): CacheRepository {
        return when (fileName.getFileExtension()) {
            "txt",
            "exe",
            "png" -> {
                firstCacheRepository
            }
            else -> {
                secondCacheRepository
            }
        }
    }
}