package org.d3ifcool.hystorms.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.d3ifcool.hystorms.network.WeatherRetrofit
import org.d3ifcool.hystorms.util.WeatherRetrofitBuilder
import org.d3ifcool.hystorms.util.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideMoshiBuilder(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @WeatherRetrofitBuilder
    @Singleton
    @Provides
    fun provideWeatherRetrofit(moshi: Moshi): Retrofit.Builder {
        return Retrofit.Builder().baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
    }

    @WeatherService
    @Singleton
    @Provides
    fun provideWeatherService(
        @WeatherRetrofitBuilder retrofit: Retrofit.Builder
    ): WeatherRetrofit {
        return retrofit.build().create(WeatherRetrofit::class.java)
    }
}