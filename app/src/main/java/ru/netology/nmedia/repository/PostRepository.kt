package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun like(id: Long)
    fun share(id: Long)
    fun look(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
}
