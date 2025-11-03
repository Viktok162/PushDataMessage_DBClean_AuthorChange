package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

private val empty = Post (
    id = 0,
    author = "Writer",  // value
    content = "text2",
    published = "time2",
    likeByMe = false
)

class PostViewModel(application: Application): AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun like(id: Long) = repository.like(id)
    fun share(id: Long) = repository.share(id)
    fun look(id: Long) = repository.look(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun changeContent(content: String) {
        val text = content.trim()
        edited.value?.let {
            if (text == it.content) {
                return@let
            }
            edited.value = it.copy(content = text)
        }
    }

//      fun save() {
//        edited.value?.let {
//            repository.save(it)
//          }
//          edited.value = empty
//      }

//      fun save() {
//        edited.value?.let { post ->
//            if (post.content.isNotBlank()) {
//                repository.save(post)
//                edited.value = empty
//            }
//        }
//      }
        fun save() {
            edited.value?.let { post ->
                if (post.content.isBlank()) {
                    // Контент не может быть пустым
                } else {
                    repository.save(post)
                }
                // Всегда сбрасывать после попытки (даже если не сохранили)
                edited.value = empty
            }
        }


    fun edit(post:Post){
        edited.value = post
    }

//    fun getPostById(id: Long): Post? {
//        return data.value?.find { it.id == id }
//    }

    fun changeAuthor(author: String) {
        val newAuthor = author.trim()
        edited.value?.let {
            if (newAuthor == it.author) {
                return@let
            }
            edited.value = it.copy(author = newAuthor)
        }
    }
}
