package com.example.pokedexjcompose.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.pokedexjcompose.data.remote.responses.Pokemon
import com.example.pokedexjcompose.repository.PokemonRepository
import com.example.pokedexjcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
)    :ViewModel() {

    suspend fun getPokemonInfo(pokemonName:String):Resource<Pokemon>
    {
        return repository.getPokemonInfo(pokemonName )
    }
}