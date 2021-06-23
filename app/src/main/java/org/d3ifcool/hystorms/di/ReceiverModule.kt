package org.d3ifcool.hystorms.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.d3ifcool.hystorms.receiver.ScheduleAlarmReceiver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReceiverModule {
    @Singleton
    @Provides
    fun provideScheduleReceiver(): ScheduleAlarmReceiver = ScheduleAlarmReceiver()
}