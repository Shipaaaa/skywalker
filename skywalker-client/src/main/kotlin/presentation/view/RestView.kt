package presentation.view

import Configurations
import Configurations.ENABLE_FILE_CONTENT_LOGGING
import core.utils.Logger
import domain.entity.FileEntity
import domain.usecase.CacheUseCase
import domain.utils.copyToSuspend
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import presentation.model.ApiResponse
import presentation.model.ErrorApiResponse
import java.io.File
import java.nio.file.Files

/**
 * Created by v.shipugin on 03/11/2018
 */
class RestView(
    private val cacheUseCase: CacheUseCase
) : CacheView {

    companion object {
        private const val TAG = "RestView"
        val UPLOAD_DIR = Configurations.UPLOAD_DIR
    }

    override fun start() {
        val server = embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                    disableHtmlEscaping()
                    serializeNulls()
                    generateNonExecutableJson()
                }
            }

            install(CallLogging)
            routing {
                post("/file") { saveFile(call) }
                put("/file/{filename}") { updateFile(call) }
                get("/file/{filename}") { loadFile(call) }
                delete("/file/{filename}") { deleteFile(call) }
                get("/file") { getAllInfo(call) }
            }
        }
        server.start(wait = true)
    }

    private suspend fun saveFile(call: ApplicationCall) {
        logMessage("Saving file...")

        val multipart = call.receiveMultipart()
        val fileEntity = getFileEntityFromRequest(multipart)

        logMessage("Saving file: ${fileEntity.name}")

        try {
            cacheUseCase.saveFile(fileEntity)
            logMessage("File: ${fileEntity.name} saved successfully")

            call.respond(ApiResponse("SUCCESS"))

            @Suppress("ConstantConditionIf")
            if (ENABLE_FILE_CONTENT_LOGGING) printBlob(String(fileEntity.blob))
        } catch (e: Exception) {
            logError("Error: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, ErrorApiResponse("${e.message}"))
        }
    }

    private suspend fun updateFile(call: ApplicationCall) {
        val fileName = call.parameters["fileName"]

        if (fileName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, ErrorApiResponse("File name is required"))
            return
        }

        logMessage("Updating file: $fileName")

        val multipart = call.receiveMultipart()
        val fileEntity = getFileEntityFromRequest(multipart)
        if (fileName != fileEntity.name) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorApiResponse("The specified file name does not match the specified file")
            )
            return
        }

        try {
            cacheUseCase.updateFile(fileEntity)
            logMessage("File: $fileName updated successfully")

            call.respond(ApiResponse("SUCCESS"))

            @Suppress("ConstantConditionIf")
            if (ENABLE_FILE_CONTENT_LOGGING) printBlob(String(fileEntity.blob))
        } catch (e: Exception) {
            logError("Error: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, ErrorApiResponse("${e.message}"))
        }
    }

    private suspend fun loadFile(call: ApplicationCall) {
        val fileName = call.parameters["fileName"]

        if (fileName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, ErrorApiResponse("File name is required"))
            return
        }

        logMessage("Loading file: $fileName")

        try {
            val file = cacheUseCase.loadFile(fileName)
            logMessage(
                "File name: ${file.fileMetadataEntity.fileName}\n" +
                        "Full sie: ${file.fileMetadataEntity.fullSize}\n" +
                        "Compression: ${file.fileMetadataEntity.compressionType}"
            )

            call.respond(file.fileMetadataEntity)

            @Suppress("ConstantConditionIf")
            if (ENABLE_FILE_CONTENT_LOGGING) printBlob(String(file.fileEntity.blob))
        } catch (e: Exception) {
            logError("Error: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, ErrorApiResponse("${e.message}"))
        }
    }

    private suspend fun deleteFile(call: ApplicationCall) {
        val fileName = call.parameters["fileName"]

        if (fileName.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, ErrorApiResponse("File name is required"))
            return
        }

        logMessage("Deleting file: $fileName")

        try {
            cacheUseCase.deleteFile(fileName)
            logMessage("File: $fileName deleted successfully")

            call.respond(ApiResponse("SUCCESS"))
        } catch (e: Exception) {
            logError("Error: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, ErrorApiResponse("${e.message}"))
        }
    }

    private suspend fun getAllInfo(call: ApplicationCall) {
        val filesMetadata = cacheUseCase.loadAllInfo()
        filesMetadata.forEachIndexed { index, entity ->
            logMessage(
                "Index: $index\n" +
                        "File name: ${entity.fileName}\n" +
                        "Full sie: ${entity.fullSize}\n" +
                        "Compression: ${entity.compressionType}"
            )
        }

        call.respond(filesMetadata)
    }

    private fun logMessage(message: String) {
        Logger.log(TAG, message)
    }

    private fun logError(message: String) {
        Logger.log(TAG, message)
    }

    private fun printBlob(fileBlob: String) {
        logMessage("File content")
        logMessage(fileBlob)
    }

    private suspend fun getFileEntityFromRequest(multipart: MultiPartData): FileEntity {
        var fileTitle = ""
        var fileContent: File? = null

        multipart.forEachPart { part ->
            logMessage("part.name: ${part.name}")

            when (part) {
                is PartData.FileItem -> {
                    fileTitle = part.originalFileName
                            ?: throw NullPointerException("File name not found")

                    logMessage("part.originalFileName: $fileTitle")
                    val ext = File(fileTitle).extension

                    val uploadDir = File(UPLOAD_DIR)
                    if (!uploadDir.exists()) uploadDir.mkdir()

                    val file = File(
                        uploadDir,
                        "upload-$fileTitle-${System.currentTimeMillis()}.$ext"
                    )

                    part.streamProvider().use { inputStream ->
                        file.outputStream().buffered().use { outputStream ->
                            inputStream.copyToSuspend(outputStream)
                        }
                    }
                    fileContent = file
                }
            }

            part.dispose()
        }

        return FileEntity(fileTitle, Files.readAllBytes(requireNotNull(fileContent).toPath()))
    }
}