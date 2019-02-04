package presentation.view

import Configurations
import core.utils.Logger
import domain.entity.FileEntity
import domain.usecase.CacheUseCase
import presentation.model.MenuItem
import presentation.model.MenuItem.*
import java.io.File
import java.nio.file.Files
import java.util.*

/**
 * Created by v.shipugin on 03/11/2018
 */
class ConsoleView(
    private val scanner: Scanner,
    private val cacheUseCase: CacheUseCase
) : CacheView {

    companion object {
        private const val TAG = "CacheView"
        private const val IS_TXT_DEBUG_ENABLED = Configurations.ENABLE_FILE_CONTENT_LOGGING
    }

    override fun start() {
        Logger.log(TAG, "Start")
        showMessage("Hello!\nI'm Skywalker\n\n")

        showMessage(
            "Menu:\n" +
                    "Enter 1 for save file\n" +
                    "Enter 2 for update file\n" +
                    "Enter 3 for load file\n" +
                    "Enter 4 for delete file\n" +
                    "Enter 5 for getting all files info\n" +
                    "Enter 0 for exit\n\n"
        )

        showMessage("Choose menu item:")
        var pickedMenuItem = MenuItem.fromNumber(scanner.next().toIntOrNull())

        while (pickedMenuItem != EXIT) {

            when (pickedMenuItem) {
                SAVE -> saveFile()
                UPDATE -> update()
                LOAD -> load()
                GET_ALL_INFO -> getAllInfo()
                DELETE -> delete()
                else -> showError("I didn't understand you. Repeat please:")
            }

            showMessage("Choose menu item:")
            pickedMenuItem = MenuItem.fromNumber(scanner.next().toIntOrNull())
        }

        showMessage("Goodbye!\n\n")
        Logger.log(TAG, "Finish")
    }

    private fun saveFile() {
        Logger.log(TAG, "saveFile()")
        showMessage("\n|================================================|")
        showMessage("| You have chosen to save the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Enter file path:")
        val filePath = scanner.next()

        showMessage("| Saving file: $fileName\n| ")
        Logger.log(TAG, "Saving file: $fileName\n")

        try {
            val file = File(filePath)
            val blob = Files.readAllBytes(file.toPath())
            val fileEntity = FileEntity(fileName, blob)

            cacheUseCase.saveFile(fileEntity)
            showMessage("| File: $fileName saved successfully")
            Logger.log(TAG, "File: $fileName saved successfully")

            @Suppress("ConstantConditionIf")
            if (IS_TXT_DEBUG_ENABLED) printBlob(File(filePath).readText(Charsets.UTF_8))
        } catch (e: Exception) {
            Logger.log(TAG, e)
            showError("| Error: ${e.message}")
        }

        showMessage("|================================================|\n\n")
    }

    private fun update() {
        Logger.log(TAG, "update()")
        showMessage("\n|================================================|")
        showMessage("| You have chosen to update the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Enter file path:")
        val filePath = scanner.next()

        showMessage("| Updating file: $fileName\n| ")
        Logger.log(TAG, "Updating file: $fileName\n")

        try {
            val file = File(filePath)
            val blob = Files.readAllBytes(file.toPath())
            val fileEntity = FileEntity(fileName, blob)

            cacheUseCase.updateFile(fileEntity)
            showMessage("| File: $fileName updated successfully")
            Logger.log(TAG, "File: $fileName updated successfully")

            @Suppress("ConstantConditionIf")
            if (IS_TXT_DEBUG_ENABLED) printBlob(File(filePath).readText(Charsets.UTF_8))
        } catch (e: Exception) {
            Logger.log(TAG, e)
            showError("| Error: ${e.message}")
        }

        showMessage("|================================================|\n\n")
    }

    private fun load() {
        Logger.log(TAG, "load()")
        showMessage("\n|================================================|")
        showMessage("| You have chosen to load the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Loading file: $fileName\n| ")
        Logger.log(TAG, "Loading file: $fileName\n")

        try {
            val file = cacheUseCase.loadFile(fileName)
            showMessage("| File name: ${file.fileMetadataEntity.fileName}")
            showMessage("| Full sie: ${file.fileMetadataEntity.fullSize}")
            showMessage("| Compression: ${file.fileMetadataEntity.compressionType}")
            Logger.log(TAG, "File: $fileName loaded successfully")
            
            @Suppress("ConstantConditionIf")
            if (IS_TXT_DEBUG_ENABLED) printBlob(String(file.fileEntity.blob))
        } catch (e: Exception) {
            Logger.log(TAG, e)
            showError("| Error: ${e.message}")
        }

        showMessage("|================================================|\n\n")
    }

    private fun delete() {
        Logger.log(TAG, "delete()")
        showMessage("\n|================================================|")
        showMessage("| You have chosen to delete the file.")
        showMessage("| Enter file name:")

        val fileName = scanner.next()

        showMessage("| Deleting file: $fileName\n| ")
        Logger.log(TAG, "Deleting file: $fileName\n")

        try {
            cacheUseCase.deleteFile(fileName)
            showMessage("| File: $fileName deleted successfully")
            Logger.log(TAG, "File: $fileName deleted successfully")
        } catch (e: Exception) {
            Logger.log(TAG, e)
            showError("| Error: ${e.message}")
        }

        showMessage("|================================================|\n\n")
    }

    private fun getAllInfo() {
        Logger.log(TAG, "getAllInfo()")
        showMessage("\n|================================================|")
        showMessage("| You have chosen to get all information.")
        showMessage("|================================================|")

        val filesMetadata = cacheUseCase.loadAllInfo()
        if (filesMetadata.isEmpty()) {
            showMessage("| Cache is empty")
            Logger.log(TAG, "Cache is empty")
        } else {
            Logger.log(TAG, "All info loaded successfully")
            filesMetadata.forEachIndexed { index, entity ->
                showMessage("| Index: $index")
                showMessage("| File name: ${entity.fileName}")
                showMessage("| Full sie: ${entity.fullSize}")
                showMessage("| Compression: ${entity.compressionType}")
            }
        }
        showMessage("|================================================|\n\n")
    }

    private fun showMessage(message: String) {
        System.err.flush()
        System.out.println(message)
        System.out.flush()
    }

    private fun showError(message: String) {
        System.out.flush()
        System.err.println(message)
        System.err.flush()
    }

    private fun printBlob(fileBlob: String) {
        showMessage("\n|================================================|")
        showMessage("| File content")
        showMessage(fileBlob)
        showMessage("\n|================================================|")
    }
}