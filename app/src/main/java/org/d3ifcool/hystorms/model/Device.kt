package org.d3ifcool.hystorms.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Device(
    var id: String = "",
    var name: String = "",
    var deviceName: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var macAddress: String = "",
    var owner: String? = "",
    var address: String = "",
    @field:JvmField var isAuthorized: Boolean = false,
    var conditions: String = "",
    var createdAt: Date = Calendar.getInstance().time,
    @ServerTimestamp var timeStamp: Date = Calendar.getInstance().time
) : Serializable {
    override fun toString(): String {
        return "Informasi alat $name / $macAddress, dengan lokasi $address, Latitude $latitude, Longitude $longitude."
    }
}