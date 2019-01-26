package domain.usecase

import domain.entity.FileEntity

interface PredictionUseCase {
    fun getSampleDataFromFile(fileEntity: FileEntity): String
}