package presentation.model

/**
 * Created by v.shipugin on 03/11/2018
 */
enum class MenuItem(val number: Int) {
    SAVE(1),
    UPDATE(2),
    LOAD(3),
    DELETE(4),
    GET_ALL_INFO(5),

    EXIT(0),
    UNKNOWN(-1);

    companion object {
        fun fromNumber(number: Int?): MenuItem {
            return MenuItem.values().find { it.number == number } ?: MenuItem.UNKNOWN
        }
    }
}