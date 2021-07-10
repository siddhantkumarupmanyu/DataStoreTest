package com.example.datastore.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datastore.repository.Repository
import com.example.datastore.vo.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User> = _user

    fun initUser(user: User) {
        viewModelScope.launch {
            repository.getUserDetails(user).collectLatest {
                _user.value = it
            }
        }
    }

    fun generateMessages() {
        viewModelScope.launch {
            user.value?.let {
                repository.generateMessage(it)
            }
        }
    }
}
