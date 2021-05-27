package org.d3ifcool.hystorms.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Tank(
    var id: String = "",
    var name: String = "",
    var photoUrl: String? = null,
    var currentLuminous: Double = 0.0,
    var currentTemp: Double = 273.0,
    var currentHumidity: Double = 0.0,
    var owner: String = "",
    var device: String = "",
    @field:JvmField var isAuthorized: Boolean = false,
    var createdAt: Date = Calendar.getInstance().time,
    var plantedAt: Date = Calendar.getInstance().time,
    @ServerTimestamp var timeStamp: Date = Calendar.getInstance().time
) : Parcelable
