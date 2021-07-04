package org.d3ifcool.hystorms.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.d3ifcool.hystorms.db.weather.WeatherCacheMapper
import org.d3ifcool.hystorms.db.weather.WeatherDao
import org.d3ifcool.hystorms.network.WeatherNetworkMapper
import org.d3ifcool.hystorms.network.WeatherRetrofit
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepository
import org.d3ifcool.hystorms.repository.device.DevicesRepositoryImpl
import org.d3ifcool.hystorms.repository.home.HomeRepositoryImpl
import org.d3ifcool.hystorms.repository.splash.SplashRepositoryImpl
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepositoryImpl
import org.d3ifcool.hystorms.repository.device.DetailDeviceRepository
import org.d3ifcool.hystorms.repository.device.DetailDeviceRepositoryImpl
import org.d3ifcool.hystorms.repository.device.DevicesRepository
import org.d3ifcool.hystorms.repository.encyclopedia.AddNutritionRepository
import org.d3ifcool.hystorms.repository.encyclopedia.AddNutritionRepositoryImpl
import org.d3ifcool.hystorms.repository.encyclopedia.EncyclopediaRepository
import org.d3ifcool.hystorms.repository.encyclopedia.EncyclopediaRepositoryImpl
import org.d3ifcool.hystorms.repository.home.HomeRepository
import org.d3ifcool.hystorms.repository.setting.*
import org.d3ifcool.hystorms.repository.tank.*
import org.d3ifcool.hystorms.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAuthenticationRepository(
        firebaseAuth: FirebaseAuth,
        @UserReference userRef: CollectionReference,
        storageReference: FirebaseStorage
    ): AuthenticationRepository {
        return AuthenticationRepositoryImpl(firebaseAuth, userRef, storageReference)
    }

    @Singleton
    @Provides
    fun provideSplashRepository(
        firebaseAuth: FirebaseAuth,
        @UserReference userRef: CollectionReference
    ): SplashRepositoryImpl = SplashRepositoryImpl(firebaseAuth, userRef)

    @Singleton
    @Provides
    fun provideHomeRepository(
        @WeatherService weatherService: WeatherRetrofit,
        weatherDao: WeatherDao,
        networkMapper: WeatherNetworkMapper,
        cacheMapper: WeatherCacheMapper,
        @TanksReference tankReference: CollectionReference,
        @DevicesReference deviceRef: CollectionReference,
        @ScheduleReference scheduleRef: CollectionReference
    ): HomeRepository =
        HomeRepositoryImpl(
            weatherService,
            weatherDao,
            networkMapper,
            cacheMapper,
            tankReference,
            deviceRef,
            scheduleRef
        )

    @Singleton
    @Provides
    fun provideDevicesRepository(
        @DevicesReference deviceRef: CollectionReference,
        @TanksReference tankRef: CollectionReference
    ): DevicesRepository = DevicesRepositoryImpl(deviceRef, tankRef)

    @Singleton
    @Provides
    fun provideDetailDeviceRepository(
        @TanksReference tanksReference: CollectionReference,
        @SensorPhysicReference physicReference: CollectionReference,
        @UserReference userRef: CollectionReference,
        @DevicesReference deviceRef: CollectionReference,
        @WeatherService weatherService: WeatherRetrofit,
        weatherDao: WeatherDao,
        networkMapper: WeatherNetworkMapper,
        cacheMapper: WeatherCacheMapper
    ): DetailDeviceRepository =
        DetailDeviceRepositoryImpl(
            tanksReference,
            physicReference,
            userRef,
            deviceRef,
            weatherService,
            weatherDao,
            networkMapper,
            cacheMapper
        )

    @Singleton
    @Provides
    fun provideDetailTankRepository(
        @PlantReference plantReference: CollectionReference,
        @TanksReference tankReference: CollectionReference,
        @ScheduleReference scheduleRef: CollectionReference
    ): DetailTankRepository =
        DetailTankRepositoryImpl(plantReference,tankReference, scheduleRef)

    @Singleton
    @Provides
    fun provideEncyclopediaRepository(
        @PlantReference plantReference: CollectionReference,
        @NutritionReference nutritionReference: CollectionReference
    ): EncyclopediaRepository =
        EncyclopediaRepositoryImpl(plantReference, nutritionReference)

    @Singleton
    @Provides
    fun provideSettingRepository(
        @UserReference userReference: CollectionReference,
        firebaseAuth: FirebaseAuth
    ): SettingRepository =
        SettingRepositoryImpl(userReference, firebaseAuth)

    @Singleton
    @Provides
    fun provideEditProfileRepository(
        storageReference: FirebaseStorage,
        @UserReference userReference: CollectionReference
    ): EditProfileRepository =
        EditProfileRepositoryImpl(userReference, storageReference)

    @Singleton
    @Provides
    fun provideChangePasswordRepository(
        firebaseAuth: FirebaseAuth
    ): ChangePasswordRepository =
        ChangePasswordRepositoryImpl(firebaseAuth)

    @Singleton
    @Provides
    fun provideAddNutritionRepository(
        firebaseStorage: FirebaseStorage,
        @NutritionReference nutritionReference: CollectionReference
    ): AddNutritionRepository = AddNutritionRepositoryImpl(firebaseStorage, nutritionReference)

    @Singleton
    @Provides
    fun provideAddScheduleRepository(
        @ScheduleReference scheduleRef: CollectionReference
    ): AddScheduleRepository = AddScheduleRepositoryImpl(scheduleRef)

    @Singleton
    @Provides
    fun provideHistoryRepository(
        @HistoryReference historyRef: CollectionReference
    ): HistoryRepository = HistoryRepositoryImpl(historyRef)

    @Singleton
    @Provides
    fun provideEditTankRepository(
        @TanksReference tankRef: CollectionReference,
        firebaseStorage: FirebaseStorage
    ): EditTankRepository = EditTankRepositoryImpl(tankRef, firebaseStorage)
}