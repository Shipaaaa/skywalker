package data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by v.shipugin on 18/11/2018
 */
data class PredictResponse(
    @SerializedName("compression_type")
    val compressionType: String
)