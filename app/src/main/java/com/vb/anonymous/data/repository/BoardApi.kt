package com.vb.anonymous.data.repository

import com.vb.anonymous.data.model.BoardsResponse
import com.vb.anonymous.data.model.ThreadPostsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BoardApi {

    @GET("boards.json")
    suspend fun getBoards(): BoardsResponse

    @GET("{board}/res/{threadId}.json")
    suspend fun getThreadPosts(
        @Path("board") board: String,
        @Path("threadId") threadId: String
    ): ThreadPostsResponse
}