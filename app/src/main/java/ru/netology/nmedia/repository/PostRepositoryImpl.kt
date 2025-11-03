package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.toEntity
import android.util.Log

class PostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {

//    override fun getAll(): LiveData<List<Post>> {
//        return dao.getAll().map { list ->
//            list.map {
//                it.toDto()
//            }
//        }
//    }
    override fun getAll(): LiveData<List<Post>> {
        return dao.getAll().map { entityList ->
            // Здесь добавляем лог размера списка перед маппингом в DTO
            Log.d("DB_Debug", "Retrieved posts count: ${entityList.size}")
            entityList.map { it.toDto() }
        }
    }


    override fun like(id: Long) {
        dao.likeById(id)
    }

    override fun share(id: Long) {
        dao.share(id)
    }

    override fun look(id: Long) {
        dao.look(id)
    }

    override fun save(post: Post) {
//        dao.save(PostEntity.fromDto(post))  var 1 PostEntity.kt

        dao.save(post.toEntity())  // var 2 PostEntity.kt
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}
