package org.d3ifcool.hystorms.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.d3ifcool.hystorms.repository.AuthRepository
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
        @ProfilePicture profilePicRef: StorageReference
    ): AuthRepository {
        return AuthRepository(firebaseAuth, userRef, profilePicRef)
    }
}