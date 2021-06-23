package org.d3ifcool.hystorms.constant

import org.d3ifcool.hystorms.model.DayPicker

class Constant {
    companion object{
        const val PROFILE_PICTURE: String = "images/profile/"
        const val APP_DEBUG: String = "hystorms_debug"
        const val USERS: String = "users"
        const val DEVICES: String = "devices"
        const val SENSOR_PHYSIC: String = "sensor_physics"
        const val TANKS: String = "tanks"
        const val TANK: String = "images/tank/"
        const val SCHEDULE: String = "schedules"
        const val PLANTS: String = "plants"
        const val PLANT: String = "images/plant/"
        const val NUTRITION: String = "nutrition"
        const val NUTRITION_IMAGE: String = "images/nutrition/"
        const val HISTORIES: String = "histories"
        const val USER: String = "user_firebase"
        const val RC_PROFILE_IMAGE = 101
        val DAY_PICKER_DATA = listOf(
            DayPicker(0, "Minggu"),
            DayPicker(1, "Senin"),
            DayPicker(2, "Selasa"),
            DayPicker(3, "Rabu"),
            DayPicker(4, "Kamis"),
            DayPicker(5, "Jumat"),
            DayPicker(6, "Sabtu"),
        )
    }
}