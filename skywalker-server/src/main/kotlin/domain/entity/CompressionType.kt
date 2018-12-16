package domain.entity

/**
 * Created by v.shipugin on 18/11/2018
 */
enum class CompressionType(val value: String) {
    LZ4("LZ4"),
    BZIP2("BZIP2"),
    SNAPPY("SNAPPY");

    companion object {
        fun of(value: String): CompressionType? = CompressionType.values().find { it.value == value }
    }
}