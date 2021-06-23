package org.d3ifcool.hystorms.util

import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.d3ifcool.hystorms.di.RepositoryModule

//@Module
//@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
object ReplaceRepositoryModule {
}