package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.data.local.database.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    // Estado interno: Lista de posts
    private val _posts = MutableStateFlow<List<PostEntity>>(emptyList())
    // Estado público: Lo que ve la pantalla (es de solo lectura para proteger los datos)
    val posts: StateFlow<List<PostEntity>> = _posts.asStateFlow()

    init {
        // Apenas nace el ViewModel, empezamos a escuchar los cambios en la base de datos
        viewModelScope.launch {
            repository.allPosts.collect { listaActualizada ->
                _posts.value = listaActualizada
            }
        }
    }

    // Acción: El usuario escribió algo y le dio a "Publicar"
    fun agregarPost(contenido: String, autorId: Int) {
        viewModelScope.launch {
            val nuevoPost = PostEntity(
                authorId = autorId,
                content = contenido,
                title = "Sin Título" // O puedes pedir título también
            )
            repository.createPost(nuevoPost)
        }
    }

    // Acción: El usuario dio like
    fun darLike(postId: Int) {
        viewModelScope.launch {
            repository.likePost(postId)
        }
    }
}