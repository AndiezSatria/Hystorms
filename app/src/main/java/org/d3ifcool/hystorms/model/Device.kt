package org.d3ifcool.hystorms.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Device(
    var id: String = "",
    var name: String = "",
    var deviceName: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var macAddress: String = "",
    var owner: String = "",
    var address: String = "",
    @field:JvmField var isAuthorized: Boolean = false,
    var conditions: String = "",
    var createdAt: Date = Calendar.getInstance().time,
    @ServerTimestamp var timeStamp: Date = Calendar.getInstance().time
) : Parcelable