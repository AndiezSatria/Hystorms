package org.d3ifcool.hystorms.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var photoUrl: String? = null,
    var createdAt: Date = Calendar.getInstance().time,
    var favoriteDevice: String? = null,
    @ServerTimestamp var modifiedAt: Date = Calendar.getInstance().time
) : Parcelable
