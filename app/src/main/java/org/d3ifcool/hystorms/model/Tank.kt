package org.d3ifcool.hystorms.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Tank(
    var id: String = "",
    var name: String = "",
    var photoUrl: String? = null,
    var sensorData: List<DataSensor> = listOf(),
    var amount: Int = 0,
    var owner: String = "",
    var device: String = "",
    var plant: String = "",
    var history: String = "",
    @field:JvmField var isAuthorized: Boolean = false,
    var createdAt: Date = Calendar.getInstance().time,
    var plantedAt: Date = Calendar.getInstance().time,
    @ServerTimestamp var timeStamp: Date = Calendar.getInstance().time
) : Serializable {
    override fun toString(): String {
        var string = "Tanki $name, jumlah $amount tanaman."
        sensorData.forEach { string += "\n${it}" }
        return string
    }
}
