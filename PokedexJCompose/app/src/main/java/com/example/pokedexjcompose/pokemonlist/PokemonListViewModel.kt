package com.example.pokedexjcompose.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import com.example.pokedexjcompose.models.PokemonListEntry
import com.example.pokedexjcompose.repository.PokemonRepository
import com.example.pokedexjcompose.util.Const.PAGE_SIZE
import com.example.pokedexjcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {
    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokemonListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)
    private  var cachedPokemonList= listOf<PokemonListEntry>()//arama yaparken aranan yoksa cache deki verileri göstereceğiz
    private  var isSearchStarting= true
    var isSearching= mutableStateOf(false)
    fun searchPokemon(query: String)
    {
        val listToSearch=if(isSearchStarting)
        {
            pokemonList.value
        }
        else
        {
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty())
            {
                pokemonList.value=cachedPokemonList
                isSearching.value=false
                isSearchStarting=true
                return@launch
            }
            val result =listToSearch.filter {
                it.pokemonName.contains(query.trim(),ignoreCase = true) ||
                        it.number.toString()==query.trim()//pokemon numarasına göre arama yapmamızı saglar
            }
            if (isSearchStarting)
            {
                cachedPokemonList=pokemonList.value
                isSearchStarting=false

            }
            pokemonList.value=result
            isSearching.value=true

        }

    }

    init {
        loadPokemonPaginated()
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
            when(result) {
                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if(entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"//pokemonların endpointe gelen numaraları almak için yapılan işlem bölgesi sonucunda fotoların url ulasıyoruz
                        PokemonListEntry(entry.name.capitalize(Locale.ROOT), url, number.toInt())
                    }
                    curPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
            }
        }
    }



    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}