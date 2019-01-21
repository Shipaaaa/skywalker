package presentation.view

import Configurations
import domain.entity.FileEntity
import presentation.model.MenuItem
import presentation.model.MenuItem.*
import presentation.presenter.CachePresenter
import java.io.File
import java.nio.file.Files
import java.util.*

/**
 * Created by v.shipugin on 03/11/2018
 */
class ConsoleView(
    private val scanner: Scanner,
    private val presenter: CachePresenter
) : CacheView {

    companion object {
        private const val IS_TXT_DEBUG_ENABLED = Configurations.ENABLE_FILE_CONTENT_LOGGING
    }

    override fun start() {
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
    }

    private fun saveFile() {
        showMessage("\n|================================================|")
        showMessage("| You have chosen to save the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Enter file path:")
        val filePath = scanner.next()

        showMessage("| Saving file: $fileName\n| ")

        try {
            val file = File(filePath)
            val blob = Files.readAllBytes(file.toPath())
            val fileEntity = FileEntity(fileName, blob)

            presenter.saveFile(fileEntity)
            showMessage("| File: $fileName saved successfully")

            @Suppress("ConstantConditionIf")
            if (IS_TXT_DEBUG_ENABLED) printBlob(File(filePath).readText(Charsets.UTF_8))
        } catch (e: Exception) {
            showError("| Error: ${e.message}")
        }

        showMessage("|================================================|\n\n")
    }

    private fun update() {
        showMessage("\n|================================================|")
        showMessage("| You have chosen to update the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Enter file path:")
        val filePath = scanner.next()

        showMessage("| Updating file: $fileName\n| ")

        try {
            val file = File(filePath)
            val blob = Files.readAllBytes(file.toPath())
            val fileEntity = FileEntity(fileName, blob)

            presenter.updateFile(fileEntity)
            showMessage("| File: $fileName updated successfully")

            @Suppress("ConstantConditionIf")
            if (IS_TXT_DEBUG_ENABLED) printBlob(File(filePath).readText(Charsets.UTF_8))
        } catch (e: Exception) {
            showError("| Error: ${e.message}")
        }

        showMessage("|================================================|\n\n")
    }

    private fun load() {
        showMessage("\n|================================================|")
        showMessage("| You have chosen to load the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Loading file: $fileName\n| ")

        try {
            val file = presenter.loadFile(fileName)
            showMessage("| File name: ${file.fileMetadataEntity.fileName}")
            showMessage("| Full sie: ${file.fileMetadataEntity.fullSize}")
            showMessage("| Compression: ${file.fileMetadataEntity.compressionType}")

            @Suppress("ConstantConditionIf")
            if (IS_TXT_DEBUG_ENABLED) printBlob(String(file.fileEntity.blob))
        } catch (e: Exception) {
            showError("| Error: ${e.message}")
        }

        showMessage("|================================================|\n\n")
    }

    private fun delete() {
        showMessage("\n|================================================|")
        showMessage("| You have chosen to delete the file.")
        showMessage("| Enter file name:")

        val fileName = scanner.next()

        showMessage("| Deleting file: $fileName\n| ")

        try {
            presenter.deleteFile(fileName)
            showMessage("| File: $fileName deleted successfully")
        } catch (e: Exception) {
            showError("| Error: ${e.message}")
        }

        showMessage("|================================================|\n\n")
    }

    private fun getAllInfo() {
        showMessage("\n|================================================|")
        showMessage("| You have chosen to get all information.")
        showMessage("|================================================|")

        val filesMetadata = presenter.loadAllInfo()
        if (filesMetadata.isEmpty()) {
            showMessage("| Cache is empty")
        } else {
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