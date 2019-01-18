package presentation.model

/**
 * Created by v.shipugin on 03/11/2018
 */
enum class MenuItems(val number: Int) {
    SAVE(1),
    LOAD(2),
    GET_ALL_INFO(3),
    EXIT(0),
    UNKNOWN(-1)
}