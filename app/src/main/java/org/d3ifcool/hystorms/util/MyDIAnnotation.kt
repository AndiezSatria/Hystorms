package org.d3ifcool.hystorms.util

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProfilePicture

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DevicesReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SensorPhysicReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TanksReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ScheduleReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlantReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NutritionReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HistoryReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherRetrofitBuilder

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AddressRetrofitBuilder

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AddressService