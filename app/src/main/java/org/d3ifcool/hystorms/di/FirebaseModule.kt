package org.d3ifcool.hystorms.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.d3ifcool.hystorms.data.Constant
import org.d3ifcool.hystorms.util.ProfilePicture
import org.d3ifcool.hystorms.util.UserReference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    // Authentication
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseUser(firebaseAuth: FirebaseAuth): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Storage
    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    @ProfilePicture
    fun provideProfilePicFolder(storage: FirebaseStorage): StorageReference {
        return storage.reference.child(Constant.PROFILE_PICTURE)
    }

    // Firestore
    @Singleton
    @Provides
    fun provideFirestoreRootReference(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @UserReference
    @Singleton
    @Provides
    fun provideUsersReference(rootRef: FirebaseFirestore): CollectionReference {
        return rootRef.collection(Constant.USERS)
    }
}