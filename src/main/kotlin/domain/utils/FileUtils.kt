package domain.utils

/**
 * Created by v.shipugin on 16/09/2018
 */
fun String.getFileExtension(): String {
    val lastIndexOf = this.lastIndexOf(".")

    return if (lastIndexOf == -1) {
        ""
    } else {
        this.substring(lastIndexOf)
    }
}