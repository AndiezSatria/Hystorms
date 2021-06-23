package org.d3ifcool.hystorms.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.d3ifcool.hystorms.ui.auth.AuthFragmentFactory
import org.d3ifcool.hystorms.ui.main.MainFragmentFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FragmentModule {

    @Provides
    @Singleton
    fun provideAuthFragmentFactory(): AuthFragmentFactory {
        return AuthFragmentFactory()
    }

    @Provides
    @Singleton
    fun provideMainFragmentFactory(): MainFragmentFactory {
        return MainFragmentFactory()
    }
}