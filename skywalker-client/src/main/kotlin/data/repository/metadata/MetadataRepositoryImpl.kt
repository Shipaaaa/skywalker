package data.repository.metadata

import data.service.metadata.MetadataService
import data.worker.callable.MetadataCallable
import data.worker.runnable.MetadataRunnable
import domain.entity.FileMetadataEntity
import org.apache.ignite.Ignite

/**
 * Created by v.shipugin on 18/11/2018
 */
class MetadataRepositoryImpl(private val ignite: Ignite) : MetadataRepository {

    override fun saveFileMetadata(file: FileMetadataEntity) {
        val runnable = MetadataRunnable { metadataService: MetadataService ->
            metadataService.saveFileMetadata(file)
        }

        ignite.compute().run(runnable)
    }

    override fun loadFileMetadata(fileName: String): FileMetadataEntity? {
        val callable = MetadataCallable { metadataService: MetadataService ->
            metadataService.loadFileMetadata(fileName)
        }

        return ignite.compute().call(callable)
    }

    override fun deleteFileMetadata(fileName: String) {
        val runnable = MetadataRunnable { metadataService: MetadataService ->
            metadataService.deleteFileMetadata(fileName)
        }

        ignite.compute().run(runnable)
    }
}


