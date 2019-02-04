package core.utils

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

/**
 * Created by v.shipugin on 03/11/2018
 */
object Logger {

    private val sb = StringBuilder()
    private const val LOG_FILE_NAME: String = "logs.txt"
    private val logsFile = PrintWriter(FileWriter(File(LOG_FILE_NAME), true))

    private var enableLogs: Boolean = false

    fun init(enableLogs: Boolean) {
        this.enableLogs = enableLogs
    }

    fun destroy() {
        logsFile.close()
    }

    fun log(tag: String, message: String) {
        sb.append("|================================================|").append("\n")
        sb.append("| $tag: $message").append("\n")
        sb.append("|================================================|").append("\n")
        sb.append("\n")

        logsFile.write(sb.toString())
        logsFile.flush()
        sb.clear()

        if (enableLogs) {
            println("|================================================|")
            println("| $tag: $message")
            println("|================================================|")
        }
    }

    fun log(tag: String, error: Throwable) {
        sb.append("|================================================|").append("\n")
        sb.append("| $tag: ${error.message}").append("\n")
        sb.append("|================================================|").append("\n")
        sb.append("\n")
        logsFile.write(sb.toString())
        logsFile.flush()
        sb.clear()

        if (enableLogs) {
            println("|================================================|")
            println("| $tag: ${error.message}")
            println("|================================================|")
        }
    }
}