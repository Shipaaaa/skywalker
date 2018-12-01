package presentation.model

import domain.entity.FileEntity

/**
 * Created by v.shipugin on 15/09/2018
 */
// TODO Что выводить пользователю?
data class LoadingFileResult(
    val name: String,
    val path: String,
    val fileEntity: FileEntity
)