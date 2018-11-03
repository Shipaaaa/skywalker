package presentation

import data.repository.FirstCacheRepositoryImpl
import data.repository.SecondCacheRepositoryImpl
import domain.entity.FileEntity
import domain.usecase.ArchiveUseCaseImpl
import presentation.Main.Menu.*
import java.util.*

/**
 * Created by v.shipugin on 05/09/2018
 */
fun main(args: Array<String>) {
    Main().work()
}

class Main {

    private val archiveUseCase by lazy {
        ArchiveUseCaseImpl(
            FirstCacheRepositoryImpl(),
            SecondCacheRepositoryImpl()
        )
    }

    private val scanner by lazy { Scanner(System.`in`) }

    fun work() {
        println("Hello!\nI'm Skywalker")

        println(
            "Menu:\n" +
                    "Enter 1 for save file\n" +
                    "Enter 2 for load file\n" +
                    "Enter 0 for exit"
        )

        println("Choose menu item:")
        var pick = scanner.nextInt()

        while (pick != EXIT.number) {

            when (pick) {
                SAVE.number -> saveFile()
                LOAD.number -> load()
                else -> println("I didn't understand you. Repeat please:")
            }

            println("Choose menu item:")
            pick = scanner.nextInt()
        }

        println("Goodbye!")
    }

    private fun saveFile() {
        println("Enter file name:")
        val fileName = scanner.next()

        println("Enter file path:")
        val filePath = scanner.next()

        println("Saving file: $fileName")

        archiveUseCase.zipFile(
            FileEntity(fileName, filePath)
        )
    }

    private fun load() {
        println("Enter file name:")
        val fileName = scanner.next()

        println("Loading file: $fileName")
        val file = archiveUseCase.unzipFile(fileName)

        println("File mame: ${file.name}.\nFile path: ${file.path}.")
    }

    enum class Menu(val number: Int) {
        SAVE(1),
        LOAD(2),
        EXIT(0)
    }
}