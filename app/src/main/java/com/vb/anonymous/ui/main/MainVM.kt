package com.vb.anonymous.ui.main

import androidx.lifecycle.*
import com.vb.anonymous.domain.model.PostsCount
import com.vb.anonymous.domain.repository.BoardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainVM(private val boardRepo: BoardRepo) : ViewModel() {

    var totalThreadMessages = 0
    var currentUrl = ""
    var currentThreadObserver: LiveData<PostsCount>? = null

    fun getBoards(): MutableLiveData<MutableList<String>> {
        val boardsLiveData = MutableLiveData<MutableList<String>>()
        viewModelScope.launch(Dispatchers.IO) {
            val boardsList = mutableListOf<String>()
            boardRepo.getBoards().forEach { boardsList.add(it.name) }
            withContext(Dispatchers.Main) { boardsLiveData.value = boardsList }
        }
        return boardsLiveData
    }

    fun getThreadPosts(board: String, threadId: String) = liveData {
        if (boardRepo.getPosts(board, threadId) != null) {
            emitSource(boardRepo.getPosts(board, threadId)!!.asLiveData(Dispatchers.IO, 20000L))
        }
    }
}