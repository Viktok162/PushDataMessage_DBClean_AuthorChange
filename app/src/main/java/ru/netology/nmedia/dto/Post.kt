package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String ="author1",
    val published: String ="time1",
    val content: String ="text1",
    val likes: Int = 999,  //  value
    val shares: Int = 998, //  value
    val looks: Int = 997,  //  value
    val likeByMe: Boolean = false,
    val video: String? = null
)
