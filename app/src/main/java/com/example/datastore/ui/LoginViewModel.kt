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

    // What should login return??
    // Is it a suspend function?? - cannot guess it right now; after implementing DataStore
    // liveData? flow?
    // In my opinion currently it returns boolean; we will add complexity as needed (after implementing datastore)
    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(username, password)
            _loginResult.value = result
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            repository.register(username, password)
        }
    }

}