package presentation

import domain.usecase.CacheUseCase
import presentation.model.MenuItems.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by v.shipugin on 03/11/2018
 */
class ConsoleView(
    private val scanner: Scanner,
    private val cacheUseCase: CacheUseCase
) {

    companion object {
        private const val IS_TXT_DEBUG_ENABLED = true
    }

    fun start() {
        showMessage("Hello!\nI'm Skywalker\n\n")

        showMessage(
            "Menu:\n" +
                    "Enter 1 for save file\n" +
                    "Enter 2 for load file\n" +
                    "Enter 3 for getting all files info\n" +
                    "Enter 0 for exit\n\n"
        )

        showMessage("Choose menu item:")
        var pick = scanner.next().toIntOrNull() ?: UNKNOWN.number

        while (pick != EXIT.number) {

            when (pick) {
                SAVE.number -> saveFile()
                LOAD.number -> load()
                GET_ALL_INFO.number -> getAllInfo()
                else -> showError("I didn't understand you. Repeat please:")
            }

            showMessage("Choose menu item:")
            pick = scanner.next().toIntOrNull() ?: UNKNOWN.number
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
            cacheUseCase.saveFile(fileName, filePath)
            showMessage("| File: $fileName saved successfully")

            @Suppress("ConstantConditionIf")
            if (IS_TXT_DEBUG_ENABLED) {
                showMessage("\n|================================================|")
                showMessage("| File content")
                showMessage(File(filePath).readText(Charsets.UTF_8))
                showMessage("\n|================================================|")
            }
        } catch (e: IOException) {
            showError("| File: $fileName not found!")
        } catch (e: NullPointerException) {
            showError("| Something went wrong")
        }

        showMessage("|================================================|\n\n")
    }

    private fun load() {
        showMessage("\n|================================================|")
        showMessage("| You have chosen to load the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Loading file: $fileName\n| ")
        val file = cacheUseCase.loadFile(fileName)

        if (file == null) {
            showError("| File: $fileName not found!")
        } else {
            showMessage("| File mame: ${file.name}\n| File path: ${file.path}")

            @Suppress("ConstantConditionIf")
            if (IS_TXT_DEBUG_ENABLED) {
                showMessage("\n|================================================|")
                showMessage("| File content")
                showMessage(String(file.fileEntity.blob))
                showMessage("\n|================================================|")
            }
        }
        showMessage("|================================================|\n\n")
    }

    private fun getAllInfo() {
        showMessage("\n|================================================|")
        showMessage("| You have chosen to get all information.")
        showMessage("|================================================|")
        val filesMetadata = cacheUseCase.getAllInfo()
        if (filesMetadata.isEmpty()) {
            showMessage("| Cache is empty")
            showMessage("|================================================|")
        } else {
            filesMetadata.forEachIndexed { index, entity ->
                showMessage("| Index: $index")
                showMessage("| File name: ${entity.fileName}")
                showMessage("| File path: ${entity.filePath}")
                showMessage("| Full sie: ${entity.fullSize}")
                showMessage("| Compression: ${entity.compressionType}")
                showMessage("|================================================|")
            }
        }
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
}