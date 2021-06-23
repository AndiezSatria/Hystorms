package org.d3ifcool.hystorms.model

import java.io.Serializable

data class SensorPhysic(
    var id: Int = 1,
    @field:JvmField var isError: Boolean = false,
    var name: String = "",
) : Serializable {
    override fun toString(): String {
        return "Kondisi $name: " +
                if (isError) "Error, Tidak Mengembalikan Nilai."
                else "Baik, Sensor berfungsi dengan baik."
    }
}
