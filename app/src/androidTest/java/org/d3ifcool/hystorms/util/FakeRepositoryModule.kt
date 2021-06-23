package org.d3ifcool.hystorms.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.d3ifcool.hystorms.repository.FakeAuthRepositoryImpl
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepository
import javax.inject.Singleton

/*
    FakeAuthRepositoryImpl digunakan untuk mengganti module yang ada di production code
    dengan data palsu agar data untuk test tidak tersimpan ke firebase
    Comment semua anotasi pada saat menjalankan test yang menggunakan module asli dari production code
    (Ingin mendapatkan error dari firebase atau API)
 */
//@Module
//@InstallIn(SingletonComponent::class)
object FakeRepositoryModule {
//    @Singleton
//    @Provides
    fun provideAuthFakeRepository(): AuthenticationRepository = FakeAuthRepositoryImpl()
}