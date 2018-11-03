package core.utils

/**
 * Created by v.shipugin on 03/11/2018
 */
object Logger {

    private var enableLogs: Boolean = false

    fun init(enableLogs: Boolean) {
        this.enableLogs = enableLogs
    }

    fun log(tag: String, message: String) {
        if (enableLogs) {
            println("|================================================|")
            println("| $tag: $message")
            println("|================================================|")
        }
    }

    fun log(tag: String, error: Throwable) {
        if (enableLogs) {
            println("|================================================|")
            println("| $tag: ${error.message}")
            println("|================================================|")
        }
    }
}