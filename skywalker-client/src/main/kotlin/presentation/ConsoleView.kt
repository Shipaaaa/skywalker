package presentation

import domain.entity.FileEntity
import domain.usecase.ArchiveUseCase
import presentation.MenuItems.*
import java.util.*

/**
 * Created by v.shipugin on 03/11/2018
 */
class ConsoleView(
    private val scanner: Scanner,
    private val archiveUseCase: ArchiveUseCase
) {

    fun showMenu() {
        showMessage("Hello!\nI'm Skywalker")

        showMessage(
            "Menu:\n" +
                    "Enter 1 for save file\n" +
                    "Enter 2 for load file\n" +
                    "Enter 0 for exit"
        )

        showMessage("Choose menu item:")
        var pick = scanner.next().toIntOrNull() ?: UNKNOWN.number

        while (pick != EXIT.number) {

            when (pick) {
                SAVE.number -> saveFile()
                LOAD.number -> load()
                else -> showError("I didn't understand you. Repeat please:")
            }

            showMessage("Choose menu item:")
            pick = scanner.next().toIntOrNull() ?: UNKNOWN.number
        }

        showMessage("Goodbye!")
    }

    private fun saveFile() {
        showMessage("Enter file name:")
        val fileName = scanner.next()

        showMessage("Enter file path:")
        val filePath = scanner.next()

        showMessage("Saving file: $fileName")

        archiveUseCase.zipFile(
            FileEntity(fileName, filePath)
        )
    }

    private fun load() {
        showMessage("Enter file name:")
        val fileName = scanner.next()

        showMessage("Loading file: $fileName")
        val file = archiveUseCase.unzipFile(fileName)

        if (file == null) {
            showError("File: $fileName not found!")
        } else {
            showMessage("File mame: ${file.name}.\nFile path: ${file.path}.")
        }
    }

    private fun showMessage(message: String) {
        System.out.println(message)
    }

    private fun showError(message: String) {
        System.err.println(message)
    }
}