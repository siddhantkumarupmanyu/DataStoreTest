package com.example.datastore.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.datastore.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {


    private val _messages = MutableLiveData(2)

    val messages: LiveData<Int> = _messages

    fun generateMessages() {
        // TODo:
        // _messages.value = repository.generateMessages()
    }
}
