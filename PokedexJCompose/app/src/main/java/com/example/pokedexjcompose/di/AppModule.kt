package com.example.pokedexjcompose.di

import com.example.pokedexjcompose.api.PokemonApi
import com.example.pokedexjcompose.repository.PokemonRepository
import com.example.pokedexjcompose.util.Const.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokemonApi
    ) = PokemonRepository(api)

    @Singleton
    @Provides
    fun providePokeApi(): PokemonApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokemonApi::class.java)
    }
}