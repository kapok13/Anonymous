package com.vb.anonymous.domain.repository

import com.vb.anonymous.domain.model.Boards
import com.vb.anonymous.domain.model.PostsCount
import kotlinx.coroutines.flow.Flow

interface BoardRepo {
    suspend fun getBoards(): MutableList<Boards>
    suspend fun getPosts(board: String, threadId: String): Flow<PostsCount>?
}