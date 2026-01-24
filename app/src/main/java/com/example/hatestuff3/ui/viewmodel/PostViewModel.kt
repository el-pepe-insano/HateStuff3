package com.example.hatestuff3.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.data.local.database.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostEntity>>(emptyList())
    val posts: StateFlow<List<PostEntity>> = _posts.asStateFlow()

    init {
        // Al usar .collect sobre el Flow del repositorio, la lista se actualiza
        // automáticamente cada vez que alguien publica algo nuevo.
        viewModelScope.launch {
            repository.allPosts.collect { listaActualizada ->
                _posts.value = listaActualizada
            }
        }
    }

    /**
     * Función para publicar posts híbridos (Texto + Foto opcional)
     * Coincide con la llamada desde AppNavGraph.
     */
    fun submitPost(content: String, uri: Uri?) { // El parámetro se llama 'content'
        viewModelScope.launch {
            val nuevoPost = PostEntity(
                authorId = 1,
                authorName = "Usuario",
                content = content,          // CAMBIO: Antes decía 'contenido', ahora 'content'
                imageUri = uri?.toString()
            )
            repository.createPost(nuevoPost)
        }
    }

    // Mantengo tu lógica de likes
    fun darLike(postId: Int) {
        viewModelScope.launch {
            repository.likePost(postId)
        }
    }
}