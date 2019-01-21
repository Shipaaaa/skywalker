/**
 * Created by v.shipugin on 2019-01-20
 */
object Configurations {

    // TODO переключить на false после релиза
    val ENABLE_LOGGING =
        System.getenv("SKYWALKER_CLIENT_ENABLE_LOGGING")?.toBoolean() ?: true
}

