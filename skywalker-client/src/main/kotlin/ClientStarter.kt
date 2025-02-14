/**
 * Created by v.shipugin on 05/09/2018
 */

fun main(args: Array<String>) {
    with(ClientInitializer()) {
        initLogger()
        initIgnite()
        initHttpClient()
        startApp()
        destroyHttpClient()
        destroyIgnite()
    }
}