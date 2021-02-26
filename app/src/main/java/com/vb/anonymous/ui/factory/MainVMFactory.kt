package com.vb.anonymous.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vb.anonymous.domain.repository.BoardRepo

class MainVMFactory(private val boardApi: BoardRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        modelClass.getConstructor(BoardRepo::class.java).newInstance(boardApi)
}