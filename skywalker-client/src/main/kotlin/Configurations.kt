/**
 * Created by v.shipugin on 2019-01-20
 */
object Configurations {

    // TODO удалить после релиза
    const val ENABLE_FILE_CONTENT_LOGGING = true
    // TODO переключить на false после релиза
    val ENABLE_LOGGING =
        System.getenv("SKYWALKER_CLIENT_ENABLE_LOGGING")?.toBoolean() ?: true


    val ENABLE_REST_API =
        System.getenv("SKYWALKER_CLIENT_ENABLE_REST_API")?.toBoolean() ?: true
    val UPLOAD_DIR =
        System.getenv("SKYWALKER_CLIENT_UPLOAD_DIR") ?: "/Users/v.shipugin/Documents/GitHub/skywalker/testFiles2"


    val SIZE_OF_SAMPLE_IN_KB =
        System.getenv("SKYWALKER_CLIENT_PREDICTION_SIZE_OF_SAMPLE_IN_KB")?.toIntOrNull() ?: 750
    val COUNT_OF_BLOCKS_FOR_SAMPLE =
        System.getenv("SKYWALKER_CLIENT_PREDICTION_COUNT_OF_BLOCKS_FOR_SAMPLE")?.toIntOrNull() ?: 30
    val ENABLE_COUNT_OF_BLOCKS =
        System.getenv("SKYWALKER_CLIENT_PREDICTION_ENABLE_COUNT_OF_BLOCKS")?.toBoolean() ?: true
    val SIZE_OF_BLOCKS_IN_KB =
        System.getenv("SKYWALKER_CLIENT_PREDICTION_SIZE_OF_BLOCKS_IN_KB")?.toIntOrNull() ?: 25


    val PREDICTION_SERVICE_URL = System.getenv("SKYWALKER_CLIENT_PREDICTION_SERVICE_URL")
        ?: "http://localhost/skywalker/predict"
}

