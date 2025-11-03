package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "Post_Entity")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("author")
    val author: String ="author3",
    @ColumnInfo("published")
    val published: String ="time3",
    val content: String ="text3",
    val likes: Int = 119,
    val shares: Int = 228,
    val looks: Int = 337,
    val likeByMe: Boolean = false,
    val video: String? = null
) {

    fun toDto() = Post(
        id = id,
        //author = author,
        author = if (author.isEmpty()) "no_author" else author,  // connection to Post.kt
        published = published,
        content = content,
        likes = likes,
        shares = shares,
        looks = looks,
        likeByMe = likeByMe,
        video = video
    )
// var 1   PostRepositoryImpl, appropriate for ROOM
    companion object {
        fun fromDto(post: Post) = post.run {
            PostEntity(
                id = id,
                author = author,
                published = published,
                content = content,
                likes = likes,
                shares = shares,
                looks = looks,
                likeByMe = likeByMe,
                video = video
            )
        }
    }
}
// var 2    PostRepositoryImpl Extension fun, appropriate for Kotlin
fun Post.toEntity() = PostEntity(
    id = id,
    author = author,
    // published = published,
    published = if (id == 0L) {  // Только для новых постов (id=0) генерируем время
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    } else {
        published  // Для обновлений оставляем старое время
    },
    content = content,
    likes = likes,
    shares = shares,
    looks = looks,
    likeByMe = likeByMe,
    video = video
)
