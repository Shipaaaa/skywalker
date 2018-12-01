package domain.entity

/**
 * Created by v.shipugin on 15/09/2018
 */
data class FileEntity(
    val name: String,
    val blob: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileEntity

        if (name != other.name) return false
        if (!blob.contentEquals(other.blob)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + blob.contentHashCode()

        return result
    }
}