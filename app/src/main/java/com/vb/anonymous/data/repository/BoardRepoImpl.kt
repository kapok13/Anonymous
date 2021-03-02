package com.vb.anonymous.data.repository

import android.util.Log
import com.vb.anonymous.domain.model.Boards
import com.vb.anonymous.domain.model.PostsCount
import com.vb.anonymous.domain.repository.BoardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BoardRepoImpl(private val boardApi: BoardApi) : BoardRepo {

    override suspend fun getBoards(): MutableList<Boards> {
        val boardsList = mutableListOf<Boards>()
        try {
            boardApi.getBoards().boards.forEach {
                boardsList.add(Boards(it.id))
            }
        } catch (e: Exception) {
            Log.e("BoardRepoImpl", "board call exception $e")
        }
        return suspendCoroutine { it.resume(boardsList) }
    }

    override suspend fun getPosts(board: String, threadId: String): Flow<PostsCount>? {
        return try {
            flow {
                while (true) {
                    emit(PostsCount(boardApi.getThreadPosts(board, threadId).posts_count))
                    kotlinx.coroutines.delay(10000)
                }
            }.flowOn(Dispatchers.IO)
        } catch (e: Exception) {
            Log.e("BoardRepoImpl", "thread call exception $e")
            null
        }
    }


}