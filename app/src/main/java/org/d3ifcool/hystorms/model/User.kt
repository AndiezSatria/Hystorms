package org.d3ifcool.hystorms.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var photoUrl: String? = null,
    val createdAt: Date = Calendar.getInstance().time,
    @ServerTimestamp var modifiedAt: Date = Calendar.getInstance().time
) : Serializable
