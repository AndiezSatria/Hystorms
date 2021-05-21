package org.d3ifcool.hystorms.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.d3ifcool.hystorms.db.weather.WeatherCacheMapper
import org.d3ifcool.hystorms.db.weather.WeatherDao
import org.d3ifcool.hystorms.network.WeatherNetworkMapper
import org.d3ifcool.hystorms.network.WeatherRetrofit
import org.d3ifcool.hystorms.repository.AuthRepository
import org.d3ifcool.hystorms.repository.HomeRepository
import org.d3ifcool.hystorms.repository.SplashRepository
import org.d3ifcool.hystorms.util.ProfilePicture
import org.d3ifcool.hystorms.util.UserReference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        @UserReference userRef: CollectionReference,
        storageReference: FirebaseStorage
    ): AuthRepository {
        return AuthRepository(firebaseAuth, userRef, storageReference)
    }

    @Singleton
    @Provides
    fun provideSplashRepository(
        firebaseAuth: FirebaseAuth,
        @UserReference userRef: CollectionReference
    ): SplashRepository = SplashRepository(firebaseAuth, userRef)

    @Singleton
    @Provides
    fun provideHomeRepository(
        weatherService: WeatherRetrofit,
        weatherDao: WeatherDao,
        networkMapper: WeatherNetworkMapper,
        cacheMapper: WeatherCacheMapper
    ): HomeRepository = HomeRepository(weatherService, weatherDao, networkMapper, cacheMapper)
}