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
        showMessage("Hello!\nI'm Skywalker\n\n")

        showMessage(
            "Menu:\n" +
                    "Enter 1 for save file\n" +
                    "Enter 2 for load file\n" +
                    "Enter 0 for exit\n\n"
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

        showMessage("Goodbye!\n\n")
    }

    private fun saveFile() {
        showMessage("\n|================================================|")
        showMessage("| Your have chosen to save the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Enter file path:")
        val filePath = scanner.next()

        showMessage("| Saving file: $fileName\n| ")

        archiveUseCase.zipFile(
            FileEntity(fileName, filePath)
        )
        showMessage("| File: $fileName saved successfully")
        showMessage("|================================================|\n\n")
    }

    private fun load() {
        showMessage("\n|================================================|")
        showMessage("| Your have chosen to load the file.")
        showMessage("| Enter file name:")
        val fileName = scanner.next()

        showMessage("| Loading file: $fileName\n| ")
        val file = archiveUseCase.unzipFile(fileName)

        if (file == null) {
            showError("| File: $fileName not found!")
        } else {
            showMessage("| File mame: ${file.name}\n| File path: ${file.path}")
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
}