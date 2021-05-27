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