package com.example.datastore.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datastore.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()

    val loginResult: LiveData<Boolean> = _loginResult

    fun login(username: String, password: String) {
        // viewModelScope.launch {
        //     _loginResult.value = repository.login(username, password)
        // }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            repository.register(username, password)
        }
    }

}